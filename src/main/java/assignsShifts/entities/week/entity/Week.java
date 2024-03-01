package assignsShifts.entities.week.entity;

import assignsShifts.entities.shift.entity.Shift;
import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.entities.week.type.WeekType;
import assignsShifts.models.Model;
import assignsShifts.utils.DateUtil;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("weeks")
public class Week extends Model {
  @DBRef @NonNull private List<Shift> shifts;
  @DBRef @NonNull private WeekType type;
  @NonNull private Date startDate;

  public Week(
      String id, @NonNull List<Shift> shifts, @NonNull WeekType type, @NonNull Date startDate) {
    super(id);
    this.shifts = shifts;
    this.type = type;
    this.startDate = startDate;
  }

  public Optional<Shift> getShift(ShiftType shiftType, Calendar shiftStartDate) {
    return shifts.stream()
        .filter(
            shift -> {
              Calendar shiftStartCalendar = DateUtil.getCalendar(shift.getStartDate());

              return shiftType.getId().equals(shift.getType().getId())
                  && shiftStartDate.get(Calendar.YEAR) == shiftStartCalendar.get(Calendar.YEAR)
                  && shiftStartDate.get(Calendar.DAY_OF_YEAR)
                      == shiftStartCalendar.get(Calendar.DAY_OF_YEAR);
            })
        .findFirst();
  }
}
