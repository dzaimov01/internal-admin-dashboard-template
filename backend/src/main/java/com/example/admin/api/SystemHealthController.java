package com.example.admin.api;

import java.sql.Connection;
import java.time.OffsetDateTime;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemHealthController {
  private final DataSource dataSource;
  private final String buildVersion;

  public SystemHealthController(DataSource dataSource, @Value("${app.build-version:dev}") String buildVersion) {
    this.dataSource = dataSource;
    this.buildVersion = buildVersion;
  }

  @GetMapping("/health")
  @PreAuthorize("hasAuthority('system:read')")
  public HealthStatus health() {
    boolean dbOk;
    try (Connection connection = dataSource.getConnection()) {
      dbOk = connection.isValid(2);
    } catch (Exception ex) {
      dbOk = false;
    }
    return new HealthStatus(dbOk, buildVersion, OffsetDateTime.now().toString());
  }

  public record HealthStatus(boolean databaseOk, String buildVersion, String timestamp) {}
}
