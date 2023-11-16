/* (C) 2023 */
package com.workshop.api.error;

import com.workshop.api.exception.WorkshopResponseStatusException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum WorkshopError {
  INTERNAL_ERROR(
      new ErrorResponse("internal_server_error", "server internal error"),
      HttpStatus.INTERNAL_SERVER_ERROR),
  NOT_FOUND(new ErrorResponse("not_found", "resources not found"), HttpStatus.NOT_FOUND);

  private final ErrorResponse errorResponse;
  private final HttpStatus httpStatus;

  public WorkshopResponseStatusException getResponse() {
    return new WorkshopResponseStatusException(httpStatus, errorResponse);
  }
}
