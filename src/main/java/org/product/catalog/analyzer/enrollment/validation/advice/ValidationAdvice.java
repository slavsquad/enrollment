package org.product.catalog.analyzer.enrollment.validation.advice;

import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.validation.exception.ArgumentNotValidException;
import org.product.catalog.analyzer.enrollment.validation.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ValidationAdvice {

    @ExceptionHandler(ArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(ArgumentNotValidException e) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Validation Failed");
        log.info("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Validation Failed");
        log.info("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
