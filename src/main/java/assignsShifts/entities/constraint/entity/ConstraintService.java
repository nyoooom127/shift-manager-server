package assignsShifts.entities.constraint.entity;

import assignsShifts.abstractClasses.AbstractService;
import assignsShifts.entities.user.entity.UserService;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConstraintService extends AbstractService<Constraint> {
  @Autowired UserService userService;

  @Override
  public Optional<Constraint> create(Constraint entity) {
    Optional<Constraint> constraint = super.create(entity);
    constraint.ifPresent(value -> userService.addConstraint(entity.getUser(), value));

    return constraint;
  }

  @Override
  public Optional<DeleteResult> delete(String id) {
    Optional<Constraint> constraint = this.repository.findById(id);

    constraint.ifPresent(value -> userService.removeConstraint(value.getUser(), id));
    return super.delete(id);
  }
}
