package com.transer.authorizer.infrastructure.adapters.outputs.keycloak.clients;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakLoggerConfig {

  @Bean
  public Logger logger() {
    return new KeycloakLogger();
  }

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }
}
