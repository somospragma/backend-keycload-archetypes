package com.transer.authorizer.infrastructure.adapters.outputs.keycloak.impl;

import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.clients.KeycloakClient;
import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.dtos.AuthorizationResponse;
import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.impl.ClientCredentialCatalog.ClientCredential;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakInvoker {

  private final KeycloakClient keycloakClient;

  @Cacheable(
    value = "tokenValidated",
    keyGenerator = "credentialKeyGenerator"
  )
  public AuthorizationResponse invoke(String provider, String token, ClientCredential credential) {

    return keycloakClient.validateToken(
      provider,
      getBasicAuth(credential),
      Map.of("token", token)
    );
  }

  private String getBasicAuth(ClientCredential clientCredential) {
    final String basicAuth = clientCredential.clientId() + ":" + clientCredential.clientSecret();
    final String basicAuthBase64 = Base64.getEncoder().encodeToString(basicAuth.getBytes(StandardCharsets.UTF_8));
    return "Basic ".concat(basicAuthBase64);
  }
}