package com.frosted.network_troubleshooting.repository;

import com.frosted.network_troubleshooting.entity.DiagnosisSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisSessionRepository extends JpaRepository<DiagnosisSession, Integer> {
}
