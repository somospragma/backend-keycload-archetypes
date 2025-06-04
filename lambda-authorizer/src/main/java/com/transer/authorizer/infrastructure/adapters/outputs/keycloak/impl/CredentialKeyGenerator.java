package com.transer.authorizer.infrastructure.adapters.outputs.keycloak.impl;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

@Component
public class CredentialKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, java.lang.reflect.Method method, Object... params) {
        String provider = (String) params[0];
        String token = (String) params[1];

        return provider + ":" + token;
    }
}
