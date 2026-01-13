package com.example.admin.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final Key key;
  private final String issuer;
  private final long accessTokenTtlMinutes;

  public JwtService(
      @Value("${app.security.jwt.secret}") String secret,
      @Value("${app.security.jwt.issuer}") String issuer,
      @Value("${app.security.jwt.access-token-ttl-minutes}") long accessTokenTtlMinutes) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.issuer = issuer;
    this.accessTokenTtlMinutes = accessTokenTtlMinutes;
  }

  public String generateAccessToken(String subject, List<String> roles, List<String> permissions) {
    Instant now = Instant.now();
    return Jwts.builder()
        .setSubject(subject)
        .setIssuer(issuer)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plus(accessTokenTtlMinutes, ChronoUnit.MINUTES)))
        .claim("roles", roles)
        .claim("permissions", permissions)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims parseClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }
}
