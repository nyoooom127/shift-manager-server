package assignsShifts.entities.constraint.entity;

import assignsShifts.entities.constraint.type.ConstraintType;
import assignsShifts.entities.user.entity.User;
import assignsShifts.models.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mongodb.lang.NonNull;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Document("constraints")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Constraint extends Model {
  @DBRef private ConstraintType type;
  @NonNull private Date startDate;
  @NonNull private Date endDate;
  private String comment;
  @NonNull private String user;

  public Constraint(
      String id,
      ConstraintType type,
      @NonNull Date startDate,
      @NonNull Date endDate,
      String comment,
      @NonNull String user) {
    super(id);
    this.type = type;
    this.startDate = startDate;
    this.endDate = endDate;
    this.comment = comment;
    this.user = user;
  }
}
