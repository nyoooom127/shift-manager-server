package assignsShifts.logic;

import assignsShifts.models.User;
import assignsShifts.models.Week;
import assignsShifts.models.enums.ShiftOptionEnum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class ScoreCalculator {
  private void addScoreToUser(Consumer<Double> addScore, double scoreToAdd) {
    addScore.accept(scoreToAdd);
  }

  public List<User> calculationShiftWeek(Week week) {
    List<User> userList = new ArrayList<>();

    for (Week.Day day : week.getDayList()) {
      if (day.getOjtShift() != null && day.getOjtShift().getUser() != null) {
        this.addScoreToUser(day.getOjtShift().getUser()::addOjtScore, ShiftOptionEnum.OJT.getScoreToAdd());

        userList.add(day.getOjtShift().getUser());
      }

      if (day.getPrimaryShift() != null && day.getPrimaryShift().getUser() != null) {
        double primaryScore = ShiftOptionEnum.PRIMARY.getScoreToAdd();

        if (day.getOjtShift().getUser() != null) {
          primaryScore = ShiftOptionEnum.PRIMARY.getScoreToAdd() / 2.0;
        }

        this.addScoreToUser(day.getOjtShift().getUser()::addGalaxyScore, primaryScore);
        userList.add(day.getPrimaryShift().getUser());
      }

      if (day.getSecondaryShift() != null && day.getSecondaryShift().getUser() != null) {
        this.addScoreToUser(
            day.getOjtShift().getUser()::addGalaxyScore, ShiftOptionEnum.SECONDARY.getScoreToAdd());

        userList.add(day.getSecondaryShift().getUser());
      }

      if (day.getLevShift() != null && day.getLevShift().getUser() != null) {
        this.addScoreToUser(day.getOjtShift().getUser()::addLevScore, ShiftOptionEnum.LEV.getScoreToAdd());

        userList.add(day.getSecondaryShift().getUser());
      }

      if (day.getIntegrationShift() != null && day.getIntegrationShift().getUser() != null) {
        this.addScoreToUser(
            day.getOjtShift().getUser()::addIntegrationScoreScore, ShiftOptionEnum.INTEGRATION.getScoreToAdd());

        userList.add(day.getSecondaryShift().getUser());
      }
    }

    return userList;
  }
}
