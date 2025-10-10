package com.japzio.monitor.validator;

import com.japzio.monitor.exception.AddNewTargetException;
import com.japzio.monitor.model.SupportedMethods;
import com.japzio.monitor.model.external.AddTargetRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class AddTelnetTargetRequestValidator implements Validator<AddTargetRequest> {

    @Override
    public void validate(AddTargetRequest addTargetRequest, List<String> errors) {
        if(addTargetRequest.getMethod().equals(SupportedMethods.TELNET)) {
            var requestEndpoint = addTargetRequest.getEndpoint();
            try {
                String[] manualSplit = requestEndpoint.split(":");
                if(manualSplit.length != 2){
                    throw new IllegalArgumentException("telnet invalid endpoint format");
                }
                new InetSocketAddress(manualSplit[0], Integer.parseInt(manualSplit[1]));
            } catch (AddNewTargetException|IllegalArgumentException severalException) {
                log.warn("exception occurred. {}", severalException.getMessage());
                throw new AddNewTargetException("validation failed for telnet type check");
            }
        }
    }
}
