/* (C) 2023 */
package com.workshop.api.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @ExceptionHandler(value = {WorkshopResponseStatusException.class})
  protected ResponseEntity<String> handleConflict(
      WorkshopResponseStatusException ex, WebRequest request) throws JsonProcessingException {

    String body = OBJECT_MAPPER.writeValueAsString(ex.getErrorResponse());

    return ResponseEntity.status(ex.getStatusCode())
        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .body(body);
  }
}
