package com.transer.authorizer.application.services;

import com.transer.authorizer.application.ports.inputs.Authorizer;
import com.transer.authorizer.application.ports.outputs.RoleValidator;
import com.transer.authorizer.application.ports.outputs.TokenValidator;
import com.transer.authorizer.domain.models.policies.AuthorizerResponse;
import com.transer.authorizer.domain.models.policies.PolicyDocument;
import com.transer.authorizer.domain.models.policies.Statement;
import com.transer.authorizer.infrastructure.adapters.utils.ClaimExtractor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AuthorizerService implements Authorizer, ClaimExtractor {

  private final TokenValidator tokenValidator;
  private final RoleValidator roleValidator;
  private final String clientIdUser;

  @Autowired
  public AuthorizerService(TokenValidator tokenValidator, RoleValidator roleValidator, @Value("${keycloak.clients.drivers.client-id}") String clientIdUser) {
    this.tokenValidator = tokenValidator;
    this.roleValidator = roleValidator;
    this.clientIdUser = clientIdUser;
  }

  @Override
  public AuthorizerResponse authorize(String token, String methodArn, String clientId) {

    try {

      if(validateClientIdUser(token)) {
        log.info("Result of role validation: {}", "Allow");
        return generatePolicy("Allow", methodArn, Map.of("message", "Success"));
      }

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

  private boolean validateClientIdUser(String token) {
    final JSONObject header = decodeToken(token);
    final String azp = extractClaim(header);
    return clientIdUser.equals(azp);
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
