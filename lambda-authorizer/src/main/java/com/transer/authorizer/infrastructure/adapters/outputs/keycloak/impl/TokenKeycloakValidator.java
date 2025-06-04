package com.transer.authorizer.infrastructure.adapters.outputs.keycloak.impl;

import com.transer.authorizer.application.ports.outputs.TokenValidator;
import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.dtos.AuthorizationResponse;
import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.dtos.ResourcesAccess;
import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.exceptions.UnauthorizedResponseException;
import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.impl.ClientCredentialCatalog.ClientCredential;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class TokenKeycloakValidator implements TokenValidator {

  private final KeycloakInvoker keycloakInvoker;
  private final ClientCredentialCatalog clientCredentialCatalog;

  @Autowired
  public TokenKeycloakValidator(KeycloakInvoker keycloakInvoker, ClientCredentialCatalog clientCredentialCatalog) {
    this.keycloakInvoker = keycloakInvoker;
    this.clientCredentialCatalog = clientCredentialCatalog;
  }

  @Override
  public List<String> validate(String token, String client) {
    final ClientCredential clientCredential = getClientCredential(client);
    final AuthorizationResponse authorizationResponse = keycloakInvoker.invoke(client, token, clientCredential);

    return extractRoles(authorizationResponse.getResourceAccess());
  }

  private ClientCredential getClientCredential(String client) {
    return clientCredentialCatalog.getClients().entrySet()
      .stream()
      .filter(entry -> entry.getKey().equalsIgnoreCase(client))
      .map(Map.Entry::getValue)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Client not found"));
  }


  private List<String> extractRoles(ResourcesAccess resourceAccess) {
    return Optional.ofNullable(resourceAccess)
      .map(resources -> resources.getTranserUser().getRoles())
      .orElseThrow(() -> new UnauthorizedResponseException("Invalid or expired token"));
  }
}
