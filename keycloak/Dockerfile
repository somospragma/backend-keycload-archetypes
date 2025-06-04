FROM --platform=linux/arm64 quay.io/keycloak/keycloak:25.0.4 as builder

# Database environment variables
ENV KC_DB=$(DB_TYPE)
ENV KC_DB_URL=$(URL_DB)
ENV KC_DB_SCHEMA=$(DB_SCHEMA)
ENV KC_DB_USERNAME=$(USERNAME_DB)
ENV KC_DB_PASSWORD=$(PASSWORD_DB)


# Metrict & health environment variables
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

# Super admin data environment variables
ENV KEYCLOAK_ADMIN=$(KEYCLOAK_ADMIN)
ENV KEYCLOAK_ADMIN_PASSWORD=$(KEYCLOAK_ADMIN_PASSWORD)

#Enable config without TLS 
ENV KC_HTTP_ENABLED=true
ENV KC_HTTP_PORT=8080

ENV KC_HTTPS_ENABLED=false

ENV KC_HOSTNAME_STRICT=false
ENV KC_HOSTNAME_STRICT_HTTPS=false
ENV KC_PROXY_HEADERS=xforwarded

ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start", "--verbose"]