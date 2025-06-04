package com.transer.authorizer.application.services;

import com.transer.authorizer.application.ports.inputs.Authorizer;
import com.transer.authorizer.application.ports.outputs.RoleValidator;
import com.transer.authorizer.application.ports.outputs.TokenValidator;
import com.transer.authorizer.domain.models.policies.AuthorizerResponse;
import com.transer.authorizer.domain.models.policies.PolicyDocument;
import com.transer.authorizer.domain.models.policies.Statement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizerService implements Authorizer {

  private final TokenValidator tokenValidator;
  private final RoleValidator roleValidator;

  @Override
  public AuthorizerResponse authorize(String token, String methodArn, String clientId) {

    try {

      final List<String> roles = tokenValidator.validate(extractToken(token), clientId);

      final String result = roleValidator.validate(roles, methodArn)
        ? "Allow"
        : "Deny";

      log.info("Result of role validation: {}", result);
      return generatePolicy(result, methodArn, Map.of("message", "Success"));

    } catch (Exception e) {
      log.error("It was not possible to validate the token signature or token roles", e);
      return generatePolicy("Deny", methodArn, Map.of("message", e.getMessage()));
    }
  }

  private AuthorizerResponse generatePolicy(String effect, String methodArn, Map<String, String> ctx) {
    return AuthorizerResponse.builder()
      .principalId("user")
      .context(ctx)
      .policyDocument(PolicyDocument.builder()
        .statements(Collections.singletonList(Statement.builder()
          .effect(effect)
          .resource(methodArn)
          .build()))
        .build())
      .build();
  }
}
