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
    shift.ifPresent(value -> userService.addShift(entity.getUser(), value));

    return shift;
  }

  @Override
  public Optional<Shift> update(Shift entity) {
    Optional<Shift> existingShift = findById(entity.getId());

    if (existingShift.isEmpty()) {
      return Optional.empty();
    }

    if (!existingShift.get().getUser().equals(entity.getUser())) {
      userService.removeShift(existingShift.get().getUser(), existingShift.get().getId());
      userService.addShift(entity.getUser(), entity);
    }

    return super.update(entity);
  }

  @Override
  public Optional<DeleteResult> delete(String id) {
    Optional<Shift> shift = this.repository.findById(id);

    shift.ifPresent(value -> userService.removeShift(value.getUser(), id));
    return super.delete(id);
  }
}
