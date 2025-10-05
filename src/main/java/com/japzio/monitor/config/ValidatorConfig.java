package com.japzio.monitor.config;

import com.japzio.monitor.model.dto.AddTargetRequest;
import com.japzio.monitor.validator.AddNewTargetRequestValidator;
import com.japzio.monitor.validator.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ValidatorConfig {

   @Bean
   public List<Validator<AddTargetRequest>> addTargetRequestValidatorList() {
       return new ArrayList<>(
               List.of(
                       new AddNewTargetRequestValidator()
               )
       );
   }

}
