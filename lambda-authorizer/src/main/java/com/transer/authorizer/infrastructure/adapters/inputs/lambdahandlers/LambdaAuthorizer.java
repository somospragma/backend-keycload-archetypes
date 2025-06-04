package com.transer.authorizer.infrastructure.adapters.inputs.lambdahandlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;
import com.transer.authorizer.application.ports.inputs.Authorizer;
import com.transer.authorizer.domain.models.policies.AuthorizerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class LambdaAuthorizer implements Function<APIGatewayCustomAuthorizerEvent, AuthorizerResponse> {

  private final Authorizer authorizer;

  @Override
  public AuthorizerResponse apply(APIGatewayCustomAuthorizerEvent request) {
    log.info("Authorizing request: {}", request);

    final String token = request.getAuthorizationToken();
    final String methodArn = request.getMethodArn();

    return authorizer.authorize(token, methodArn, "users");
  }
}
