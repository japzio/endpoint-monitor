package com.japzio.monitor.validator;

import com.japzio.monitor.model.SupportedMethods;
import com.japzio.monitor.model.external.AddTargetRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

@Slf4j
public class AddCurlTargetRequestValidator implements Validator<AddTargetRequest> {

    @Override
    public void validate(AddTargetRequest addTargetRequest, List<String> errors) {
        if(addTargetRequest.getMethod().equals(SupportedMethods.CURL)) {
            try {
                new URL(addTargetRequest.getEndpoint());
            } catch (MalformedURLException exception) {
                var message = MessageFormat.format("invalid endpoint format {0} for method {1}", addTargetRequest.getEndpoint(), addTargetRequest.getMethod());
                errors.add(message);
                log.warn(message);
            }
        }
    }
}
