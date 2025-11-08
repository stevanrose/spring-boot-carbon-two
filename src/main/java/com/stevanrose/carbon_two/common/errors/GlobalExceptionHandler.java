package com.stevanrose.carbon_two.common.errors;

import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<?> onConstraint(DataIntegrityViolationException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(Map.of("error", "Conflict: constraint violated (e.g., duplicate email)"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> onValidation(MethodArgumentNotValidException ex) {
    var first = ex.getBindingResult().getFieldErrors().stream().findFirst();
    String msg =
        first.map(f -> f.getField() + " " + f.getDefaultMessage()).orElse("Validation error");
    return ResponseEntity.badRequest().body(Map.of("error", msg));
  }

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorBody handleNotFound(EntityNotFoundException ex) {
    return new ErrorBody("NOT_FOUND", ex.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorBody handleConflict(IllegalStateException ex) {
    return new ErrorBody("CONFLICT", ex.getMessage());
  }

  public record ErrorBody(String code, String message) {}
}
