package com.transer.authorizer.infrastructure.adapters.outputs.rolevalidators.impl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "roles")
public class RoleCollectionCatalog {

  private boolean enabled;
  private String methodRegex;
  private Map<String, List<String>> actions;
  private List<String> components;
}
