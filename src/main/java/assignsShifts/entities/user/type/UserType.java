package assignsShifts.entities.user.type;

import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.models.Model;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Document("userTypes")
public class UserType extends Model {
  private String name;
  @NonNull private List<ShiftType> allowedShiftTypes;
  private boolean autoScheduled;
  private boolean needsSupervision;
  private boolean canSupervise;

  public UserType(String id, String name, @NonNull List<ShiftType> allowedShiftTypes, boolean autoScheduled, boolean needsSupervision, boolean canSupervise) {
    super(id);
    this.name = name;
    this.allowedShiftTypes = allowedShiftTypes;
    this.autoScheduled = autoScheduled;
    this.needsSupervision = needsSupervision;
    this.canSupervise = canSupervise;
  }
}
