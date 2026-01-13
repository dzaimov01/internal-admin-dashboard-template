package com.example.admin.app;

import com.example.admin.config.AppUserDetails;
import com.example.admin.config.JwtService;
import com.example.admin.domain.RefreshToken;
import com.example.admin.domain.User;
import com.example.admin.infra.RefreshTokenRepository;
import com.example.admin.infra.UserRepository;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;
  private final long refreshTokenTtlDays;
  private final SecureRandom secureRandom = new SecureRandom();

  public AuthService(
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      RefreshTokenRepository refreshTokenRepository,
      UserRepository userRepository,
      @Value("${app.security.jwt.refresh-token-ttl-days}") long refreshTokenTtlDays) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;
    this.refreshTokenTtlDays = refreshTokenTtlDays;
  }

  @Transactional
  public AuthTokens login(String email, String password) {
    Authentication authentication =
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();
    String accessToken = jwtService.generateAccessToken(
        principal.getUsername(), principal.roleNames(), principal.permissionNames());
    String refreshToken = generateRefreshToken();
    OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(refreshTokenTtlDays);
    refreshTokenRepository.save(new RefreshToken(principal.getUser(), refreshToken, expiresAt));
    return new AuthTokens(accessToken, refreshToken, expiresAt);
  }

  @Transactional
  public AuthTokens refresh(String refreshToken) {
    RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
        .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
    if (token.getExpiresAt().isBefore(OffsetDateTime.now())) {
      refreshTokenRepository.delete(token);
      throw new IllegalArgumentException("Expired refresh token");
    }
    User user = token.getUser();
    AppUserDetails principal = new AppUserDetails(user);
    String accessToken = jwtService.generateAccessToken(
        principal.getUsername(), principal.roleNames(), principal.permissionNames());
    return new AuthTokens(accessToken, refreshToken, token.getExpiresAt());
  }

  @Transactional
  public void logout(String refreshToken) {
    refreshTokenRepository.deleteByToken(refreshToken);
  }

  private String generateRefreshToken() {
    byte[] bytes = new byte[64];
    secureRandom.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  public record AuthTokens(String accessToken, String refreshToken, OffsetDateTime refreshTokenExpiresAt) {}
}
