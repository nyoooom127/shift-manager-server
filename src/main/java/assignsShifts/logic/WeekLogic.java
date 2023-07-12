package assignsShifts.logic;

import assignsShifts.models.User;
import assignsShifts.models.Week;
import org.springframework.stereotype.Component;

@Component
public class WeekLogic {
  private void removeUserDetailsFromShift(Week.Shift shift) {
    User user =
        User.builder().fullName(shift.getUser().getFullName()).id(shift.getUser().getId()).build();

    shift.setUser(user);
  }

  public Week removeUserDetails(Week week) {
    for (Week.Day day : week.getDayList()) {
      if (day.getOjtShift() != null && day.getOjtShift().getUser() != null) {
        this.removeUserDetailsFromShift(day.getOjtShift());
      }

      if (day.getPrimaryShift() != null && day.getPrimaryShift().getUser() != null) {
        this.removeUserDetailsFromShift(day.getPrimaryShift());
      }

      if (day.getSecondaryShift() != null && day.getSecondaryShift().getUser() != null) {
        this.removeUserDetailsFromShift(day.getSecondaryShift());
      }

      if (day.getLevShift() != null && day.getLevShift().getUser() != null) {
        this.removeUserDetailsFromShift(day.getLevShift());
      }

      if (day.getIntegrationShift() != null && day.getIntegrationShift().getUser() != null) {
        this.removeUserDetailsFromShift(day.getIntegrationShift());
      }
    }

    return week;
  }
}
