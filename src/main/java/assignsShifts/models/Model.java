package assignsShifts.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public abstract class Model {
  @Id protected String id;

  public Model() {
    this.id = UUID.randomUUID().toString();
  }
}
