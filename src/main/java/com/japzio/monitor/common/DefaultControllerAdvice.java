package com.japzio.monitor.common;

import com.japzio.monitor.exception.AddNewTargetException;
import com.japzio.monitor.exception.TargetNotFoundException;
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
        return ResponseEntity.internalServerError()
                .body(
                        Map.of(
                                "status", "Server Error",
                                "details", exception.getMessage()
                        )
                );
    }

    @ExceptionHandler(TargetNotFoundException.class)
    ResponseEntity<Map<String, Object>> handleTargetNotFoundException(TargetNotFoundException exception) {
        return ResponseEntity.status(404)
                .body(
                        Map.of(
                                "status", "Not Found",
                                "details", exception.getMessage()
                        )
                );
    }

}
