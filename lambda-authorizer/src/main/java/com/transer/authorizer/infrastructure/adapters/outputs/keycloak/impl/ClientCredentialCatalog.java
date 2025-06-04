package com.transer.authorizer.infrastructure.adapters.outputs.keycloak.impl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class ClientCredentialCatalog {

  private Map<String, ClientCredential> clients;

  public static record ClientCredential(String clientId, String clientSecret) { }
}
