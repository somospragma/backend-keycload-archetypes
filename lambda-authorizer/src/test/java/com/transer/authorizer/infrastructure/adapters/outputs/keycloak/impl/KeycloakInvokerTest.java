package com.transer.authorizer.infrastructure.adapters.outputs.keycloak.impl;

import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.clients.KeycloakClient;
import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.dtos.AuthorizationResponse;
import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.dtos.ResourcesAccess;
import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.dtos.TranserUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakInvokerTest {

  private KeycloakInvoker keycloakInvoker;

  @Mock
  private KeycloakClient keycloakClient;


  @BeforeEach
  void setUp() {
    this.keycloakInvoker = new KeycloakInvoker(keycloakClient);
  }

  @Test
  void shouldThrowExceptionWhenKeycloakInvocationGenerateError() {

    ClientCredentialCatalog.ClientCredential clientCredential = new ClientCredentialCatalog.ClientCredential("clientId", "clientSecret");

    when(keycloakClient.validateToken(anyString(), anyString(), anyMap())).thenThrow(new RuntimeException("Keycloak error"));

    final Throwable throwable = catchThrowable(() -> keycloakInvoker.invoke("provider", "token", clientCredential));

    assertThat(throwable)
      .isInstanceOf(RuntimeException.class)
      .hasMessage("Keycloak error");
  }

  @Test
  void shouldReturnCorrectRolesWhenKeycloakInvocationReturnData() {

    final ClientCredentialCatalog.ClientCredential clientCredential = new ClientCredentialCatalog.ClientCredential("clientId", "clientSecret");
    final AuthorizationResponse authorizationResponse = AuthorizationResponse.builder()
      .resourceAccess(ResourcesAccess.builder()
        .transerUser(TranserUser.builder()
          .roles(List.of("clients-view"))
          .build())
        .build())
      .build();

    when(keycloakClient.validateToken(anyString(), anyString(), anyMap())).thenReturn(authorizationResponse);

    final AuthorizationResponse authorizationResponseResult = keycloakInvoker.invoke("provider", "token", clientCredential);

    assertThat(authorizationResponseResult.getResourceAccess().getTranserUser().getRoles())
      .containsExactly("clients-view");
  }
}