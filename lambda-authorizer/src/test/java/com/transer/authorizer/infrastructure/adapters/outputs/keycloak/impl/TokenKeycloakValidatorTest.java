package com.transer.authorizer.infrastructure.adapters.outputs.keycloak.impl;

import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.dtos.AuthorizationResponse;
import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.dtos.ResourcesAccess;
import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.dtos.TranserUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class TokenKeycloakValidatorTest {

  private TokenKeycloakValidator tokenKeycloakValidator;

  @MockBean
  private KeycloakInvoker keycloakInvoker;

  @Autowired
  private ClientCredentialCatalog clientCredentialCatalog;

  @BeforeEach
  void setUp() {
    this.tokenKeycloakValidator = new TokenKeycloakValidator(keycloakInvoker, clientCredentialCatalog);
  }

  @Test
  void givenUnregisteredClientWhenKeycloakIsInvokedShouldReturnExceptionClientNoFound() {

    final Throwable throwable = catchThrowable(() -> tokenKeycloakValidator.validate("token-dummy", "client-fake"));

    assertThat(throwable)
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Client not found");
  }

  @Test
  void shouldThrowExceptionWhenKeycloakInvocationGenerateError() {

    when(keycloakInvoker.invoke(anyString(), anyString(), any(ClientCredentialCatalog.ClientCredential.class)))
      .thenThrow(new RuntimeException("Keycloak error"));

    final Throwable throwable = catchThrowable(() -> tokenKeycloakValidator.validate("token-dummy", "users"));

    assertThat(throwable)
      .isInstanceOf(RuntimeException.class)
      .hasMessage("Keycloak error");
  }

  @Test
  void shouldReturnCorrectRoleListWhenKeycloakInvocationGenerateData() {

    when(keycloakInvoker.invoke(anyString(), anyString(), any(ClientCredentialCatalog.ClientCredential.class)))
      .thenReturn(AuthorizationResponse.builder()
        .resourceAccess(ResourcesAccess.builder()
          .transerUser(TranserUser.builder()
            .roles(List.of("component-fake", "users", "component-fake_1", "component-fake_2"))
            .build())
          .build())
        .build());

    final List<String> rolesValidated = tokenKeycloakValidator.validate("token-dummy", "users");

    assertThat(rolesValidated)
      .hasSize(4)
      .containsExactly("component-fake", "users", "component-fake_1", "component-fake_2");
  }
}