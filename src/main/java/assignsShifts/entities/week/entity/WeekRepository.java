package assignsShifts.entities.week.entity;

import assignsShifts.abstractClasses.AbstractRepository;
import org.springframework.stereotype.Repository;

@Repository
public class WeekRepository extends AbstractRepository<Week> {

  @Override
  protected Class<Week> getType() {
    return Week.class;
  }
}
