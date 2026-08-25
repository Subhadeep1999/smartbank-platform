package com.smartbank.auth.security;


import com.smartbank.auth.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expiration;
    private final String issuer;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration,
            @Value("${jwt.issuer}") String issuer
    ) {
        this.signingKey = Keys.hmacShaKeyFor(
                io.jsonwebtoken.io.Decoders.BASE64.decode(secret)
        );
        this.expiration = expiration;
        this.issuer = issuer;
    }

    public String generateToken(
            User user,
            String role,
            List<String> permissions
    ) {
        Date now = new Date();

        return Jwts.builder()
                .subject(user.getUserId())
                .issuer(issuer)
                .issuedAt(now)
                .expiration(
                        new Date(now.getTime() + expiration)
                )
                .claim("username", user.getUsername())
                .claim("bankId", user.getBankId())
                .claim("role", role)
                .claim("permissions", permissions)
                .signWith(signingKey)
                .compact();
    }

    public long getExpiration() {
        return expiration;
    }
}
