/* (C) 2023-2024 */
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
  NOT_FOUND(new ErrorResponse("not_found", "resources not found"), HttpStatus.NOT_FOUND),
  INVALID_AUTH_HEADER(
      new ErrorResponse("invalid_auth_header", "invalid auth header"), HttpStatus.BAD_REQUEST),
  INVALID_TOKEN(new ErrorResponse("invalid_token", "invalid token"), HttpStatus.BAD_REQUEST),
  INVALID_TOKEN_CLAIMS(
      new ErrorResponse("invalid_token_claims", "invalid token claims"), HttpStatus.BAD_REQUEST);

  private final ErrorResponse errorResponse;
  private final HttpStatus httpStatus;

  public WorkshopResponseStatusException get() {
    return new WorkshopResponseStatusException(httpStatus, errorResponse);
  }
}
