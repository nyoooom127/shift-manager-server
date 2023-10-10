package assignsShifts.entities.shift.entity;

import assignsShifts.abstractClasses.AbstractService;
import assignsShifts.entities.user.entity.UserService;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShiftService extends AbstractService<Shift> {
  @Autowired UserService userService;

  @Override
  public Optional<Shift> create(Shift entity) {
    Optional<Shift> shift = super.create(entity);
    shift.ifPresent(value -> userService.addShift(entity.getUser().getId(), value));

    return shift;
  }

  @Override
  public Optional<DeleteResult> delete(String id) {
    Optional<Shift> shift = this.repository.findById(id);

    shift.ifPresent(value -> userService.removeShift(value.getUser().getId(), id));
    return super.delete(id);
  }
}
