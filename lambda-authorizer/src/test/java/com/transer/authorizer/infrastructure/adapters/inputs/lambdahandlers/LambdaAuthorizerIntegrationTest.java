package com.transer.authorizer.infrastructure.adapters.inputs.lambdahandlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.transer.authorizer.domain.models.policies.AuthorizerResponse;
import com.transer.authorizer.infrastructure.adapters.utils.ResourceUtil;
import com.transer.authorizer.infrastructure.generators.TokenGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest(httpPort = 8091)
@SpringBootTest
@ActiveProfiles("test")
class LambdaAuthorizerIntegrationTest implements ResourceUtil, TokenGenerator {

  @Autowired
  private LambdaAuthorizer lambdaAuthorizer;

  @Test
  void givenNoActiveTokenWhenAuthorizationRequestArrivesShouldReturnDeniedPolicy() throws Exception {

    final JsonNode noActiveToken = getJson("/responses/no-active-token-keycloak-response.json");

    stubFor(post(urlPathEqualTo("/realms/users/protocol/openid-connect/token/introspect"))
      .willReturn(aResponse()
        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .withBody(noActiveToken.toString())));

    APIGatewayCustomAuthorizerEvent apiGatewayCustomAuthorizerEvent =  APIGatewayCustomAuthorizerEvent.builder()
      .withAuthorizationToken(usersToken())
      .withMethodArn("arn:aws:execute-api:us-east-1:339713053416:bz6r7c1u5d/dev/GET/v1/notifier/send")
      .build();

    final AuthorizerResponse authorizerResponse = lambdaAuthorizer.apply(apiGatewayCustomAuthorizerEvent);

    assertThat(authorizerResponse.getPrincipalId()).isEqualTo("user");
    assertThat(authorizerResponse.getPolicyDocument().Statement).hasSize(1);
    assertThat(authorizerResponse.getPolicyDocument().Statement.getFirst().Effect).isEqualTo("Deny");
  }

  @Test
  void givenActiveTokenWhenAuthorizationRequestArrivesWithInvalidMethodArnShouldReturnDeniedPolicy() throws Exception {

    final JsonNode activeToken = getJson("/responses/active-token-keycloak-response.json");

    stubFor(post(urlPathEqualTo("/realms/users/protocol/openid-connect/token/introspect"))
      .willReturn(aResponse()
        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .withBody(activeToken.toString())));

    APIGatewayCustomAuthorizerEvent apiGatewayCustomAuthorizerEvent =  APIGatewayCustomAuthorizerEvent.builder()
      .withAuthorizationToken(usersToken())
      .withMethodArn("methodArn-fake")
      .build();

    final AuthorizerResponse authorizerResponse = lambdaAuthorizer.apply(apiGatewayCustomAuthorizerEvent);

    assertThat(authorizerResponse.getPrincipalId()).isEqualTo("user");
    assertThat(authorizerResponse.getPolicyDocument().Statement).hasSize(1);
    assertThat(authorizerResponse.getPolicyDocument().Statement.getFirst().Effect).isEqualTo("Deny");
  }

  @Test
  void givenActiveTokenWhenAuthorizationRequestArrivesWithValidMethodArnShouldReturnAllowedPolicy() throws Exception {

    final JsonNode responseKeycloak = getJson("/responses/active-token-keycloak-response.json");

    stubFor(post(urlPathEqualTo("/realms/users/protocol/openid-connect/token/introspect"))
      .willReturn(aResponse()
        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .withBody(responseKeycloak.toString())));

    APIGatewayCustomAuthorizerEvent apiGatewayCustomAuthorizerEvent =  APIGatewayCustomAuthorizerEvent.builder()
      .withAuthorizationToken(usersToken())
      .withMethodArn("arn:aws:execute-api:us-east-1:339713053416:bz6r7c1u5d/dev/POST/v1/notifier/push")
      .build();

    final AuthorizerResponse authorizerResponse = lambdaAuthorizer.apply(apiGatewayCustomAuthorizerEvent);

    assertThat(authorizerResponse.getPrincipalId()).isEqualTo("user");
    assertThat(authorizerResponse.getPolicyDocument().Statement).hasSize(1);
    assertThat(authorizerResponse.getPolicyDocument().Statement.getFirst().Effect).isEqualTo("Allow");
  }
}