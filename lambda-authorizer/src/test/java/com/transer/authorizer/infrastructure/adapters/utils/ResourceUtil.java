package com.transer.authorizer.infrastructure.adapters.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.InputStream;

public interface ResourceUtil {

  default JsonNode getJson(String path) throws Exception {
    InputStream input = this.getClass().getResourceAsStream(path);
    return getMapper().readTree(input);
  }

  default ObjectMapper getMapper() {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule());
  }
}