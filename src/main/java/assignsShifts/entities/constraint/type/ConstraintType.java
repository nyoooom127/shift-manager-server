package assignsShifts.entities.constraint.type;

import assignsShifts.models.Model;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Document("constraintTypes")
@AllArgsConstructor
@Builder
public class ConstraintType extends Model {
  @NonNull private String name;

  public ConstraintType(String id, @NonNull String name) {
    super(id);
    this.name = name;
  }
}
