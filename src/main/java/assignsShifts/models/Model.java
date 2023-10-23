package assignsShifts.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
public abstract class Model {
  @Id protected String id;

  public Model() {
    this.id = UUID.randomUUID().toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Model model)) return false;
    return Objects.equals(getId(), model.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
