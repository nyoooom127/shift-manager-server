package assignsShifts.entities.shift.entity;

import assignsShifts.abstractClasses.AbstractRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ShiftRepository extends AbstractRepository<Shift> {
  @Override
  protected Class<Shift> getType() {
    return Shift.class;
  }
}
