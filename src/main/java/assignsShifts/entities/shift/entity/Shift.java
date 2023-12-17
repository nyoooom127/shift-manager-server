package assignsShifts.entities.shift.entity;

import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.models.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("shifts")
public class Shift extends Model {
  private Date startDate;
  @DBRef private ShiftType type;
  private String user;
  private String week;

  public Shift(String id, Date startDate, ShiftType type, String user, String week) {
    super(id);
    this.startDate = startDate;
    this.type = type;
    this.user = user;
    this.week = week;
  }

  public static int compareByDate(Shift a, Shift b) {
    return a.getStartDate().compareTo(b.getStartDate());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Shift shift)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(getStartDate(), shift.getStartDate()) && Objects.equals(
            getType(),
            shift.getType()
    ) && Objects.equals(getUser(), shift.getUser());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getStartDate(), getType(), getUser());
  }
}
