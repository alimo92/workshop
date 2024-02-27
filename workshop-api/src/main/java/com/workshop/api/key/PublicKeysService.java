/* (C) 2024 */
package com.workshop.api.key;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.workshop.api.exception.WorkshopJWKException;
import jakarta.annotation.PostConstruct;
import java.text.ParseException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
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

  // Set the required JWT claims for access tokens
  private static final DefaultJWTClaimsVerifier<SecurityContext> CLAIMS_VERIFIER =
      new DefaultJWTClaimsVerifier<>(
          new JWTClaimsSet.Builder().issuer("issuer_test").build(),
          new HashSet<>(
              Arrays.asList("testMap", JWTClaimNames.ISSUED_AT, JWTClaimNames.EXPIRATION_TIME)));

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

  public boolean verifyToken(SignedJWT token) {
    try {
      String keyId = token.getHeader().getKeyID();
      JWSVerifier verifier = verifiersCache.get().get(keyId);
      if (verifier == null) {
        log.error("Unknown key with id = {}", keyId);
        return false;
      }
      return token.verify(verifier);
    } catch (JOSEException e) {
      log.warn("Invalid token. Failed verification");
      return false;
    }
  }

  public boolean verifyClaims(SignedJWT token) {
    try {
      CLAIMS_VERIFIER.verify(token.getJWTClaimsSet(), null);
      return true;
    } catch (BadJWTException | ParseException e) {
      log.error("Failed claims verification", e);
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

  private Optional<JWSVerifier> createVerifier(JWK publicKey) throws JOSEException {
    KeyType keyType = publicKey.getKeyType();

    if (keyType.equals(KeyType.RSA)) {
      return Optional.of(new RSASSAVerifier(publicKey.toRSAKey().toRSAPublicKey()));
    }

    if (keyType.equals(KeyType.EC)) {
      return Optional.of(new ECDSAVerifier(publicKey.toECKey().toECPublicKey()));
    }

    if (keyType.equals(KeyType.OCT)) {
      return Optional.of(new MACVerifier(publicKey.toOctetSequenceKey().toSecretKey()));
    }

    if (keyType.equals(KeyType.OKP)) {
      return Optional.of(new Ed25519Verifier(publicKey.toOctetKeyPair()));
    }

    return Optional.empty();
  }

  private Map<String, JWSVerifier> createVerifiers(JWKSet publicKeys) throws Exception {
    Map<String, JWSVerifier> verifiers = new LinkedHashMap<>();
    for (JWK currentKey : publicKeys.getKeys()) {
      String keyId = currentKey.getKeyID();
      Optional<JWSVerifier> verifier = createVerifier(currentKey);

      if (verifier.isPresent()) {
        verifiers.put(keyId, verifier.get());
      } else {
        throw new WorkshopJWKException(
            String.format("Unsupported keyType %s", currentKey.getKeyType()));
      }
    }

    return verifiers;
  }

  public JWSSigner getSigner(JWK key) {
    try {
      KeyType keyType = key.getKeyType();
      if (keyType.equals(KeyType.RSA)) {
        log.info("Creating {} signer", KeyType.RSA);
        return new RSASSASigner(key.toRSAKey().toPrivateKey());
      }

      if (keyType.equals(KeyType.EC)) {
        log.info("Creating {} signer", KeyType.EC);
        return new ECDSASigner(key.toECKey());
      }

      if (keyType.equals(KeyType.OCT)) {
        log.info("Creating {} signer", KeyType.OCT);
        return new MACSigner(key.toOctetSequenceKey().toSecretKey());
      }

      if (keyType.equals(KeyType.OKP)) {
        log.info("Creating {} signer", KeyType.OKP);
        return new Ed25519Signer(key.toOctetKeyPair());
      }

      throw new WorkshopJWKException(String.format("Unsupported keyType %s", keyType));

    } catch (JOSEException | WorkshopJWKException e) {
      log.error("Failed creating jwk signer", e);
      return null;
    }
  }
}
