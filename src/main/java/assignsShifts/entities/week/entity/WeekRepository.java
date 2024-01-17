package assignsShifts.entities.week.entity;

import assignsShifts.abstractClasses.AbstractRepository;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class WeekRepository extends AbstractRepository<Week> {

  @Override
  protected Class<Week> getType() {
    return Week.class;
  }

  public List<Week> findAllByDate(Date startDate) {
    Query query = Query.query(Criteria.where("startDate").gte(startDate));

    return this.mongoTemplate.find(query, getType());
  }
}
