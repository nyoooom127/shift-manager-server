package assignsShifts.entities.shift.entity;

import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.entities.user.entity.User;
import assignsShifts.models.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Document("shifts")
public class Shift extends Model {
  private Date startDate;
  //  private Date endDate;
  @DBRef private ShiftType type;
  @DBRef private User user;

  public Shift(String id, Date startDate, ShiftType type, User user) {
    super(id);
    this.startDate = startDate;
    //    this.endDate = endDate;
    this.type = type;
    this.user = user;
  }

  public static int compareByDate(Shift a, Shift b) {
    return a.getStartDate().compareTo(b.getStartDate());
  }
}
