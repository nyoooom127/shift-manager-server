package assignsShifts.logic;

import assignsShifts.entities.shift.entity.Shift;
import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.entities.user.entity.User;
import assignsShifts.entities.user.type.UserType;
import assignsShifts.entities.week.entity.Week;
import assignsShifts.models.enums.ShiftSchedulingLogicEnum;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ShiftCalculator {

  @Autowired private Gson gson;

  private List<User> allUsers;
  private List<User> potentialUsers;
  private int shiftsGenerated;
  private List<ShiftType> shiftTypes;

  private void init(List<User> userList, Set<ShiftType> shiftTypes) {
    this.allUsers = new ArrayList<>(userList);
    this.potentialUsers = new ArrayList<>(userList);
    this.shiftTypes = new ArrayList<>(shiftTypes);
    this.shiftsGenerated = 0;
  }

  public Week calculateWeek(Week week, List<User> users) {
    init(users, week.getType().getRequiredShifts());

    System.out.printf("Received week with %d shifts%n", week.getShifts().size());

    Calendar day = Calendar.getInstance();
    day.setTime(week.getStartDate());

    processExistingShifts(week.getShifts());

    for (int numDay = 0; numDay < 7; numDay++) {
      List<Shift> shifts = getShiftsForDay(day, week);
      week.getShifts().addAll(shifts);
      day.add(Calendar.DATE, 1);
    }

    System.out.println("Finished generating week for date: " + week.getStartDate().toString());
    System.out.printf("Generated %d shifts%n", shiftsGenerated);

    return week;
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

      List<User> usersForShiftType = filterUsersByShiftType(shiftType, potentialUsers);
      Map<Boolean, List<User>> splitUsersBySupervision =
          splitUsersBySupervision(shiftType, usersForShiftType);
      Shift shift;

      if (ShiftSchedulingLogicEnum.ROTATION.equals(shiftType.getSchedulingLogic())) {
        //        shifts = getShiftsByRotation(week, shiftType);
        shift = new Shift();
      } else {
        shift = getShiftByScore(shiftType, day, splitUsersBySupervision);
      }
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
      Week week, ShiftType shiftType, Map<Boolean, List<User>> splitUsersBySupervision) {
    List<Shift> shifts = new ArrayList<>();
    Calendar day = Calendar.getInstance();
    day.setTime(week.getStartDate());

    for (int numDay = 0; numDay < 7; numDay++) {
      Optional<Shift> existingShift = week.getShift(shiftType, day);

      if (existingShift.isPresent()) {
        processShift(existingShift.get());
        System.out.println("Found existing shift: \n" + gson.toJson(existingShift.get()));
      } else {
        System.out.println("No existing shift found, generating shift.");
        Shift shift = getShiftByScore(shiftType, day, splitUsersBySupervision);
        shifts.add(shift);
        shiftsGenerated++;
        //        shift.getUser().addShift(shift);
      }

      day.add(Calendar.DATE, 1);
    }

    return shifts;
  }

  private Shift getShiftByScore(
      ShiftType shiftType, Calendar day, Map<Boolean, List<User>> splitUsersBySupervision) {
    User user;
    List<User> qualifiedUsersForShiftDate =
        filterUsersByShiftDates(shiftType, day, splitUsersBySupervision.get(true));
    List<User> unQualifiedUsersForShiftDate =
        filterUsersByShiftDates(shiftType, day, splitUsersBySupervision.get(false));

    if (unQualifiedUsersForShiftDate.isEmpty()) {
      user =
          getUserForShift(
              shiftType, day, qualifiedUsersForShiftDate, splitUsersBySupervision.get(true));
    } else {
      user =
          getUserForShift(
              shiftType, day, unQualifiedUsersForShiftDate, splitUsersBySupervision.get(false));
    }

    Shift shift = new Shift(day.getTime(), shiftType, user.getId());
    user.addShift(shift);

    return shift;
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

    return !overlappingUserTypes.isEmpty() && isAutoScheduling(overlappingUserTypes) && user.isActive();
  }

  private List<User> filterUsersByShiftDates(
      ShiftType shiftType, Calendar shiftDate, List<User> users) {
    return users.stream()
        .filter(user -> filterUserByShiftDate(shiftType, shiftDate, user))
        .collect(Collectors.toList());
  }

  private boolean filterUserByShiftDate(ShiftType shiftType, Calendar shiftDate, User user) {
    return user.isEnoughDaysSinceLastShift(shiftType, shiftDate)
        && !user.isHaveConstraint(shiftDate, shiftType.getStartHour(), shiftType.getDuration());
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

  private boolean isAutoScheduling(List<UserType> userTypes) {
    return userTypes.stream().anyMatch(UserType::isAutoScheduled);
  }
}
