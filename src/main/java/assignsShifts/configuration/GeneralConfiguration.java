package assignsShifts.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class GeneralConfiguration {

  @Bean
  public Gson gson() {
    return new Gson();
  }

//  @Bean
//  public ObjectMapper objectMapper() {
//    ObjectMapper objectMapper = new ObjectMapper();
//    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
//    objectMapper.setDateFormat(simpleDateFormat);
//
//    return objectMapper;
//  }

  //todo Objectmapper - date: simpledateformat
}
