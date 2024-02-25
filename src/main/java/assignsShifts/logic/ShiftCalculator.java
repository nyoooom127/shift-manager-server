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
    
    processExistingShifts(week.getShifts());

    List<Shift> shiftsToCalculate = getSortedShiftsToCalculate(week);

    for(Shift shift : shiftsToCalculate) {
      calculateShiftUser(shift);
    }

    week.getShifts().addAll(shiftsToCalculate.stream().filter(shift -> Objects.nonNull(shift.getUser())).toList());

    System.out.println("Finished generating week for date: " + week.getStartDate().toString());
    System.out.printf("Generated %d shifts%n", shiftsGenerated);

    return week;
  }

  private List<Shift> getSortedShiftsToCalculate(Week week) {
    List<Shift> shifts = new ArrayList<>();
    for (ShiftType shiftType : shiftTypes) {
      Calendar day = DateUtil.getCalendar(week.getStartDate());

      for (int numDay = 0; numDay < 7; numDay++) {
        Optional<Shift> existingShift = week.getShift(shiftType, day);

        if (existingShift.isPresent()) {
          System.out.println("Found existing shift: \n" + gson.toJson(existingShift.get()));
          day.add(Calendar.DATE, 1);

          continue;
        }

        System.out.println("No existing shift found, generating shift.");

        boolean isFromHome = shiftType.isDefaultFromHome() &&
                (shiftType.isNight() || (shiftType.isHasWeekends() && DateUtil.isWeekend(day)));
        Shift shift = new Shift(day.getTime(), shiftType, weekId, isFromHome);

        shifts.add(shift);
        day.add(Calendar.DATE, 1);
      }
    }

    return shifts.stream().sorted((a, b) -> {
      double diff = b.calculateScore(true) - a.calculateScore(true);

      return (int) (diff > 0 ? Math.ceil(diff) : Math.floor(diff));
    }).toList();
  }

  private void calculateShiftUser(Shift shift) {
    ShiftType shiftType = shift.getType();
    Calendar day = DateUtil.getCalendar(shift.getStartDate());
    List<User> usersForShiftType = filterUsersByShiftType(shiftType, allUsers);
    Map<Boolean, List<User>> splitUsersByQualification =
            splitUsersByQualification(shiftType, usersForShiftType);

    List<User> unQualifiedUsersForShiftDate =
            filterUsersByShiftDates(shiftType, day, splitUsersByQualification.get(false));
    boolean isQualified = false;
    User user;

    if (unQualifiedUsersForShiftDate.isEmpty()) {
      user = getUserForShift(shiftType, day, splitUsersByQualification.get(true));
      isQualified = true;
    } else {
      user = getUserForShift(shiftType, day, splitUsersByQualification.get(false));
    }

    if (user == null) {
      System.out.println("No possible shift found, skipping.");

      return;
    }

    shiftsGenerated++;
    user.addShift(shift);
    shift.setUser(user.getId());
    shift.setFromHome(shift.isFromHome() && isQualified);
  }

  private User getUserForShift(ShiftType shiftType, Calendar day, List<User> usersForShiftType) {
    return switch (shiftType.getSchedulingLogic()) {
      case SCORE -> calculateShiftUserByScore(shiftType, day, usersForShiftType);
      case ROTATION -> null;
      default -> throw new IllegalStateException("Unexpected value: " + shiftType.getSchedulingLogic());
    };
  }

  private User calculateShiftUserByScore(
          ShiftType shiftType,
          Calendar day,
          List<User> usersForShiftType) {
    List<User> usersForShiftDate = filterUsersByShiftDates(shiftType, day, usersForShiftType);

    return sortUsersByScore(
            shiftType, day, filterUsersByLimitations(shiftType, day, usersForShiftDate))
            .stream()
            .findFirst()
            .orElseGet(
                    () ->
                            sortUsersByScore(shiftType, day, usersForShiftDate).stream()
                                                                               .findFirst().orElse(null));
//                    .orElseGet(
//                        () ->
//                            sortUsersByScore(shiftType, day, usersForShiftType).stream()
//                                .findFirst().orElse(null));
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
                }));
  }

  private List<User> sortUsersByScore(ShiftType shiftType, Calendar date, List<User> users) {
    return users.stream()
        .sorted(
            (a, b) -> {
              double scoreA = a.getShiftScore(shiftType);
              double scoreB = b.getShiftScore(shiftType);

              if(scoreA != scoreB){
                double diff = a.getShiftScore(shiftType) - b.getShiftScore(shiftType);

                return (int) (diff > 0 ? Math.ceil(diff) : Math.floor(diff));
              }

                return b.getNumUpcomingConstraints(date) - a.getNumUpcomingConstraints(date);
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
  }

  private boolean isAutoSchedulingUserType(List<UserType> userTypes) {
    return userTypes.stream().anyMatch(UserType::isAutoScheduled);
  }

  private boolean isQualifiedUserType(List<UserType> userTypes) {
    return userTypes.stream().anyMatch(UserType::isQualified);
  }
}
