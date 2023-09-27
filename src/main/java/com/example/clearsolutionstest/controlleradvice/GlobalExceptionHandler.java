package com.example.clearsolutionstest.controlleradvice;


import com.example.clearsolutionstest.exception.ValidationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(
      ValidationException exception) {
    BindingResult bindingResult = exception.getBindingResult();
    Map<String, Object> response = new HashMap<>();
    response.put("message", "Data validation error");
    response.put("errors", bindingResult.getAllErrors());
    return ResponseEntity.badRequest().body(response);
  }
}