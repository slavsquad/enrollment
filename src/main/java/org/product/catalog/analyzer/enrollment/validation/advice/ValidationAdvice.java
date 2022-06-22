package org.product.catalog.analyzer.enrollment.validation.advice;

import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.validation.exception.ArgumentNotValidException;
import org.product.catalog.analyzer.enrollment.validation.exception.ExceptionResponse;
import org.product.catalog.analyzer.enrollment.validation.exception.NotFindNodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class ValidationAdvice {

    @ExceptionHandler(ArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(ArgumentNotValidException e) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Validation Failed");
        log.info("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotFindNodeException.class)
    public ResponseEntity<ExceptionResponse> handleException(NotFindNodeException e) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.NOT_FOUND.value(), "Item not found");
        log.info("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Validation Failed");
        log.info("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
