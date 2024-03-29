package assignsShifts.entities.week.entity;

import assignsShifts.abstractClasses.AbstractService;
import assignsShifts.entities.shift.entity.Shift;
import assignsShifts.entities.shift.entity.ShiftService;
import assignsShifts.entities.user.entity.User;
import assignsShifts.entities.user.entity.UserService;
import assignsShifts.logic.ShiftCalculator;
import assignsShifts.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class WeekService extends AbstractService<Week> {
  @Autowired WeekRepository weekRepository;

  @Autowired ShiftCalculator shiftCalculator;

  @Autowired UserService userService;

  @Autowired ShiftService shiftService;

  public List<Week> findAllByDate(Date startDate) {
    Calendar calendar = DateUtil.getCalendar(startDate);
    calendar.add(Calendar.MONTH, -1);
    return this.weekRepository.findAllByDate(calendar.getTime());
//    return super.findAll();
  }

  public Week calculateWeek(Week week) {
    List<User> users = userService.findAll();

    return shiftCalculator.calculateWeek(week, users);
  }

  @Override
  public Optional<Week> update(Week entity) {
    Optional<Week> existingWeek = findById(entity.getId());
    Optional<Week> week = super.update(entity);
    if (existingWeek.isPresent()) {
      List<Shift> changedShifts =
          entity.getShifts().stream()
              .filter(shift -> !existingWeek.get().getShifts().contains(shift))
              .toList();
      changedShifts.forEach(shiftService::upsert);
    }

    existingWeek.ifPresent(
        value ->
            value.getShifts().stream()
                .filter(
                    existingShift ->
                        entity.getShifts().stream()
                            .noneMatch(shift -> existingShift.getId().equals(shift.getId())))
                .forEach(shift -> shiftService.delete(shift.getId())));

    return week;
  }
}
