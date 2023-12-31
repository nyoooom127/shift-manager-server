package assignsShifts.abstractClasses;

import assignsShifts.models.Model;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractService<T extends Model> {
  @Autowired protected AbstractRepository<T> repository;

  public List<T> findAll() {
    return this.repository.findAll();
  }

  public Optional<T> findById(String id) {
    return this.repository.findById(id);
  }

  public Optional<T> create(T entity) {
    if (Objects.isNull(entity.getId())) {
      entity.setId(UUID.randomUUID().toString());
    }

    return this.repository.save(entity);
  }

  public Optional<T> update(T entity) {
    if (this.repository.findById(entity.getId()).isEmpty()) {
      return Optional.empty();
    }

    return this.repository.save(entity);
  }

  public Optional<T> upsert(T entity) {
    if (this.repository.findById(entity.getId()).isEmpty()) {
      return create(entity);
    }

    return update(entity);
  }

  public Optional<DeleteResult> delete(String id) {
    if (this.repository.findById(id).isEmpty()) {
      return Optional.empty();
    }

    return repository.delete(id);
  }
}
