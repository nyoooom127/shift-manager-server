package assignsShifts.entities.shift.type;

import assignsShifts.abstractClasses.AbstractRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ShiftTypeRepository extends AbstractRepository<ShiftType> {
  @Override
  protected Class<ShiftType> getType() {
    return ShiftType.class;
  }
}
