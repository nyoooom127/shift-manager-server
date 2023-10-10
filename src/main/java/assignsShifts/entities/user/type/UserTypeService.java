package assignsShifts.entities.user.type;

import assignsShifts.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class UserTypeService extends AbstractService<UserType> {
  //  @Autowired private ShiftTypeRepository shiftTypeRepository;
  //  @Autowired private UserRepository userRepository;
  //  @Autowired private ScoreCalculator scoreCalculator;
  //
  //  public List<ShiftType> findAll() {
  //    return this.shiftTypeRepository.findAll();
  //  }
  //
  //  public Optional<ShiftType> createShiftType(ShiftType shiftType) {
  //    shiftType.setId(UUID.randomUUID().toString());
  //
  //    return this.shiftTypeRepository.save(shiftType);
  //  }
  //
  //  public Optional<ShiftType> updateShiftType(ShiftType shiftType) {
  //    if (this.shiftTypeRepository.findById(shiftType.getId()).isEmpty()) {
  //      return Optional.empty();
  //    }
  //
  //    return this.shiftTypeRepository.save(shiftType);
  //  }
  //
  //  public Optional<DeleteResult> deleteShiftType(String id){
  //    if(this.shiftTypeRepository.findById(id).isEmpty()){
  //      return Optional.empty();
  //    }
  //
  //    return shiftTypeRepository.delete(id);
  //  }
}
