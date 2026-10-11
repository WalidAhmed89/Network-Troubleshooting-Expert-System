package com.frosted.network_troubleshooting.repository;

import com.frosted.network_troubleshooting.entity.SessionFact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionFactRepository extends JpaRepository<SessionFact, Integer> {
    List<SessionFact> findBySessionId(Integer sessionId);
}
