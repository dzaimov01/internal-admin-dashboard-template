package com.example.admin.app;

import com.example.admin.domain.FeatureFlag;
import com.example.admin.domain.OrganizationSettings;
import com.example.admin.infra.FeatureFlagRepository;
import com.example.admin.infra.OrganizationSettingsRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsService {
  private final OrganizationSettingsRepository organizationSettingsRepository;
  private final FeatureFlagRepository featureFlagRepository;

  public SettingsService(
      OrganizationSettingsRepository organizationSettingsRepository,
      FeatureFlagRepository featureFlagRepository) {
    this.organizationSettingsRepository = organizationSettingsRepository;
    this.featureFlagRepository = featureFlagRepository;
  }

  public OrganizationSettings getOrganizationSettings() {
    return organizationSettingsRepository.findAll().stream().findFirst().orElseThrow();
  }

  @Transactional
  public OrganizationSettings updateOrganizationSettings(
      String name, String timezone, String supportEmail) {
    OrganizationSettings settings = getOrganizationSettings();
    settings.update(name, timezone, supportEmail);
    return settings;
  }

  public List<FeatureFlag> listFlags() {
    return featureFlagRepository.findAll();
  }

  @Transactional
  public FeatureFlag updateFlag(String key, boolean enabled) {
    FeatureFlag flag = featureFlagRepository.findByKey(key).orElseThrow();
    flag.update(enabled);
    return flag;
  }
}
