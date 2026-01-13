package com.example.admin.infra;

import com.example.admin.domain.OrganizationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationSettingsRepository extends JpaRepository<OrganizationSettings, Long> {}
