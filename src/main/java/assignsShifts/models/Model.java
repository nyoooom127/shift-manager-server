package assignsShifts.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@AllArgsConstructor
//@Builder
public abstract class Model {
  @Id protected String id;

  public Model() {
    this.id = UUID.randomUUID().toString();
  }
}
