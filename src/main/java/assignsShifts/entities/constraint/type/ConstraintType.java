package assignsShifts.entities.constraint.type;

import assignsShifts.models.Model;
import com.mongodb.lang.NonNull;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document("constraintTypes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConstraintType extends Model {
  @NonNull private String name;

  public ConstraintType(String id, @NonNull String name) {
    super(id);
    this.name = name;
  }
}
