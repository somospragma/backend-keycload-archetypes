package com.transer.authorizer.infrastructure.adapters.outputs.rolevalidators.impl;

import com.transer.authorizer.application.ports.outputs.RoleValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class RoleClientValidator implements RoleValidator {

  private final RoleCollectionCatalog roleCollectionCatalog;

  @Autowired
  public RoleClientValidator(RoleCollectionCatalog roleCollectionCatalog) {
    this.roleCollectionCatalog = roleCollectionCatalog;
  }

  @Override
  public boolean validate(List<String> roles, String methodArn) {

    final String method = extractMethod(methodArn);

    final List<String> validRoles = findValidRoles(roles);

    if(roleCollectionCatalog.isEnabled()) {
      if (validRoles.isEmpty()) {
        log.info("No valid roles found, returning allow policy");
        return true;
      }

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
