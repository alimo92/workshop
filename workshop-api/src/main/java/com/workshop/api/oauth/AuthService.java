/* (C) 2024 */
package com.workshop.api.oauth;

import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {
  public Optional<SignedJWT> getSignedToken(String authorizationHeader) {

    if (!authorizationHeader.startsWith("Bearer ")) {
      parsingErrorLog(authorizationHeader);
      return Optional.empty();
    }

    String[] parts = authorizationHeader.split(" ");
    if (parts.length != 2) {
      parsingErrorLog(authorizationHeader);
      return Optional.empty();
    }

    try {
      return Optional.of(SignedJWT.parse(parts[1]));
    } catch (ParseException e) {
      parsingErrorLog(authorizationHeader);
      return Optional.empty();
    }
  }

  private static void parsingErrorLog(String authorizationHeader) {
    log.error("Error parsing signed token from header value {}", authorizationHeader);
  }
}
