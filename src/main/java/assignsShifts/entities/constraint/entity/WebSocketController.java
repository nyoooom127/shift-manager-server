package assignsShifts.entities.constraint.entity;

import assignsShifts.JWT.VerifierRequest;
import assignsShifts.entities.constraint.type.ConstraintType;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
//@RequestMapping("/constraint")
public class WebSocketController {

  @Autowired
  SimpMessagingTemplate template;

  @PostMapping("/send")
  public ResponseEntity<Void> sendMessage(@RequestBody ConstraintType constraintType) {
    template.convertAndSend("/topic/message", constraintType);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @MessageMapping("/sendMessage")
  public void receiveMessage(@Payload ConstraintType constraintType){

  }

  @SendTo("/topic/message")
  public ConstraintType broadcastConstraintType(@Payload ConstraintType constraintType){
    return constraintType;
  }
}
