package com.example.admin.api;

import com.example.admin.app.AuthService;
import com.example.admin.app.SecurityContextService;
import com.example.admin.domain.User;
import com.example.admin.infra.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  private final UserRepository userRepository;
  private final SecurityContextService securityContextService;

  public AuthController(
      AuthService authService,
      UserRepository userRepository,
      SecurityContextService securityContextService) {
    this.authService = authService;
    this.userRepository = userRepository;
    this.securityContextService = securityContextService;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    AuthService.AuthTokens tokens = authService.login(request.email(), request.password());
    return ResponseEntity.ok(new AuthResponse(tokens.accessToken(), tokens.refreshToken(), tokens.refreshTokenExpiresAt().toString()));
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
    AuthService.AuthTokens tokens = authService.refresh(request.refreshToken());
    return ResponseEntity.ok(new AuthResponse(tokens.accessToken(), tokens.refreshToken(), tokens.refreshTokenExpiresAt().toString()));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
    authService.logout(request.refreshToken());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UserProfile> me() {
    String email = securityContextService.currentUserEmail().orElseThrow();
    User user = userRepository.findByEmail(email).orElseThrow();
    List<String> roles = user.getRoles().stream().map(r -> r.getName()).toList();
    return ResponseEntity.ok(new UserProfile(user.getEmail(), user.getFullName(), roles));
  }

  public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}

  public record RefreshRequest(@NotBlank String refreshToken) {}

  public record AuthResponse(String accessToken, String refreshToken, String refreshTokenExpiresAt) {}

  public record UserProfile(String email, String fullName, List<String> roles) {}
}
