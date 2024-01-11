package assignsShifts.logic;

import assignsShifts.entities.shift.entity.Shift;
import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.entities.user.entity.User;
import assignsShifts.entities.user.type.UserType;
import assignsShifts.entities.week.entity.Week;
import assignsShifts.models.enums.ShiftSchedulingLogicEnum;
import assignsShifts.utils.DateUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ShiftCalculator {

  @Autowired private Gson gson;

  private List<User> allUsers;
  private int shiftsGenerated;
  private List<ShiftType> shiftTypes;
  private String weekId;

  private void init(List<User> userList, Set<ShiftType> shiftTypes, String weekId) {
    this.shiftsGenerated = 0;
    this.weekId = weekId;
    this.allUsers = removeCurrentWeekSavedShifts(userList, weekId);
    this.shiftTypes = removeManualShiftTypes(shiftTypes);
  }

  private List<User> removeCurrentWeekSavedShifts(List<User> users, String weekIdToRemove){
    return users.stream().map(user -> user.removeShiftsByWeekId(weekIdToRemove)).toList();
  }

  private List<ShiftType> removeManualShiftTypes(Set<ShiftType> shiftTypes){
    return shiftTypes.stream()
                     .filter(shiftType -> !ShiftSchedulingLogicEnum.MANUAL.equals(shiftType.getSchedulingLogic()))
                     .distinct()
                     .collect(Collectors.toList());
  }

  public Week calculateWeek(Week week, List<User> users) {
    init(users, week.getType().getRequiredShifts(), week.getId());

    System.out.printf("Received week with %d shifts%n", week.getShifts().size());

    Calendar day = Calendar.getInstance();
    day.setTime(week.getStartDate());

    processExistingShifts(week.getShifts());

    week.getShifts().addAll(getWeekendShifts(week));

    for (ShiftType shiftType :
        shiftTypes.stream()
            .sorted((a, b) -> (int) Math.round(b.getScore() - a.getScore()))
            .toList()) {
      List<User> usersForShiftType = filterUsersByShiftType(shiftType, allUsers);
      Map<Boolean, List<User>> splitUsersByQualification =
          splitUsersByQualification(shiftType, usersForShiftType);
      week.getShifts().addAll(getShiftsByScore(week, shiftType, splitUsersByQualification));
    }

    //    for (int numDay = 0; numDay < 5; numDay++) {
    //      List<Shift> shifts = getShiftsForDay(day, week);
    //      week.getShifts().addAll(shifts);
    //      day.add(Calendar.DATE, 1);
    //    }

    System.out.println("Finished generating week for date: " + week.getStartDate().toString());
    System.out.printf("Generated %d shifts%n", shiftsGenerated);

    return week;
  }

  private List<Shift> getWeekendShifts(Week week) {
    List<Shift> shifts = new ArrayList<>();
    Calendar day = Calendar.getInstance();
    day.setTime(week.getStartDate());
    day.add(Calendar.DATE, 5);

    for (int numDay = 0; numDay < 2; numDay++) {
      shifts.addAll(getShiftsForDay(day, week));
      day.add(Calendar.DATE, 1);
    }

    return shifts;
  }

  private List<Shift> getShiftsForDay(Calendar day, Week week) {
    List<Shift> shifts = new ArrayList<>();

    for (ShiftType shiftType : shiftTypes) {

      Optional<Shift> existingShift = week.getShift(shiftType, day);

      if (existingShift.isPresent()) {
        //        processShift(existingShift.get());
        System.out.println("Found existing shift: \n" + gson.toJson(existingShift.get()));
        continue;
      }

      System.out.println("No existing shift found, generating shift.");

      List<User> usersForShiftType = filterUsersByShiftType(shiftType, allUsers);
      Map<Boolean, List<User>> splitUsersByQualification =
          splitUsersByQualification(shiftType, usersForShiftType);

      Shift shift = switch (shiftType.getSchedulingLogic()) {
        case SCORE -> getShiftByScore(shiftType, day, splitUsersByQualification);
        case ROTATION -> new Shift(); // getShiftsByRotation(week, shiftType);
        default -> throw new IllegalStateException("Unexpected value: " + shiftType.getSchedulingLogic());
      };

      shifts.add(shift);
      shiftsGenerated++;
      //        shift.getUser().addShift(shift);
    }

    return shifts;
  }

  //  private List<Shift> getShiftsByRotation(Week week, ShiftType shiftType) {
  //    int numShiftsPerWeek = shiftType.isHasWeekends() ? 7 : 5;
  //    List<Shift> shifts = new ArrayList<>();
  //    User leastRecentUser = sortUsersByLeastRecent(shiftType,
  // shiftType.getRotationUsers()).get(0);
  //    Calendar day = Calendar.getInstance();
  //    day.setTime(week.getStartDate());
  //
  //    for (int numDay = 0; numDay < numShiftsPerWeek; numDay++) {
  //      Optional<Shift> existingShift = week.getShift(shiftType, day);
  //
  //      if (existingShift.isPresent()) {
  //        processShift(existingShift.get());
  //      } else {
  //        Shift shift = new Shift(day.getTime(), shiftType, leastRecentUser.getId());
  //        shifts.add(shift);
  //        leastRecentUser.addShift(shift);
  //      }
  //
  //      day.add(Calendar.DATE, 1);
  //    }
  //
  //    return shifts;
  //  }

  private List<Shift> getShiftsByScore(
      Week week, ShiftType shiftType, Map<Boolean, List<User>> splitUsersByQualification) {
    List<Shift> shifts = new ArrayList<>();
    Calendar day = Calendar.getInstance();
    day.setTime(week.getStartDate());

    for (int numDay = 0; numDay < 5; numDay++) {
      Optional<Shift> existingShift = week.getShift(shiftType, day);

      if (existingShift.isPresent()) {
        //        processShift(existingShift.get());
        System.out.println("Found existing shift: \n" + gson.toJson(existingShift.get()));
        day.add(Calendar.DATE, 1);

        continue;
      }
      System.out.println("No existing shift found, generating shift.");
      Shift shift = getShiftByScore(shiftType, day, splitUsersByQualification);
      shifts.add(shift);
      shiftsGenerated++;
      //        shift.getUser().addShift(shift);

      day.add(Calendar.DATE, 1);
    }

    return shifts;
  }

  private Shift getShiftByScore(
      ShiftType shiftType, Calendar day, Map<Boolean, List<User>> splitUsersByQualification) {
    User user;
    List<User> qualifiedUsersForShiftDate =
        filterUsersByShiftDates(shiftType, day, splitUsersByQualification.get(true));
    List<User> unQualifiedUsersForShiftDate =
        filterUsersByShiftDates(shiftType, day, splitUsersByQualification.get(false));

    if (unQualifiedUsersForShiftDate.isEmpty()) {
      user =
          getUserForShift(
              shiftType, day, qualifiedUsersForShiftDate, splitUsersByQualification.get(true));
    } else {
      user =
          getUserForShift(
              shiftType, day, unQualifiedUsersForShiftDate, splitUsersByQualification.get(false));
    }

    Shift shift = new Shift(day.getTime(), shiftType, user.getId(), weekId);
    user.addShift(shift);

    return shift;
  }

  private User getUserForShift(
      ShiftType shiftType,
      Calendar day,
      List<User> usersForShiftDate,
      List<User> usersForShiftType) {
    return sortUsersByScore(
            shiftType, day, filterUsersByLimitations(shiftType, day, usersForShiftDate))
        .stream()
        .findFirst()
        .orElseGet(
            () ->
                sortUsersByScore(shiftType, day, usersForShiftDate).stream()
                    .findFirst()
                    .orElseGet(
                        () ->
                            sortUsersByScore(shiftType, day, usersForShiftType).stream()
                                .findFirst()
                                .orElse(null)));
  }

  private List<User> filterUsersByShiftType(ShiftType shiftType, List<User> users) {
    return users.stream()
        .filter(user -> filterUserByShiftType(shiftType, user))
        .collect(Collectors.toList());
  }

  private boolean filterUserByShiftType(ShiftType shiftType, User user) {
    List<UserType> overlappingUserTypes = user.getOverlappingTypes(shiftType);

    return !overlappingUserTypes.isEmpty()
        && isAutoSchedulingUserType(overlappingUserTypes)
        && user.isActive()
        && (!shiftType.isNeedQualified()
            || isQualifiedUserType(overlappingUserTypes)
            || user.isQualified());
  }

  private List<User> filterUsersByShiftDates(
      ShiftType shiftType, Calendar shiftDate, List<User> users) {
    return users.stream()
        .filter(user -> filterUserByShiftDate(shiftType, shiftDate, user))
        .collect(Collectors.toList());
  }

  private boolean filterUserByShiftDate(ShiftType shiftType, Calendar shiftDate, User user) {
    return user.isEnoughDaysSinceLastShift(shiftType, shiftDate)
        && user.isEnoughDaysUntilNextShift(shiftType, shiftDate)
        && !user.isHaveConstraint(shiftDate, shiftType.getStartHour(), shiftType.getDuration())
        && user.getShifts().stream().filter(shift -> shift.getWeek().equals(weekId)).count()
            < shiftType.getMaxShiftsPerWeek();
  }

  private List<User> filterUsersByLimitations(
      ShiftType shiftType, Calendar shiftDate, List<User> users) {
    return users.stream()
        .filter(user -> filterUserByLimitations(shiftType, shiftDate, user))
        .collect(Collectors.toList());
  }

  private boolean filterUserByLimitations(ShiftType shiftType, Calendar shiftDate, User user) {
    return (DateUtil.isWeekend(shiftDate) || DateUtil.isTomorrowWeekend(shiftDate))
        || !(shiftType.isNight() && user.isAvoidNight());
    //        && (!DateUtil.isWeekend(shiftDate) || !user.isAvoidWeekend());
  }

  private Map<Boolean, List<User>> splitUsersByQualification(
      ShiftType shiftType, List<User> users) {
    return users.stream()
        .collect(
            Collectors.partitioningBy(
                user -> {
                  List<UserType> overlappingUserTypes = user.getOverlappingTypes(shiftType);
                  return overlappingUserTypes.stream().anyMatch(UserType::isQualified)
                      || user.isQualified();
                  //                      &&
                  // overlappingUserTypes.stream().noneMatch(UserType::isNeedsSupervision);
                }));
  }

  private List<User> sortUsersByScore(ShiftType shiftType, Calendar date, List<User> users) {
    return users.stream()
        .sorted(
            (a, b) -> {
              int numUpcomingConstraintsA = a.getNumUpcomingConstraints(date);
              int numUpcomingConstraintsB = b.getNumUpcomingConstraints(date);

              if (numUpcomingConstraintsA != numUpcomingConstraintsB) {
                return numUpcomingConstraintsB - numUpcomingConstraintsA;
              }

              double diff = a.getShiftScore(shiftType) - b.getShiftScore(shiftType);

              return (int) (diff > 0 ? Math.ceil(diff) : Math.floor(diff));
            })
        .collect(Collectors.toList());
  }

  private void processExistingShifts(List<Shift> existingShifts) {
    existingShifts.forEach(this::processShift);
  }

  private void processShift(Shift shift) {
    Optional<User> shiftUser =
        allUsers.stream().filter(user -> user.getId().equals(shift.getUser())).findFirst();

    if (shiftUser.isEmpty()) {
      return;
    }

    shiftUser.get().addShift(shift);
    //    potentialUsers.remove(shiftUser.get());
    //    allUsers.set(allUsers.indexOf(shiftUser.get()), shiftUser.get());
  }

  private boolean isAutoSchedulingUserType(List<UserType> userTypes) {
    return userTypes.stream().anyMatch(UserType::isAutoScheduled);
  }

  private boolean isQualifiedUserType(List<UserType> userTypes) {
    return userTypes.stream().anyMatch(UserType::isQualified);
  }
}
