package assignsShifts.entities.shift.entity;

import assignsShifts.services.AbstractService;
import org.springframework.stereotype.Service;

@Service
public class ShiftService extends AbstractService<Shift> {
  //  @Autowired private ShiftRepository shiftRepository;
  //  @Autowired private UserRepository userRepository;
  //  @Autowired private ScoreCalculator scoreCalculator;

  //  public List<Shift> findAll() {
  //    return this.shiftRepository.findAll();
  //  }

  //  public Optional<Shift> createShift(Shift shift) {
  //    shift.setId(UUID.randomUUID().toString());
  //
  //    return this.shiftRepository.save(shift);
  //  }
  //
  //  public Optional<Shift> updateShift(Shift shift) {
  //    if (this.shiftRepository.findById(shift.getId()).isEmpty()) {
  //      return Optional.empty();
  //    }
  //
  //    return this.shiftRepository.save(shift);
  //  }
  //
  //  public Optional<DeleteResult> deleteShift(String id){
  //    if(this.shiftRepository.findById(id).isEmpty()){
  //      return Optional.empty();
  //    }
  //
  //    return shiftRepository.delete(id);
  //  }
}
