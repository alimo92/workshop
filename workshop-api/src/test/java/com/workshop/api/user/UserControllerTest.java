/* (C) 2024 */
package com.workshop.api.user;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.workshop.api.key.PublicKeysService;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

  @Autowired private WebTestClient webTestClient;
  @Autowired private ResourceLoader resourceLoader;

  @Autowired private PublicKeysService publicKeysService;

  @Test
  void test() throws ParseException, JOSEException, IOException {

    String jwks =
        new String(
            Files.readAllBytes(
                resourceLoader.getResource("classpath:jwk/jwks.json").getFile().toPath()));

    JWKSet keys = JWKSet.parse(jwks);
    JWK key = keys.getKeyByKeyId("sig-2024-02-19T06:29:00Z");

    JWSHeader header =
        new JWSHeader.Builder((JWSAlgorithm.parse(key.getAlgorithm().toString())))
            .keyID(key.getKeyID())
            .build();

    Map<String, String> map = Map.of("key1", "value1", "key2", "value2");

    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .issuer("issuer_test")
            .issueTime(Date.from(Instant.now()))
            .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
            .claim("testMap", map)
            .build();

    SignedJWT signedJWT = new SignedJWT(header, claimsSet);
    JWSSigner signer = publicKeysService.getSigner(key);
    signedJWT.sign(signer);

    String token = signedJWT.serialize();
    System.out.println(token);

    UserRequestBody userRequest = new UserRequestBody("first", "last", 0);
    webTestClient
        .post()
        .uri("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(userRequest)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(UserResponse.class)
        .consumeWith(
            result -> {
              UserResponse userResponse = result.getResponseBody();
              assertNotNull("userResponse not null", userResponse);
              if (userResponse != null) {
                assertEquals(
                    "firstName assertion", userRequest.firstName(), userResponse.firstName());
                assertEquals("lastName assertion", userRequest.lastName(), userResponse.lastName());
                assertEquals("balance assertion", userRequest.balance(), userResponse.balance());
              }
            });
  }
}
