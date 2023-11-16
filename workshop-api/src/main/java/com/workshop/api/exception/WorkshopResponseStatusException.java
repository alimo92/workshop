/* (C) 2023 */
package com.workshop.api.exception;

import com.workshop.api.error.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class WorkshopResponseStatusException extends ResponseStatusException {

  private final transient ErrorResponse errorResponse;

  public WorkshopResponseStatusException(HttpStatusCode status, ErrorResponse errorResponse) {
    super(status);
    this.errorResponse = errorResponse;
  }
}
