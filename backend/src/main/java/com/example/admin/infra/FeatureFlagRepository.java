package com.example.admin.infra;

import com.example.admin.domain.FeatureFlag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {
  Optional<FeatureFlag> findByKey(String key);
}
