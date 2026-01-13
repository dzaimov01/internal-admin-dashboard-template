package com.example.admin.api;

import com.example.admin.app.MetricsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
  private final MetricsService metricsService;

  public MetricsController(MetricsService metricsService) {
    this.metricsService = metricsService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('metrics:read')")
  public MetricsService.MetricsSnapshot getMetrics() {
    return metricsService.snapshot();
  }
}
