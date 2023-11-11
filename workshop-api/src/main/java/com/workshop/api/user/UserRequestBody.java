/* (C) 2023 */
package com.workshop.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record UserRequestBody(
    @NotBlank @JsonProperty("firstName") String firstName,
    @NotBlank @JsonProperty("lastName") String lastName) {

  public UserModel getModel() {
    return new UserModel(UUID.randomUUID().toString(), firstName, lastName);
  }
}
