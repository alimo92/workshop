/* (C) 2023 */
package com.workshop.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record UserRequestBody(
    @JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName) {

  public UserModel getModel() {
    return new UserModel(UUID.randomUUID().toString(), firstName, lastName);
  }
}
