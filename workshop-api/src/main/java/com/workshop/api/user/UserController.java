/* (C) 2023 */
package com.workshop.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserController {
  private static final String SWAGGER_USERS_TAG = "Users";

  @Autowired private UserService userService;

  @Operation(summary = "Create User", tags = SWAGGER_USERS_TAG, description = "Create a new user")
  @PostMapping(
      path = "/users",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserResponse> createUser(@RequestBody UserRequestBody user) {
    UserResponse body = userService.createUser(user.getModel()).getResponse();
    return new ResponseEntity<>(body, HttpStatus.CREATED);
  }

  @Operation(
      summary = "Get Users",
      tags = SWAGGER_USERS_TAG,
      description =
          "Get Users. If no id is passed as a request parameter the response will include all available users")
  @GetMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<UserResponse>> getUsers(
      @RequestParam(name = "id", required = false, defaultValue = "") List<String> ids) {
    List<UserResponse> users =
        userService.getUsers(ids).stream().map(UserModel::getResponse).toList();
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @Operation(summary = "Delete User", tags = SWAGGER_USERS_TAG, description = "Delete user by id")
  @DeleteMapping(path = "/users/{id}")
  public ResponseEntity<Void> deleteUser(
      @NotBlank
          @Schema(name = "id", description = "The user's id", type = "string", example = "user_id")
          @PathVariable(name = "id")
          String id) {
    userService.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
