package com.transer.authorizer.infrastructure.adapters.outputs.keycloak.clients;


import com.transer.authorizer.infrastructure.adapters.outputs.keycloak.dtos.AuthorizationResponse;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(
  name = "keycloakClient",
  url = "${keycloak.base-url}",
  configuration = {
    KeycloakFormEncoder.class,
    KeycloakLoggerConfig.class
  })
public interface KeycloakClient {


  @PostMapping(
    value = "/realms/{provider}/protocol/openid-connect/token/introspect",
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
  )
  @Headers("Content-Type: application/x-www-form-urlencoded")
  AuthorizationResponse validateToken(
    @PathVariable("provider") String provider,
    @RequestHeader("Authorization") String authorization,
    @RequestBody Map<String, ?> formParams
  );
}