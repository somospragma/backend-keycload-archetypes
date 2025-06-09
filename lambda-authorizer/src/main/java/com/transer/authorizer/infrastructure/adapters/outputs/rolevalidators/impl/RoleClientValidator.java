package com.transer.authorizer.infrastructure.adapters.outputs.rolevalidators.impl;

import com.transer.authorizer.application.ports.outputs.RoleValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleClientValidator implements RoleValidator {

  private final RoleCollectionCatalog roleCollectionCatalog;

  @Override
  public boolean validate(List<String> roles, String methodArn) {

    final String method = extractMethod(methodArn);

    final List<String> validRoles = findValidRoles(roles);

    if(roleCollectionCatalog.isEnabled()) {
      log.info("Role validation is enabled, validating roles: {}", validRoles);
      return findValidActions(validRoles)
        .stream()
        .anyMatch(methodAction -> methodAction.equalsIgnoreCase(method));
    }

    log.info("Role validation is disabled, returning allow policy");
    return true;
  }

  public List<String> findValidRoles(List<String> roles) {
    return roles.stream()
      .filter(role -> roleCollectionCatalog.getComponents().stream()
        .anyMatch(component -> role.startsWith(component + "-")))
      .toList();
  }

  public List<String> findValidActions(List<String> roles) {
    return roleCollectionCatalog.getActions().entrySet()
      .stream()
      .filter(entry -> entry.getValue().stream()
        .anyMatch(action -> roles.stream()
          .anyMatch(role -> role.endsWith("-" + action))))
      .map(Map.Entry::getKey)
      .toList();
  }

  private String extractMethod(String methodArn) {
    Pattern pattern = Pattern.compile(roleCollectionCatalog.getMethodRegex());
    Matcher matcher = pattern.matcher(methodArn);
    final Optional<String> methodOpt = matcher.find()
      ? Optional.of(matcher.group(1))
      : Optional.empty();
    return methodOpt
      .map(String::toLowerCase)
      .orElseThrow(() -> new IllegalArgumentException("Method not found"));
  }
}
