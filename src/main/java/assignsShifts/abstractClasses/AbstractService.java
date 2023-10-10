package assignsShifts.abstractClasses;

import assignsShifts.models.Model;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractService<T extends Model> {
  @Autowired protected AbstractRepository<T> repository;

  public List<T> findAll() {
    return this.repository.findAll();
  }

  public Optional<T> create(T shiftType) {
    shiftType.setId(UUID.randomUUID().toString());

    return this.repository.save(shiftType);
  }

  public Optional<T> update(T shiftType) {
    if (this.repository.findById(shiftType.getId()).isEmpty()) {
      return Optional.empty();
    }

    return this.repository.save(shiftType);
  }

  public Optional<DeleteResult> delete(String id) {
    if (this.repository.findById(id).isEmpty()) {
      return Optional.empty();
    }

    return repository.delete(id);
  }
}
