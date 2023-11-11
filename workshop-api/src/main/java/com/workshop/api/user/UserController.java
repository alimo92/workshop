/* (C) 2023 */
package com.workshop.api.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

  @Autowired private UserService userService;

  @PostMapping(
      path = "/users",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserResponse> createUser(@RequestBody UserRequestBody user) {
    UserResponse body = userService.createUser(user.getModel()).getResponse();
    return new ResponseEntity<>(body, HttpStatus.CREATED);
  }

  @GetMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<UserResponse>> getUsers(
      @RequestParam(name = "id", required = false, defaultValue = "") List<String> ids) {
    List<UserResponse> users =
        userService.getUsers(ids).stream().map(UserModel::getResponse).toList();
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @DeleteMapping(path = "/users/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") String id) {
    userService.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
