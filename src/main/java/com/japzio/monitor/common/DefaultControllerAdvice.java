package com.japzio.monitor.common;

import com.japzio.monitor.exception.AddNewTargetException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class DefaultControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest()
                .body(
                        Map.of(
                                "status", "Bad Request",
                                "details", exception.getBody()
                        )
                );
    }

    @ExceptionHandler(AddNewTargetException.class)
    ResponseEntity<Map<String, Object>> handleAddNewTargetException(AddNewTargetException exception) {
        return ResponseEntity.badRequest()
                .body(
                        Map.of(
                                "status", "Bad Request",
                                "details", exception.getMessage()
                        )
                );
    }

}
