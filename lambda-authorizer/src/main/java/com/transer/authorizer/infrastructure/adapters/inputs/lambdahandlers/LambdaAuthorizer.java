package com.transer.authorizer.infrastructure.adapters.inputs.lambdahandlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;
import com.transer.authorizer.application.ports.inputs.Authorizer;
import com.transer.authorizer.domain.models.policies.AuthorizerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;


@Component
public class LambdaAuthorizer implements Function<APIGatewayCustomAuthorizerEvent, AuthorizerResponse> {

  private static final Logger logger = LoggerFactory.getLogger(LambdaAuthorizer.class);

  private final Authorizer authorizer;

  @Autowired
  public LambdaAuthorizer(Authorizer authorizer) {
    this.authorizer = authorizer;
  }

  @Override
  public AuthorizerResponse apply(APIGatewayCustomAuthorizerEvent request) {
    logger.info("Authorizing request: {}", request);

    String token = request.getAuthorizationToken();
    final String methodArn = request.getMethodArn();

    // Agregar "Bearer " si no lo tiene
      if (!StringUtils.startsWithIgnoreCase(token, "Bearer ")) {
        token = "Bearer " + token;
    }

    return authorizer.authorize(token, methodArn, "users");
  }
}
