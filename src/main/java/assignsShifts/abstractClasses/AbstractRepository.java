package assignsShifts.abstractClasses;

import assignsShifts.models.Model;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<T extends Model> {
  @Autowired protected MongoTemplate mongoTemplate;

  public List<T> findAll() {
    return this.mongoTemplate.findAll(getType());
  }

  public Optional<T> findById(String id) {
    return Optional.ofNullable(this.mongoTemplate.findById(id, getType()));
  }

  public Optional<T> findByIdInclude(String id, String... include) {
    Query query = Query.query(Criteria.where("_id").is(id));
    query.fields().include(include);

    return Optional.ofNullable(this.mongoTemplate.findOne(query, getType()));
  }

  public Optional<T> findByIdExclude(String id, String... exclude) {
    Query query = Query.query(Criteria.where("_id").is(id));
    query.fields().exclude(exclude);

    return Optional.ofNullable(this.mongoTemplate.findOne(query, getType()));
  }

  public Optional<T> save(T entity) {
    return Optional.of(this.mongoTemplate.save(entity));
  }

  public Optional<DeleteResult> delete(String id) {
    Query query = Query.query(Criteria.where("id").is(id));

    return Optional.of(this.mongoTemplate.remove(query, getType()));
  }

  protected abstract Class<T> getType();
}
