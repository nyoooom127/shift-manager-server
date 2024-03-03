package assignsShifts.entities.shift.type;

import assignsShifts.models.Model;
import assignsShifts.models.enums.ShiftSchedulingLogicEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("shiftTypes")
public class ShiftType extends Model {
  //  @Id private String id;
  @NonNull private String name;
  @NonNull private List<String> allowedUserTypeIds;
  @NonNull private double duration;
  @NonNull private int startHour;
  //  @NonNull private Duration defaultLength;
  @NonNull private double score;
  @NonNull private double weekendScore;
  private int minBreak;
  private int maxShiftsPerWeek;
  private boolean hasWeekends;
  //  private int numShiftsPerWeek;
  private ShiftSchedulingLogicEnum schedulingLogic;
  private int displayOrder;

  @JsonProperty("isNeedQualified")
  private boolean isNeedQualified;

  @JsonProperty("isNight")
  private boolean isNight;

  @JsonProperty("isDefaultWeekdayFromHome")
  private boolean isDefaultWeekdayFromHome;

  @JsonProperty("isDefaultWeekendFromHome")
  private boolean isDefaultWeekendFromHome;
  //  @DBRef private List<User> rotationUsers;

  public ShiftType(
      String id,
      @NonNull String name,
      @NonNull List<String> allowedUserTypeIds,
      @NonNull double duration,
      @NonNull int startHour,
      double score,
      double weekendScore,
      int minBreak,
      boolean hasWeekends,
      ShiftSchedulingLogicEnum schedulingLogic,
      int displayOrder,
      boolean isNeedQualified,
      boolean isNight,
      boolean isDefaultWeekdayFromHome,
      boolean isDefaultWeekendFromHome
      //      List<User> rotationUsers
      ) {
    super(id);
    this.name = name;
    this.allowedUserTypeIds = allowedUserTypeIds;
    this.duration = duration;
    this.startHour = startHour;
    this.score = score;
    this.weekendScore = weekendScore;
    this.minBreak = minBreak;
    this.hasWeekends = hasWeekends;
    this.schedulingLogic = schedulingLogic;
    this.displayOrder = displayOrder;
    this.isNeedQualified = isNeedQualified;
    this.isNight = isNight;
    this.isDefaultWeekdayFromHome = isDefaultWeekdayFromHome;
    this.isDefaultWeekendFromHome = isDefaultWeekendFromHome;
    //    this.rotationUsers = rotationUsers;
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
