package com.transer.authorizer.infrastructure.adapters.outputs.rolevalidators.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
@ActiveProfiles("integration")
class RoleClientValidatorTest {

  private RoleClientValidator roleClientValidator;

  @Autowired
  private RoleCollectionCatalog roleCollectionCatalog;

  @BeforeEach
  void setUp() {
    this.roleClientValidator = new RoleClientValidator(roleCollectionCatalog);
  }

  @Test
  void givenClientViewRoleWhenRoleClientValidatorWithInvalidMethodArnIsInvokedShouldGenerateMethodNoFound() {
    final List<String> roleClientView = List.of("client-view");

    final Throwable throwable = catchThrowable(() -> roleClientValidator.validate(roleClientView, "methodArn"));

    assertThat(throwable)
      .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Method not found");
  }

  @Test
  void givenFakeComponentRoleWhenRoleClientValidatorWithValidMethodArnIsInvokedShouldReturnTrue() {
    final List<String> componentFakeRole = List.of("component-fake-view");
    final String validMethodArn = "arn:aws:execute-api:us-east-1:339713053416:bz6r7c1u5d/dev/POST/v1/notifier/push";

    assertThat(roleClientValidator.validate(componentFakeRole, validMethodArn))
      .isTrue();
  }

  @Test
  void givenCorrectComponentRoleWhenRoleClientValidatorWithIncorrectMethodArnIsInvokedShouldReturnFalse() {
    final List<String> roles = List.of("clients-view");
    final String validMethodArn = "arn:aws:execute-api:us-east-1:339713053416:bz6r7c1u5d/dev/POST/v2/notifier/push";

    assertThat(roleClientValidator.validate(roles, validMethodArn))
      .isFalse();
  }

  @Test
  void givenCorrectComponentRoleViewWhenRoleClientValidatorWithCorrectMethodArnIsInvokedShouldReturnTrue() {
    final List<String> roles = List.of("drivers-view");
    final String methodArn = "arn:aws:execute-api:us-east-1:339713053416:bz6r7c1u5d/dev/GET/v2/notifier";

    assertThat(roleClientValidator.validate(roles, methodArn))
      .isTrue();
  }

  @Test
  void givenCorrectComponentRoleAssociationWhenRoleClientValidatorWithCorrectMethodArnIsInvokedShouldReturnTrue() {
    final List<String> roles = List.of("clients-association");
    final String methodArn = "arn:aws:execute-api:us-east-1:339713053416:bz6r7c1u5d/dev/POST/v2/clients/create";

    assertThat(roleClientValidator.validate(roles, methodArn))
      .isTrue();
  }
}