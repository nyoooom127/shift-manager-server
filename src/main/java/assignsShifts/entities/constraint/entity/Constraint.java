package assignsShifts.entities.constraint.entity;

import assignsShifts.entities.constraint.type.ConstraintType;
import assignsShifts.models.Model;
import assignsShifts.entities.user.entity.User;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Document("constraints")
@AllArgsConstructor
@Builder
public class Constraint extends Model {
  @DBRef private ConstraintType type;
  @NonNull private Date startDate;
  @NonNull private Date endDate;
  private String comment;
  @DBRef @NonNull private User user;

  public Constraint(String id, ConstraintType type, @NonNull Date startDate, @NonNull Date endDate, String comment, @NonNull User user) {
    super(id);
    this.type = type;
    this.startDate = startDate;
    this.endDate = endDate;
    this.comment = comment;
    this.user = user;
  }
}
