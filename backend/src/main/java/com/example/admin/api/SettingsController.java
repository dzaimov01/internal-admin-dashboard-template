package com.example.admin.api;

import com.example.admin.app.SettingsService;
import com.example.admin.domain.FeatureFlag;
import com.example.admin.domain.OrganizationSettings;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {
  private final SettingsService settingsService;

  public SettingsController(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @GetMapping("/organization")
  @PreAuthorize("hasAuthority('settings:read')")
  public OrganizationSettings getOrganization() {
    return settingsService.getOrganizationSettings();
  }

  @PatchMapping("/organization")
  @PreAuthorize("hasAuthority('settings:write')")
  public OrganizationSettings updateOrganization(@Valid @RequestBody OrganizationRequest request) {
    return settingsService.updateOrganizationSettings(
        request.organizationName(), request.timezone(), request.supportEmail());
  }

  @GetMapping("/flags")
  @PreAuthorize("hasAuthority('settings:read')")
  public List<FeatureFlag> listFlags() {
    return settingsService.listFlags();
  }

  @PatchMapping("/flags")
  @PreAuthorize("hasAuthority('settings:write')")
  public FeatureFlag updateFlag(@Valid @RequestBody FeatureFlagRequest request) {
    return settingsService.updateFlag(request.key(), request.enabled());
  }

  public record OrganizationRequest(
      @NotBlank String organizationName,
      @NotBlank String timezone,
      @Email @NotBlank String supportEmail) {}

  public record FeatureFlagRequest(@NotBlank String key, boolean enabled) {}
}
