/* (C) 2023 */
package com.workshop.api.ping;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
  private static final String SWAGGER_PING_TAG = "Ping";

  @Operation(summary = "Ping", tags = SWAGGER_PING_TAG)
  @GetMapping(path = "/ping")
  public ResponseEntity<Void> ping() {
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
