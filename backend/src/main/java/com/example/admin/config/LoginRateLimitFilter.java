package com.example.admin.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {
  private static final int MAX_ATTEMPTS = 5;
  private static final long WINDOW_SECONDS = 60;

  private final Map<String, AttemptWindow> attempts = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if ("/api/auth/login".equals(request.getRequestURI()) && "POST".equals(request.getMethod())) {
      String key = request.getRemoteAddr();
      AttemptWindow window = attempts.computeIfAbsent(key, k -> new AttemptWindow());
      synchronized (window) {
        long now = Instant.now().getEpochSecond();
        if (now - window.windowStart > WINDOW_SECONDS) {
          window.windowStart = now;
          window.count = 0;
        }
        window.count++;
        if (window.count > MAX_ATTEMPTS) {
          response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
          return;
        }
      }
    }

    filterChain.doFilter(request, response);
  }

  private static class AttemptWindow {
    long windowStart = Instant.now().getEpochSecond();
    int count = 0;
  }
}
