package assignsShifts.models;

import assignsShifts.models.enums.ShiftOptionEnum;
import assignsShifts.models.enums.UserPermissionsEnum;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Data
@Document("users")
@AllArgsConstructor
@Builder
public class User {
  @Id private String id;
  @NonNull private String fullName;
  private AuthorizationData authorizationData;
  private List<User.Constraint> constraints;
  private List<ShiftOptionEnum> shiftOptions;
  private double ojtScore;
  private double levScore;
  private double integrationScore;
  private double galaxyScore;

  public void addOjtScore(double score) {
    this.ojtScore += score;
  }

  public void addLevScore(double score) {
    this.levScore += score;
  }

  public void addGalaxyScore(double score) {
    this.galaxyScore += score;
  }

  public void addIntegrationScoreScore(double score) {
    this.integrationScore += score;
  }

  public List<User.Constraint> getConstraints() {
    if (this.constraints == null) {
      this.constraints = new ArrayList<>();
    }

    return this.constraints;
  }

  public List<ShiftOptionEnum> getShiftOptions() {
    if (this.shiftOptions == null) {
      this.shiftOptions = new ArrayList<>();
    }

    return this.shiftOptions;
  }

  private boolean isCalendarEquals(Calendar calendar1, Calendar calendar2) {
    if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
        && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
        && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)) {
      return true;
    }

    return false;
  }

  public boolean isHaveConstraint(Calendar calendar) {
    for (User.Constraint constraint : this.getConstraints()) {
      if (this.isCalendarEquals(constraint.getDate(), calendar)) {
        return true;
      }
    }

    return false;
  }

  @Data
  @AllArgsConstructor
  public static class Constraint {
    private String uuidString;
    @NonNull private Calendar date;
  }

  @Data
  @AllArgsConstructor
  public static class AuthorizationData {
    @NonNull private String userName;
    @NonNull private String password;
    @NonNull private UserPermissionsEnum userPermissions;
  }
}
