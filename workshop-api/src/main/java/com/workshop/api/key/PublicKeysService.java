/* (C) 2024 */
package com.workshop.api.key;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.workshop.api.exception.WorkshopJWKException;
import jakarta.annotation.PostConstruct;
import java.text.ParseException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PublicKeysService {
  private static final Duration BLOCK_TIMEOUT = Duration.of(60, ChronoUnit.SECONDS);
  private static final AtomicReference<Map<String, JWSVerifier>> verifiersCache =
      new AtomicReference<>();

  @Autowired private WebClient jwkClient;

  @Scheduled(cron = "0 * * * * *", zone = "UTC")
  private void updateCachedVerifiersTask() {
    try {
      log.info("Updating cached verifiers...");
      updateCachedVerifiers();
    } catch (Exception e) {
      log.error("Failed to update cached verifiers", e);
    }
  }

  public boolean verifyToken(JWSObject token) {
    try {
      String keyId = token.getHeader().getKeyID();
      JWSVerifier verifier = verifiersCache.get().get(keyId);
      if (verifier == null) {
        log.error("Unknown key with id = {}", keyId);
        return false;
      }
      return token.verify(verifiersCache.get().get(keyId));
    } catch (JOSEException e) {
      log.warn("Invalid token. Failed verification");
      return false;
    }
  }

  @PostConstruct
  private void updateCachedVerifiers() throws Exception {
    JWKSet publicKeys = getPublicKeys().block(BLOCK_TIMEOUT);
    Map<String, JWSVerifier> verifiers = createVerifiers(publicKeys);
    log.info("keyIds : {}", verifiers.keySet());
    verifiersCache.set(verifiers);
  }

  public Mono<JWKSet> getPublicKeys() {
    return jwkClient
        .get()
        .uri("/oauth/jwks/rsa.json")
        .retrieve()
        .bodyToMono(String.class)
        .map(
            jwkSetStr -> {
              try {
                return JWKSet.parse(jwkSetStr);
              } catch (ParseException e) {
                throw new RuntimeException(e);
              }
            });
  }

  private Map<String, JWSVerifier> createVerifiers(JWKSet publicKeys) throws Exception {
    Map<String, JWSVerifier> verifiers = new LinkedHashMap<>();
    for (JWK currentKey : publicKeys.getKeys()) {
      KeyType keyType = currentKey.getKeyType();
      String keyId = currentKey.getKeyID();
      JWSVerifier verifier;

      if (keyType.equals(KeyType.RSA)) {
        verifier = new RSASSAVerifier(currentKey.toRSAKey().toRSAPublicKey());
      } else if (keyType.equals(KeyType.EC)) {
        verifier = new ECDSAVerifier(currentKey.toECKey().toECPublicKey());
      } else if (keyType.equals(KeyType.OCT)) {
        verifier = new MACVerifier(currentKey.toOctetSequenceKey().toSecretKey());
      } else if (keyType.equals(KeyType.OKP)) {
        verifier = new Ed25519Verifier(currentKey.toOctetKeyPair());
      } else {
        throw new WorkshopJWKException(String.format("Unsupported keyType %s", keyType));
      }

      verifiers.put(keyId, verifier);
    }

    return verifiers;
  }
}
