package com.transer.authorizer.application.services;

import com.transer.authorizer.application.ports.outputs.RoleValidator;
import com.transer.authorizer.application.ports.outputs.TokenValidator;
import com.transer.authorizer.domain.models.policies.AuthorizerResponse;
import com.transer.authorizer.infrastructure.generators.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizerServiceTest implements TokenGenerator {

  private AuthorizerService authorizerService;

  @Mock
  private TokenValidator tokenValidator;

  @Mock
  private RoleValidator roleValidator;



  @BeforeEach
  void setUp() {
    this.authorizerService = new AuthorizerService(tokenValidator, roleValidator, "transer-drivers");
  }

  @Test
  void givenInvalidMethodWhenAttemptingToValidateTokenShouldResultDeniedPolicy() {

    final AuthorizerResponse authorize = authorizerService.authorize("Bearer token-dummy-fake", "methodArn", "clientId");

    assertThat(authorize.getPrincipalId()).isEqualTo("user");
    assertThat(authorize.getPolicyDocument().Statement).hasSize(1);
    assertThat(authorize.getPolicyDocument().Statement.getFirst().Effect).isEqualTo("Deny");
  }

  @Test
  void givenValidMethodWhenAttemptingToValidateDriverTokenShouldResultAllowPolicy() {

    final AuthorizerResponse authorize = authorizerService.authorize(driversToken(), "methodArn", "clientId");

    assertThat(authorize.getPrincipalId()).isEqualTo("user");
    assertThat(authorize.getPolicyDocument().Statement).hasSize(1);
    assertThat(authorize.getPolicyDocument().Statement.getFirst().Effect).isEqualTo("Allow");
  }

  @Test
  void shouldReturnDeniedPolicyWhenRoleValidationResultWasFalse() {


    when(tokenValidator.validate(anyString(), anyString())).thenReturn(List.of());
    when(roleValidator.validate(anyList(), anyString())).thenReturn(false);

    final AuthorizerResponse authorize = authorizerService.authorize(usersToken(), "methodArn", "clientId");

    assertThat(authorize.getPrincipalId()).isEqualTo("user");
    assertThat(authorize.getPolicyDocument().Statement).hasSize(1);
    assertThat(authorize.getPolicyDocument().Statement.getFirst().Effect).isEqualTo("Deny");
  }

  @Test
  void shouldReturnAllowedPolicyWhenRoleValidationResultWasTrue() {

    when(tokenValidator.validate(anyString(), anyString())).thenReturn(List.of());
    when(roleValidator.validate(anyList(), anyString())).thenReturn(true);

    final AuthorizerResponse authorize = authorizerService.authorize(usersToken(), "methodArn", "clientId");

    assertThat(authorize.getPrincipalId()).isEqualTo("user");
    assertThat(authorize.getPolicyDocument().Statement).hasSize(1);
    assertThat(authorize.getContext()).containsExactly(Map.entry("message", "Success"));
    assertThat(authorize.getPolicyDocument().Statement.getFirst().Effect).isEqualTo("Allow");
  }
}