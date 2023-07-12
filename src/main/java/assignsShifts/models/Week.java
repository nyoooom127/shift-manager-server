package assignsShifts.models;

import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@Document("week")
public class Week {
  @Id private String UUIDString;
  @NonNull private List<Day> dayList;
  private String comment;
  private boolean isClosed;
  private boolean isActive;

  @Data
  @AllArgsConstructor
  public static class Day {
    @NonNull private long dateInMillis;
    @NonNull private Shift ojtShift;
    @NonNull private Shift primaryShift;
    @NonNull private Shift secondaryShift;
    @NonNull private Shift levShift;
    @NonNull private Shift integrationShift;
    private String comment;
  }

  @Data
  @AllArgsConstructor
  public static class Shift {
    @DBRef private User user;
    private String comment;
    @NonNull private boolean isNeeded;
  }
}
