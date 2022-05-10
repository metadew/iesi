package io.metadew.iesi.server.rest.configuration.security.jwt;

import lombok.Data;

@Data
public class TokenResponse {
    private final String accessToken;
    private final String refreshToken;
}
