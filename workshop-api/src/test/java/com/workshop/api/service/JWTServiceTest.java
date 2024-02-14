/* (C) 2024 */
package com.workshop.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class JWTServiceTest {

  private static final int DEFAULT_TIMEOUT_MS = 5_000;

  @Autowired WebClient jwksClient;

  @Test
  public void test() {
    Mono<String> response =
        jwksClient
            .get()
            .uri("/oauth/jwks/rsa.json")
            .exchangeToMono(
                clientResponse -> {
                  if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                    return clientResponse.bodyToMono(String.class);
                  } else if (clientResponse.statusCode().is4xxClientError()) {
                    return Mono.just("Error response");
                  } else {
                    return clientResponse.createException().flatMap(Mono::error);
                  }
                });

    System.out.println(response.block(Duration.of(DEFAULT_TIMEOUT_MS, ChronoUnit.MILLIS)));
  }

  @Test
  void test_HS256() throws JOSEException, ParseException {

    // Create an HMAC-protected JWS object with a string payload
    JWSObject jwsObject =
        new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload("Hello, world!"));

    // We need a 256-bit key for HS256 which must be pre-shared
    byte[] sharedKey = new byte[32];
    new SecureRandom().nextBytes(sharedKey);

    JWSVerifier verifier = new MACVerifier(sharedKey);
    MACSigner signer = new MACSigner(sharedKey);

    // Apply the HMAC to the JWS object
    jwsObject.sign(signer);

    // Output in URL-safe format
    String token = jwsObject.serialize();
    System.out.println(token);

    // To parse the JWS and verify it, e.g. on client-side
    jwsObject = JWSObject.parse(token);

    assertTrue(jwsObject.verify(verifier));

    assertEquals("Hello, world!", jwsObject.getPayload().toString());
  }

  @Test
  void test_RSA() throws JOSEException, ParseException {
    // RSA signatures require a public and private RSA key pair,
    // the public key must be made known to the JWS recipient to
    // allow the signatures to be verified
    RSAKey rsaJWK = new RSAKeyGenerator(2048).keyID("123").generate();
    RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();

    // Create RSA-signer with the private key
    JWSSigner signer = new RSASSASigner(rsaJWK);

    // Prepare JWS object with simple string as payload
    JWSObject jwsObject =
        new JWSObject(
            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
            new Payload("In RSA we trust!"));

    // Compute the RSA signature
    jwsObject.sign(signer);

    // To serialize to compact form, produces something like
    // eyJhbGciOiJSUzI1NiJ9.SW4gUlNBIHdlIHRydXN0IQ.IRMQENi4nJyp4er2L
    // mZq3ivwoAjqa1uUkSBKFIX7ATndFF5ivnt-m8uApHO4kfIFOrW7w2Ezmlg3Qd
    // maXlS9DhN0nUk_hGI3amEjkKd0BWYCB8vfUbUv0XGjQip78AI4z1PrFRNidm7
    // -jPDm5Iq0SZnjKjCNS5Q15fokXZc8u0A
    String s = jwsObject.serialize();

    System.out.println(s);

    // To parse the JWS and verify it, e.g. on client-side
    jwsObject = JWSObject.parse(s);

    JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);

    assertTrue(jwsObject.verify(verifier));

    assertEquals("In RSA we trust!", jwsObject.getPayload().toString());
  }
}
