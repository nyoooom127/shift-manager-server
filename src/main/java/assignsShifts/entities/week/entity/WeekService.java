package assignsShifts.entities.week.entity;

import assignsShifts.abstractClasses.AbstractService;
import assignsShifts.entities.user.entity.User;
import assignsShifts.entities.user.entity.UserService;
import assignsShifts.logic.ShiftCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeekService extends AbstractService<Week> {
  @Autowired ShiftCalculator shiftCalculator;

  @Autowired UserService userService;

  public Week calculateWeek(Week week) {
    List<User> users = userService.findAll();

    return shiftCalculator.calculateWeek(week, users);
  }
}
