package assignsShifts.entities.user.entity;

import assignsShifts.entities.constraint.entity.Constraint;
import assignsShifts.entities.shift.entity.Shift;
import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.entities.user.type.UserType;
import assignsShifts.models.Model;
import assignsShifts.models.enums.UserPermissionsEnum;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static assignsShifts.utils.DateUtil.isDateInRange;

@Data
@Document("users")
@AllArgsConstructor
@Builder(toBuilder = true)
public class User extends Model {
  @NonNull private String fullName;
  @DBRef private List<UserType> types;
  @DBRef private Map<ShiftType, Integer> numShifts;
  private AuthorizationData authorizationData;
  @DBRef @NonNull private List<Constraint> constraints;
  @DBRef @NonNull private List<Shift> shifts;
  private boolean active;
  //  @DBRef private Shift mostRecentShift;

  public User(
      String id,
      @NonNull String fullName,
      List<UserType> types,
      Map<ShiftType, Integer> numShifts,
      AuthorizationData authorizationData,
      @NonNull List<Constraint> constraints,
      @NonNull List<Shift> shifts,
      boolean active) {
    super(id);
    this.fullName = fullName;
    this.types = types;
    this.numShifts = numShifts;
    this.authorizationData = authorizationData;
    this.constraints = constraints;
    this.shifts = shifts;
    this.active = active;
  }

  public User hideAuthData() {
    return this.toBuilder().authorizationData(null).build();
  }

  public void addShift(Shift shiftToAdd) {
    this.shifts.add(shiftToAdd);
    Integer currentNum = this.numShifts.getOrDefault(shiftToAdd.getType(), 0);
    this.numShifts.put(shiftToAdd.getType(), currentNum + 1);

    //    if (this.mostRecentShift == null || ) {
    //      this.mostRecentShift = shiftToAdd;
    //    }
  }

  public List<Constraint> getConstraints() {
    if (this.constraints == null) {
      this.constraints = new ArrayList<>();
    }

    return this.constraints;
  }

  public List<ShiftType> getShiftOptions() {
    return this.getTypes().stream()
        .flatMap(userType -> userType.getAllowedShiftTypes().stream())
        .distinct()
        .collect(Collectors.toList());
  }

  public boolean isEnoughDaysSinceLastShift(ShiftType shiftType, Calendar startDate) {
    Optional<Shift> mostRecentShift = getMostRecentShift(shiftType);

    if (mostRecentShift.isEmpty()) {
      return true;
    }

    return Duration.between(mostRecentShift.get().getStartDate().toInstant(), startDate.toInstant())
            .toDays()
        > shiftType.getMinBreak();
  }

  public Optional<Shift> getMostRecentShift(ShiftType shiftType) {
    return this.getShifts().stream()
        .filter(shift -> shiftType.equals(shift.getType()))
        .max(Shift::compareByDate);
  }

  public List<UserType> getOverlappingTypes(ShiftType shiftType) {
    return this.getTypes().stream()
        .filter(userType -> shiftType.getAllowedUserTypes().contains(userType))
        .collect(Collectors.toList());
  }

  public boolean isHaveConstraint(Calendar shiftStart, int shiftDuration) {
    Calendar shiftEnd = (Calendar) shiftStart.clone();
    shiftEnd.add(Calendar.DATE, shiftDuration);

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
      return getNumShifts().get(shiftType) * shiftType.getScore();
    }

    List<UserType> userTypes = getOverlappingTypes(shiftType);

    List<ShiftType> shiftTypes =
        userTypes.stream()
            .flatMap(userType -> userType.getAllowedShiftTypes().stream())
            .distinct()
            .toList();

    return shiftTypes.stream()
        .mapToDouble(currShiftType -> getNumShifts().get(currShiftType) * currShiftType.getScore())
        .sum();
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

  @Data
  @AllArgsConstructor
  public static class AuthorizationData {
    @NonNull private String username;
    @NonNull private String password;
    @NonNull private String email;
    @NonNull private String phone;
    @NonNull private UserPermissionsEnum userPermissions;
  }
}
