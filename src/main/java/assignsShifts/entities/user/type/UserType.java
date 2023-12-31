package assignsShifts.entities.user.type;

import assignsShifts.entities.shift.type.ShiftType;
import assignsShifts.models.Model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("userTypes")
public class UserType extends Model {
  private String name;
  @NonNull @DBRef private List<ShiftType> allowedShiftTypes;
  private boolean autoScheduled;

  @JsonProperty("isQualified")
  private boolean isQualified;

  private String color;

  public UserType(
      String id,
      String name,
      @NonNull List<ShiftType> allowedShiftTypes,
      boolean autoScheduled,
      boolean isQualified,
      String color) {
    super(id);
    this.name = name;
    this.allowedShiftTypes = allowedShiftTypes;
    this.autoScheduled = autoScheduled;
    this.isQualified = isQualified;
    this.color = color;
  }
}
