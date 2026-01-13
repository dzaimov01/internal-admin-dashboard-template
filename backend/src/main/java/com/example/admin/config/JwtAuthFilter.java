package com.example.admin.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      try {
        Claims claims = jwtService.parseClaims(token);
        String subject = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);
        List<String> permissions = claims.get("permissions", List.class);

        List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
        if (roles != null) {
          roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }
        if (permissions != null) {
          permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
        }

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(subject, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception ex) {
        SecurityContextHolder.clearContext();
      }
    }

    filterChain.doFilter(request, response);
  }
}
