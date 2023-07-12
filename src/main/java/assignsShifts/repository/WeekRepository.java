package assignsShifts.repository;

import assignsShifts.models.Week;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WeekRepository {
  @Autowired private MongoTemplate mongoTemplate;

  public List<Week> findAll() {
    return this.mongoTemplate.findAll(Week.class);
  }

  public Optional<Week> findById(String uuidString) {
    return Optional.ofNullable(this.mongoTemplate.findById(uuidString, Week.class));
  }

  public Optional<Week> findActive() {
    Query query = Query.query(Criteria.where("isActive").is(true));

    return Optional.ofNullable(this.mongoTemplate.findOne(query, Week.class));
  }

  public Optional<Week> save(Week week) {
    return Optional.of(this.mongoTemplate.save(week));
  }
}
