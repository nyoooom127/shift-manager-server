package assignsShifts.entities.shift.type;

import assignsShifts.models.Model;
import assignsShifts.entities.user.entity.User;
import assignsShifts.entities.user.type.UserType;
import assignsShifts.models.enums.ShiftSchedulingLogicEnum;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@Document("shiftTypes")
public class ShiftType extends Model {
//  @Id private String id;
  @NonNull private String name;
  @DBRef @NonNull private List<UserType> allowedUserTypes;
//  @NonNull private Duration defaultLength;
  @NonNull private double score;
  private int minBreak;
  private boolean hasWeekends;
//  private int numShiftsPerWeek;
  private ShiftSchedulingLogicEnum schedulingLogic;
  @DBRef private List<User> rotationUsers;

  public ShiftType(String id, @NonNull String name, @NonNull List<UserType> allowedUserTypes, double score, int minBreak, boolean hasWeekends, ShiftSchedulingLogicEnum schedulingLogic, List<User> rotationUsers) {
    super(id);
    this.name = name;
    this.allowedUserTypes = allowedUserTypes;
    this.score = score;
    this.minBreak = minBreak;
    this.hasWeekends = hasWeekends;
    this.schedulingLogic = schedulingLogic;
    this.rotationUsers = rotationUsers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ShiftType shiftType = (ShiftType) o;
    return id.equals(shiftType.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
