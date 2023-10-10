package assignsShifts.entities.shift.type;

import assignsShifts.repository.AbstractRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ShiftTypeRepository extends AbstractRepository<ShiftType> {
  @Override
  protected Class<ShiftType> getType() {
    return ShiftType.class;
  }
}
