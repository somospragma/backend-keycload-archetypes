package com.transer.authorizer.infrastructure.adapters.outputs.keycloak.clients;

import feign.Logger;
import feign.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class KeycloakLogger extends Logger {

  @Override
  protected void log(String s, String format, Object... args) {
    log.info("Feign log: {}:{}", s, String.format(format, args));
  }

  @Override
  protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
    log.info("Feign Response [{}] : Status: {}, Body: {}", configKey, response.status(), response);
    return super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);
  }
}
