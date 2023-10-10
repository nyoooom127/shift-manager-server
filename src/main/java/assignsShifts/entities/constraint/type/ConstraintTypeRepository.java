package assignsShifts.entities.constraint.type;

import assignsShifts.abstractClasses.AbstractRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ConstraintTypeRepository extends AbstractRepository<ConstraintType> {
  @Override
  protected Class<ConstraintType> getType() {
    return ConstraintType.class;
  }
}
