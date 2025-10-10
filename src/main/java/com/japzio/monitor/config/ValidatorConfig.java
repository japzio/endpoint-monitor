package com.japzio.monitor.config;

import com.japzio.monitor.model.external.AddTargetRequest;
import com.japzio.monitor.validator.AddCurlTargetRequestValidator;
import com.japzio.monitor.validator.AddTelnetTargetRequestValidator;
import com.japzio.monitor.validator.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.japzio.monitor.model.SupportedMethods.CURL;
import static com.japzio.monitor.model.SupportedMethods.TELNET;

@Configuration
public class ValidatorConfig {

   @Bean
   public List<Validator<AddTargetRequest>> addTargetRequestValidatorList() {
       return new ArrayList<>(
               List.of(
                       new AddCurlTargetRequestValidator(),
                       new AddTelnetTargetRequestValidator()
               )
       );
   }

    @Bean
    public Map<String, Validator<AddTargetRequest>> addTargetRequestValidatorMap() {
        return new HashMap<>(
                Map.of(
                        CURL.toString(), new AddCurlTargetRequestValidator(),
                        TELNET.toString(), new AddTelnetTargetRequestValidator()
                )
        );
    }

}
