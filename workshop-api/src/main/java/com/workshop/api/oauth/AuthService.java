/* (C) 2024 */
package com.workshop.api.oauth;

import com.nimbusds.jwt.SignedJWT;
import com.workshop.api.error.WorkshopError;
import java.text.ParseException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

  public SignedJWT getSignedToken(String authorizationHeader) {

    if (!authorizationHeader.startsWith("Bearer ")) {
      log.error("Malformed auth header {}", authorizationHeader);
      throw WorkshopError.INVALID_AUTH_HEADER.get();
    }

    String[] parts = authorizationHeader.split(" ");
    if (parts.length != 2) {
      log.error("Malformed auth header parts {}", Arrays.toString(parts));
      throw WorkshopError.INVALID_AUTH_HEADER.get();
    }

    try {
      return SignedJWT.parse(parts[1]);
    } catch (ParseException e) {
      log.error("Invalid token value {}", parts[1]);
      throw WorkshopError.INVALID_TOKEN.get();
    }
  }
}
