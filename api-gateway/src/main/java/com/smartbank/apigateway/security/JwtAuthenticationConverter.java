package com.smartbank.apigateway.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        List<String> authorities = new ArrayList<>();

        // Role
        String role = jwt.getClaimAsString("role");

        if (role != null && !role.isBlank()) {
            authorities.add("ROLE_" + role);
        }

        // Permissions
        List<String> permissions =
                jwt.getClaimAsStringList("permissions");

        if (permissions != null) {
            authorities.addAll(permissions);
        }

        Collection<GrantedAuthority> grantedAuthorities =
                authorities.stream()
                        .map(authority ->
                                (GrantedAuthority) new SimpleGrantedAuthority(authority)
                        )
                        .toList();

        return new JwtAuthenticationToken(
                jwt,
                grantedAuthorities,
                jwt.getClaimAsString("username")
        );
    }
}