package assignsShifts.entities.constraint.entity;

import assignsShifts.repository.AbstractRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ConstraintRepository extends AbstractRepository<Constraint> {
  @Override
  protected Class<Constraint> getType() {
    return Constraint.class;
  }
}
