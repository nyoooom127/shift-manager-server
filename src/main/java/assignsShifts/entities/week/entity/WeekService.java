package assignsShifts.entities.week.entity;

import assignsShifts.abstractClasses.AbstractService;
import assignsShifts.entities.shift.entity.ShiftService;
import assignsShifts.entities.user.entity.User;
import assignsShifts.entities.user.entity.UserService;
import assignsShifts.logic.ShiftCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WeekService extends AbstractService<Week> {
  @Autowired ShiftCalculator shiftCalculator;

  @Autowired UserService userService;

  @Autowired
  ShiftService shiftService;

  public Week calculateWeek(Week week) {
    List<User> users = userService.findAll();

    return shiftCalculator.calculateWeek(week, users);
  }

  @Override
  public Optional<Week> update(Week entity) {
    Optional<Week> week = super.update(entity);

    entity.getShifts().forEach(shift -> shiftService.create(shift));

    return week;
  }
}
