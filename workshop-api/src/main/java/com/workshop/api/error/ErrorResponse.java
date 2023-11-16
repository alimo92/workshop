/* (C) 2023 */
package com.workshop.api.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor()
public class ErrorResponse {
  @JsonProperty("code")
  String code;

  @JsonProperty("message")
  String message;
}
