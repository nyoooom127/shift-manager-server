package assignsShifts.entities.user.entity;

import assignsShifts.entities.constraint.entity.Constraint;
import assignsShifts.entities.shift.entity.Shift;
import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.entities.user.type.UserType;
import assignsShifts.models.Model;
import assignsShifts.models.enums.UserPermissionsEnum;
import assignsShifts.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static assignsShifts.utils.DateUtil.isDateInRange;

@Data
@Document("users")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class User extends Model implements Cloneable {
  private String fullName;
  @DBRef private List<UserType> types;
  private Map<String, Integer> numShifts;
  private Map<String, Integer> numWeekendShifts;
  Map<String, Double> initialScores;
  private AuthorizationData authorizationData;
  @DBRef private List<Constraint> constraints;
  @DBRef private List<Shift> shifts;
  private boolean active;

  @JsonProperty("isQualified")
  private boolean isQualified;

  private boolean avoidNight;
  private boolean avoidWeekend;

  public User(
      String id,
      @NonNull String fullName,
      List<UserType> types,
      Map<String, Integer> numShifts,
      Map<String, Integer> numWeekendShifts,
      Map<String, Double> initialScores,
      AuthorizationData authorizationData,
      @NonNull List<Constraint> constraints,
      @NonNull List<Shift> shifts,
      boolean active,
      boolean isQualified,
      boolean avoidNight,
      boolean avoidWeekend) {
    super(id);
    this.fullName = fullName;
    this.types = types;
    this.numShifts = numShifts;
    this.numWeekendShifts = numWeekendShifts;
    this.initialScores = initialScores;
    this.authorizationData = authorizationData;
    this.constraints = constraints;
    this.shifts = shifts;
    this.active = active;
    this.isQualified = isQualified;
    this.avoidNight = avoidNight;
    this.avoidWeekend = avoidWeekend;
  }

  public User hideAuthData() {
    return this; // .toBuilder().authorizationData(null).build();
  }

  public void addShift(Shift shiftToAdd) {
    this.shifts.add(shiftToAdd);

    if (DateUtil.isWeekend(shiftToAdd.getStartDate())) {
      Integer currentNum = this.numWeekendShifts.getOrDefault(shiftToAdd.getType().getId(), 0);
      this.numWeekendShifts.put(shiftToAdd.getType().getId(), currentNum + 1);
    } else {
      Integer currentNum = this.numShifts.getOrDefault(shiftToAdd.getType().getId(), 0);
      this.numShifts.put(shiftToAdd.getType().getId(), currentNum + 1);
    }
  }

  public void removeShift(Shift shiftToRemove) {
    boolean removed =
        this.getShifts().removeIf(shift -> shift.getId().equals(shiftToRemove.getId()));

    if (!removed) {
      return;
    }

    if (DateUtil.isWeekend(shiftToRemove.getStartDate())) {
      Integer currentNum = this.numWeekendShifts.getOrDefault(shiftToRemove.getType().getId(), 1);
      this.numWeekendShifts.put(shiftToRemove.getType().getId(), currentNum - 1);
    } else {
      Integer currentNum = this.numShifts.getOrDefault(shiftToRemove.getType().getId(), 1);
      this.numShifts.put(shiftToRemove.getType().getId(), currentNum - 1);
    }
  }

  public List<Constraint> getConstraints() {
    if (this.constraints == null) {
      this.constraints = new ArrayList<>();
    }

    return this.constraints;
  }

  public boolean isEnoughDaysSinceLastShift(ShiftType shiftType, Calendar startDate) {
    Optional<Shift> mostRecentShift = getMostRecentShift(shiftType, startDate);

    if (mostRecentShift.isEmpty()) {
      return true;
    }

    return Math.abs(
            Duration.between(
                    mostRecentShift.get().getStartDate().toInstant(), startDate.toInstant())
                .toDays())
        > shiftType.getMinBreak();
  }

  public boolean isEnoughDaysUntilNextShift(ShiftType shiftType, Calendar startDate) {
    Optional<Shift> nextUpcomingShift = getClosestUpcomingShift(shiftType, startDate);

    if (nextUpcomingShift.isEmpty()) {
      return true;
    }

    return Math.abs(
            Duration.between(
                    nextUpcomingShift.get().getStartDate().toInstant(), startDate.toInstant())
                .toDays())
        > shiftType.getMinBreak();
  }

  public Optional<Shift> getMostRecentShift(ShiftType shiftType, Calendar date) {
    return this.getShifts().stream()
        .filter(shift -> shift.getStartDate().compareTo(date.getTime()) <= 0)
        //        .filter(shift -> shiftType.equals(shift.getType()))
        .max(Shift::compareByDate);
  }

  public Optional<Shift> getClosestUpcomingShift(ShiftType shiftType, Calendar date) {
    return this.getShifts().stream()
        .filter(shift -> shift.getStartDate().compareTo(date.getTime()) >= 0)
        //        .filter(shift -> shiftType.equals(shift.getType()))
        .min(Shift::compareByDate);
  }

  public List<UserType> getOverlappingTypes(ShiftType shiftType) {
    return this.getTypes().stream()
        .filter(userType -> shiftType.getAllowedUserTypeIds().contains(userType.getId()))
        .collect(Collectors.toList());
  }

  public boolean isHaveConstraint(Calendar shiftStart, int shiftStartHour, double shiftDuration) {
    Calendar shiftStartHourCal = ((Calendar) shiftStart.clone());
    shiftStartHourCal.set(Calendar.HOUR_OF_DAY, shiftStartHour);
    Calendar shiftEnd = (Calendar) shiftStartHourCal.clone();
    shiftEnd.add(Calendar.HOUR_OF_DAY, (int) Math.round(shiftDuration));

    for (Constraint constraint : this.getConstraints()) {
      Calendar constraintStart = Calendar.getInstance();
      constraintStart.setTime(constraint.getStartDate());
      Calendar constraintEnd = Calendar.getInstance();
      constraintEnd.setTime(constraint.getEndDate());

      if (isDateInRange(constraintStart, constraintEnd, shiftStart, shiftEnd)) {
        return true;
      }
    }

    return false;
  }

  public int getNumUpcomingConstraints(Calendar start) {
    Calendar end = (Calendar) start.clone();
    end.add(Calendar.DATE, 7);

    List<Constraint> upcomingConstraints =
        this.getConstraints().stream()
            .filter(
                constraint -> {
                  Calendar constraintStart = Calendar.getInstance();
                  constraintStart.setTime(constraint.getStartDate());
                  Calendar constraintEnd = Calendar.getInstance();
                  constraintEnd.setTime(constraint.getEndDate());

                  return isDateInRange(constraintStart, constraintEnd, start, end);
                })
            .toList();

    return upcomingConstraints.size();
  }

  public double getShiftScore(ShiftType shiftType) {
    return getShiftScore(shiftType, false);
  }

  public double getShiftScore(ShiftType shiftType, boolean strict) {
    if (strict) {
      return getShiftTypeScore(shiftType);
    }

    List<UserType> userTypes = getOverlappingTypes(shiftType);

    List<ShiftType> shiftTypes =
        userTypes.stream()
            .flatMap(userType -> userType.getAllowedShiftTypes().stream())
            .distinct()
            .toList();

    return shiftTypes.stream().mapToDouble(this::getShiftTypeScore).sum();
  }

  private double getShiftTypeScore(ShiftType shiftType) {
    return Optional.ofNullable(getInitialScores())
            .map(initScores -> initScores.getOrDefault(shiftType.getId(), 0.0))
            .orElse(0.0)
        + getNumShifts().getOrDefault(shiftType.getId(), 0) * shiftType.getScore()
        + getNumWeekendShifts().getOrDefault(shiftType.getId(), 0) * shiftType.getWeekendScore();
  }

  public User cloneWithoutLists() {
    //    User t = (User) clone();
    User clone =
        User.builder()
            .fullName(getFullName())
            .authorizationData(getAuthorizationData())
            .active(isActive())
            .isQualified(isQualified())
            .avoidNight(isAvoidNight())
            .build();
    // todo - find way to add to builder.
    clone.setId(getId()); // Separate because it's a member of superclass Model

    return clone;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return id.equals(user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public User clone() {
    try {
      User clone = (User) super.clone();
      // TODO: copy mutable state here, so the clone can't change the internals of the original
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

  @Data
  @AllArgsConstructor
  public static class AuthorizationData {
    @NonNull private String username;
    private String password;
    @NonNull private String email;
    @NonNull private String phone;
    @NonNull private UserPermissionsEnum userPermissions;

    //    @JsonIgnore
    //    public String getPassword() {
    //      return password;
    //    }
    //
    //    @JsonProperty
    //    public void setPassword( String password) {
    //      this.password = password;
    //    }
  }
}
