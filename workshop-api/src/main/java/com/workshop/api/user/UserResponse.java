/* (C) 2023 */
package com.workshop.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponse(
    @JsonProperty("id") String id,
    @JsonProperty("firstName") String firstName,
    @JsonProperty("lastName") String lastName) {}
