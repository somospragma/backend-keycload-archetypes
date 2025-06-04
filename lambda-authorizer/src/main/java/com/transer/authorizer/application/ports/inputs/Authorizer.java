package com.transer.authorizer.application.ports.inputs;

import com.transer.authorizer.domain.models.policies.AuthorizerResponse;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public interface Authorizer {

  AuthorizerResponse authorize(String token, String methodArn, String clientId);

  default String extractToken(String bearerToken) {
    return Optional.ofNullable(bearerToken)
      .filter(token -> StringUtils.startsWithIgnoreCase(token, "Bearer"))
      .map(token -> StringUtils.split(token, " "))
      .map(Arrays::stream)
      .map(stringStream -> stringStream.reduce((first, second) -> second)
        .orElseThrow(() -> new IllegalArgumentException("Invalid token")))
      .orElseThrow(() -> new IllegalArgumentException("Bearer token missing"));
  }
}
