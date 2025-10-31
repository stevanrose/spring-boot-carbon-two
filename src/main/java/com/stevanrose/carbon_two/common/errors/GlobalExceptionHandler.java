package com.stevanrose.carbon_two.common.errors;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorBody handleNotFound(EntityNotFoundException ex) {
    return new ErrorBody("NOT_FOUND", ex.getMessage());
  }

  public record ErrorBody(String code, String message) {}
}
