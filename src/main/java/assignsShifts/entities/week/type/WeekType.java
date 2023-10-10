package assignsShifts.entities.week.type;

import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.models.Model;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Document("week")
public class WeekType extends Model {
  @NonNull private String name;
  @NonNull private Set<ShiftType> requiredShifts;

  public WeekType(String id, @NonNull String name, @NonNull Set<ShiftType> requiredShifts) {
    super(id);
    this.name = name;
    this.requiredShifts = requiredShifts;
  }
}
