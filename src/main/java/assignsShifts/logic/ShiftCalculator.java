package assignsShifts.logic;

import assignsShifts.entities.shift.entity.Shift;
import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.entities.user.entity.User;
import assignsShifts.entities.user.type.UserType;
import assignsShifts.entities.week.entity.Week;
import assignsShifts.models.enums.ShiftSchedulingLogicEnum;
import assignsShifts.utils.DateUtil;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ShiftCalculator {
  private List<User> allUsers;
  private List<User> potentialUsers;

  private void init(List<User> userList) {
    this.allUsers = new ArrayList<>(userList);
    this.potentialUsers = new ArrayList<>(userList);
  }

  public Week calculateWeek(Week week, List<User> users) {
    init(users);

    for (ShiftType shiftType : week.getType().getRequiredShifts()) {
      List<User> usersForShiftType = filterUsersByShiftType(shiftType, potentialUsers);
      Map<Boolean, List<User>> splitUsersBySupervision =
          splitUsersBySupervision(shiftType, usersForShiftType);
      Calendar day = Calendar.getInstance();
      day.setTime(week.getStartDate());

      List<Shift> shifts;

      if (ShiftSchedulingLogicEnum.ROTATION.equals(shiftType.getSchedulingLogic())) {
        shifts = getShiftsByRotation(week, shiftType);
      } else {
        shifts = getShiftsByScore(week, shiftType, splitUsersBySupervision);
      }

      shifts.forEach(
          shift -> {
            potentialUsers.remove(shift.getUser());
            potentialUsers.add(shift.getUser());
          });

      week.getShifts().addAll(shifts);
    }

    return week;
  }

  private List<Shift> getShiftsByRotation(Week week, ShiftType shiftType) {
    int numShiftsPerWeek = shiftType.isHasWeekends() ? 7 : 5;
    List<Shift> shifts = new ArrayList<>();
    User leastRecentUser = sortUsersByLeastRecent(shiftType, shiftType.getRotationUsers()).get(0);
    Calendar day = Calendar.getInstance();
    day.setTime(week.getStartDate());

    for (int numDay = 0; numDay < numShiftsPerWeek; numDay++) {
      Optional<Shift> existingShift = week.getShift(shiftType, day);

      if (existingShift.isPresent()) {
        processShift(existingShift.get());
      } else {
        Shift shift = new Shift(day.getTime(), shiftType, leastRecentUser);
        shifts.add(shift);
        leastRecentUser.addShift(shift);
      }

      day.add(Calendar.DATE, 1);
    }

    return shifts;
  }

  private List<Shift> getShiftsByScore(
      Week week, ShiftType shiftType, Map<Boolean, List<User>> splitUsersBySupervision) {
    List<Shift> shifts = new ArrayList<>();
    Calendar day = Calendar.getInstance();
    day.setTime(week.getStartDate());

    for (int numDay = 0; numDay < 5; numDay++) {
      Optional<Shift> existingShift = week.getShift(shiftType, day);

      if (existingShift.isPresent()) {
        processShift(existingShift.get());
      } else {
        Shift shift =
            getShiftByScore(shiftType, Collections.singletonList(day), splitUsersBySupervision)
                .get(0);
        shifts.add(shift);
        shift.getUser().addShift(shift);
      }

      day.add(Calendar.DATE, 1);
    }

    if (shiftType.isHasWeekends()) {
      Calendar friday = (Calendar) day.clone();
      friday.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
      Calendar saturday = (Calendar) day.clone();
      saturday.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

      List<Shift> weekendShifts =
          getShiftByScore(shiftType, List.of(friday, saturday), splitUsersBySupervision);
      shifts.addAll(weekendShifts);
      weekendShifts.forEach(shift -> shift.getUser().addShift(shift));
    }

    return shifts;
  }

  private List<Shift> getShiftByScore(
      ShiftType shiftType, List<Calendar> days, Map<Boolean, List<User>> splitUsersBySupervision) {
    User user;
    List<User> qualifiedUsersForShiftDate =
        filterUsersByShiftDates(shiftType, days, splitUsersBySupervision.get(true));
    List<User> unQualifiedUsersForShiftDate =
        filterUsersByShiftDates(shiftType, days, splitUsersBySupervision.get(false));

    if (days.stream().anyMatch(DateUtil::isWeekend) || unQualifiedUsersForShiftDate.isEmpty()) {
      user =
          getUserForShift(
              shiftType,
              days.get(0),
              qualifiedUsersForShiftDate,
              splitUsersBySupervision.get(true));
    } else {
      user =
          getUserForShift(
              shiftType,
              days.get(0),
              unQualifiedUsersForShiftDate,
              splitUsersBySupervision.get(false));
    }

    return days.stream()
        .map(day -> new Shift(day.getTime(), shiftType, user))
        .collect(Collectors.toList());
  }

  private User getUserForShift(
      ShiftType shiftType,
      Calendar day,
      List<User> usersForShiftDate,
      List<User> usersForShiftType) {
    return sortUsersByScore(shiftType, day, usersForShiftDate).stream()
        .findFirst()
        .orElseGet(
            () ->
                sortUsersByScore(shiftType, day, usersForShiftType).stream()
                    .findFirst()
                    .orElse(null));
  }

  private List<User> filterUsersByShiftType(ShiftType shiftType, List<User> users) {
    return users.stream()
        .filter(user -> filterUserByShiftType(shiftType, user))
        .collect(Collectors.toList());
  }

  private boolean filterUserByShiftType(ShiftType shiftType, User user) {
    List<UserType> overlappingUserTypes = user.getOverlappingTypes(shiftType);

    return !overlappingUserTypes.isEmpty() && isAutoScheduling(overlappingUserTypes);
  }

  private List<User> filterUsersByShiftDates(
      ShiftType shiftType, List<Calendar> shiftDates, List<User> users) {
    return users.stream()
        .filter(
            user ->
                shiftDates.stream()
                    .allMatch(shiftDate -> filterUserByShiftDate(shiftType, shiftDate, user)))
        .collect(Collectors.toList());
  }

  private boolean filterUserByShiftDate(ShiftType shiftType, Calendar shiftDate, User user) {
    return user.isEnoughDaysSinceLastShift(shiftType, shiftDate)
        && user.isHaveConstraint(shiftDate, shiftType.getMinBreak());
  }

  private Map<Boolean, List<User>> splitUsersBySupervision(ShiftType shiftType, List<User> users) {
    return users.stream()
        .collect(
            Collectors.partitioningBy(
                user -> {
                  List<UserType> overlappingUserTypes = user.getOverlappingTypes(shiftType);
                  return overlappingUserTypes.stream().anyMatch(UserType::isCanSupervise)
                      && overlappingUserTypes.stream().noneMatch(UserType::isNeedsSupervision);
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

              return (int) (a.getShiftScore(shiftType) - b.getShiftScore(shiftType));
            })
        .collect(Collectors.toList());
  }

  private List<User> sortUsersByLeastRecent(ShiftType shiftType, List<User> users) {
    return users.stream()
        .sorted(
            (a, b) -> {
              Optional<Shift> mostRecentShiftA = a.getMostRecentShift(shiftType);
              Optional<Shift> mostRecentShiftB = b.getMostRecentShift(shiftType);

              if (mostRecentShiftA.isEmpty() && mostRecentShiftB.isEmpty()) {
                return 0;
              }

              if (mostRecentShiftA.isPresent() && mostRecentShiftB.isEmpty()) {
                return 1;
              }

              if (mostRecentShiftA.isEmpty()) {
                return -1;
              }

              return Shift.compareByDate(mostRecentShiftA.get(), mostRecentShiftB.get());
            })
        .collect(Collectors.toList());
  }

  private void processShift(Shift shift) {
    User shiftUser = shift.getUser();
    shiftUser.addShift(shift);
    potentialUsers.remove(shiftUser);
    allUsers.set(allUsers.indexOf(shiftUser), shiftUser);
  }

  private boolean isAutoScheduling(List<UserType> userTypes) {
    return userTypes.stream().anyMatch(UserType::isAutoScheduled);
  }
}
