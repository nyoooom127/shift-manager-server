package assignsShifts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication
//@EnableMongoRepositories
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
public class AssignsShiftsApplication {
  public static void main(String[] args) {
    SpringApplication.run(AssignsShiftsApplication.class, args);
  }
}
