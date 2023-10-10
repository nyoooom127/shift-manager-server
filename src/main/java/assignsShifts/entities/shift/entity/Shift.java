package assignsShifts.entities.shift.entity;

import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.models.Model;
import assignsShifts.entities.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Document("shifts")
public class Shift extends Model {
//  @Id private String id;
  private Date startDate;
  private Date endDate;
  private int numDays;
  @DBRef private ShiftType type;
  @DBRef private User user;

  public Shift(String id, Date startDate, Date endDate, int numDays, ShiftType type, User user) {
    super(id);
    this.startDate = startDate;
    this.endDate = endDate;
    this.numDays = numDays;
    this.type = type;
    this.user = user;
  }

  public static int compareByDate(Shift a, Shift b) {
    return a.getStartDate().compareTo(b.getStartDate());
  }
}
