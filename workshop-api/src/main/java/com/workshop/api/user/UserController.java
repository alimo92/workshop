/* (C) 2023-2024 */
package com.workshop.api.user;

import com.nimbusds.jwt.SignedJWT;
import com.workshop.api.error.WorkshopError;
import com.workshop.api.exception.WorkshopJWKException;
import com.workshop.api.key.PublicKeysService;
import com.workshop.api.oauth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserController {
  private static final String SWAGGER_USERS_TAG = "Users";

  @Autowired private UserService userService;

  @Autowired private AuthService authService;

  @Autowired private PublicKeysService publicKeysService;

  @Operation(summary = "Create User", tags = SWAGGER_USERS_TAG, description = "Create a new user")
  @PostMapping(
      path = "/users",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserResponse> createUser(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @RequestBody UserRequestBody user)
      throws WorkshopJWKException {

    SignedJWT token = authService.getSignedToken(authorizationHeader);

    boolean isVerified = publicKeysService.verifyToken(token);
    if (!isVerified) {
      log.error("Invalid token value");
      throw WorkshopError.INVALID_TOKEN.get();
    }

    boolean areClaimsVerified = publicKeysService.verifyClaims(token);
    if (!areClaimsVerified) {
      log.error("Invalid token claims");
      throw WorkshopError.INVALID_TOKEN_CLAIMS.get();
    }

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
    Optional<UserModel> user = userService.getUser(id);

    if (user.isEmpty()) {
      throw WorkshopError.NOT_FOUND.get();
    }

    userService.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "Get User", tags = SWAGGER_USERS_TAG, description = "Get user by id")
  @GetMapping(path = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserResponse> getUser(
      @NotBlank
          @Schema(name = "id", description = "The user's id", type = "string", example = "user_id")
          @PathVariable(name = "id")
          String id) {
    Optional<UserModel> user = userService.getUser(id);

    if (user.isEmpty()) {
      throw WorkshopError.NOT_FOUND.get();
    }

    return new ResponseEntity<>(user.get().getResponse(), HttpStatus.OK);
  }
}
