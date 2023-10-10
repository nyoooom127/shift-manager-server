package assignsShifts.entities.week.type;

import assignsShifts.repository.AbstractRepository;
import org.springframework.stereotype.Repository;

@Repository
public class WeekTypeRepository extends AbstractRepository<WeekType> {

  @Override
  protected Class<WeekType> getType() {
    return WeekType.class;
  }
}
