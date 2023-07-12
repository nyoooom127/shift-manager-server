package assignsShifts.logic;

import assignsShifts.models.User;
import assignsShifts.models.Week;
import assignsShifts.models.enums.ShiftOptionEnum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ShiftCalculator {
  private ShiftToCalc ojtUsers;
  private ShiftToCalc primaryUsers;
  private ShiftToCalc secondaryUsers;
  private ShiftToCalc levUsers;
  private ShiftToCalc integrationUsers;
  private List<User> ojtBlackList;

  private void init(List<User> userList) {
    this.ojtUsers =
        new ShiftToCalc(
            userList,
            user -> user.getShiftOptions().contains(ShiftOptionEnum.OJT),
            Comparator.comparingDouble(User::getOjtScore));

    this.primaryUsers =
        new ShiftToCalc(
            userList,
            user -> user.getShiftOptions().contains(ShiftOptionEnum.PRIMARY),
            Comparator.comparingDouble(User::getGalaxyScore));

    this.secondaryUsers =
        new ShiftToCalc(
            userList,
            user -> user.getShiftOptions().contains(ShiftOptionEnum.SECONDARY),
            Comparator.comparingDouble(User::getGalaxyScore));

    this.levUsers =
        new ShiftToCalc(
            userList,
            user -> user.getShiftOptions().contains(ShiftOptionEnum.LEV),
            Comparator.comparingDouble(User::getLevScore));

    this.integrationUsers =
        new ShiftToCalc(
            userList,
            user -> user.getShiftOptions().contains(ShiftOptionEnum.INTEGRATION),
            Comparator.comparingDouble(User::getIntegrationScore));

    this.ojtBlackList = new ArrayList<>();
  }

  private void clearData(ShiftToCalc... shiftToCalcArray) {
    for (int index = 0; index < shiftToCalcArray.length; index++) {
      shiftToCalcArray[index].clearData();

      shiftToCalcArray[index] = null;
    }

    this.ojtBlackList.clear();
    this.ojtBlackList = null;
  }

  private User getUserWithNoConstraint(
      List<User> userList, Calendar calendar, List<User> blackList) {
    User userWithNoConstraint = null;

    for (User user : userList) {
      if (!user.isHaveConstraint(calendar) && !blackList.contains(user)) {
        userWithNoConstraint = user;

        break;
      }
    }

    return userWithNoConstraint;
  }

  private void addScoreToUser(Consumer<Double> addScore, double scoreToAdd) {
    addScore.accept(scoreToAdd);
  }

  private void setOjtUser(Week.Day day, Calendar calendarDay) {
    if (day.getOjtShift().isNeeded()) {
      User ojtUser =
          this.getUserWithNoConstraint(ojtUsers.getUserList(), calendarDay, ojtBlackList);

      if (ojtUser != null) {
        day.getOjtShift().setUser(ojtUser);
        ojtBlackList.add(ojtUser);
        this.addScoreToUser(ojtUser::addOjtScore, ShiftOptionEnum.OJT.getScoreToAdd());
      }
    }
  }

  private User getNextUser(ShiftToCalc shiftToCalc, Calendar calendarDay) {
    User user =
        this.getUserWithNoConstraint(
            shiftToCalc.getUserList(), calendarDay, shiftToCalc.getSelectedUserList());

    if (user == null) {
      shiftToCalc.selectedUserList.clear();
      user =
          this.getUserWithNoConstraint(
              shiftToCalc.getUserList(), calendarDay, shiftToCalc.getSelectedUserList());
    }

    shiftToCalc.selectedUserList.add(user);

    return user;
  }

  private void setPrimaryAndSecondaryUser(Week.Day day, Calendar calendarDay) {
    if (day.getPrimaryShift().isNeeded()) {
      User primaryUser = this.getNextUser(this.primaryUsers, calendarDay);

      this.secondaryUsers.selectedUserList.add(primaryUser);
      day.getPrimaryShift().setUser(primaryUser);
      this.addScoreToUser(primaryUser::addGalaxyScore, ShiftOptionEnum.PRIMARY.getScoreToAdd());
    }

    if (day.getSecondaryShift().isNeeded()) {
      this.secondaryUsers.selectedUserList.add(day.getPrimaryShift().getUser());
      User secondaryUser = this.getNextUser(this.secondaryUsers, calendarDay);
      this.secondaryUsers.selectedUserList.remove(day.getPrimaryShift().getUser());

      this.primaryUsers.selectedUserList.add(secondaryUser);
      day.getSecondaryShift().setUser(secondaryUser);
      this.addScoreToUser(secondaryUser::addGalaxyScore, ShiftOptionEnum.PRIMARY.getScoreToAdd());
    }
  }

  private void setLevAndIntegrationUsers(Week.Day day, Calendar calendarDay) {
    if (day.getLevShift().isNeeded()) {
      User levUser =
          this.getUserWithNoConstraint(levUsers.getUserList(), calendarDay, new ArrayList<>());

      day.getLevShift().setUser(levUser);
    }

    if (day.getIntegrationShift().isNeeded()) {
      User integrationUser =
          this.getUserWithNoConstraint(
              integrationUsers.getUserList(), calendarDay, new ArrayList<>());

      day.getIntegrationShift().setUser(integrationUser);
    }
  }

  public Week calculationShiftWeek(Week week, List<User> userList) {
    this.init(userList);

    for (int dayIndex = 0; dayIndex < week.getDayList().size(); dayIndex++) {
      Calendar calendarDay = Calendar.getInstance();
      calendarDay.setTimeInMillis(week.getDayList().get(dayIndex).getDateInMillis());

      this.setOjtUser(week.getDayList().get(dayIndex), calendarDay);
      this.setPrimaryAndSecondaryUser(week.getDayList().get(dayIndex), calendarDay);
      this.setLevAndIntegrationUsers(week.getDayList().get(dayIndex), calendarDay);

      if (dayIndex > 0) {
        this.primaryUsers.selectedUserList.remove(
            week.getDayList().get(dayIndex - 1).getSecondaryShift().getUser());
        this.secondaryUsers.selectedUserList.remove(
            week.getDayList().get(dayIndex - 1).getPrimaryShift().getUser());
      }
    }

    this.clearData(ojtUsers, primaryUsers, secondaryUsers, levUsers, integrationUsers);
    return week;
  }

  private static class ShiftToCalc {
    private Comparator<? super User> comparator;
    private List<User> userList;
    private List<User> selectedUserList;

    public ShiftToCalc(
        List<User> userList,
        Predicate<? super User> predicate,
        Comparator<? super User> comparator) {
      this.userList = userList.stream().filter(predicate).collect(Collectors.toList());
      this.comparator = comparator;
      this.selectedUserList = new ArrayList<>();
    }

    public void clearData() {
      this.userList.clear();

      this.userList = null;
      this.comparator = null;
      this.selectedUserList = null;
    }

    public List<User> getUserList() {
      return this.userList.stream().sorted(comparator).collect(Collectors.toList());
    }

    public List<User> getSelectedUserList() {
      return this.selectedUserList.stream().sorted(comparator).collect(Collectors.toList());
    }
  }
}
