package assignsShifts.entities.user.type;

import assignsShifts.abstractClasses.AbstractRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserTypeRepository extends AbstractRepository<UserType> {

  @Override
  protected Class<UserType> getType() {
    return UserType.class;
  }
}
