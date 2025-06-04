package com.transer.authorizer.infrastructure.adapters.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

public interface ClaimExtractor {

  default JSONObject decodeToken(String token) {

    String[] tokenParts = token.split("\\.");
    if (tokenParts.length != 3) {
      throw new IllegalArgumentException("El token no es valido, no está bien formado");
    }
    String payloadBase64 = tokenParts[1];
    byte[] payloadBytes;
    try {
      payloadBytes = Base64.getUrlDecoder().decode(payloadBase64);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("El payload del token no está correctamente codificado en Base64", e);
    }
    String payload = new String(payloadBytes);
    return new JSONObject(payload);
  }

  default String extractClaim(JSONObject headerToken) {
    try {
      return headerToken.getString("azp");
    } catch (JSONException e) {
      throw new IllegalArgumentException("El payload del token no contiene los campos esperados", e);
    }
  }

}
