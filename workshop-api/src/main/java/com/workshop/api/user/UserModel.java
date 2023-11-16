/* (C) 2023 */
package com.workshop.api.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
public class UserModel {
  @Id private String id;
  private String firstName;
  private String lastName;
  private int balance;

  public UserResponse getResponse() {
    return new UserResponse(id, firstName, lastName, balance);
  }
}
