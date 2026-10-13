package com.frosted.network_troubleshooting.repository;

import com.frosted.network_troubleshooting.entity.MatchedRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchedRuleRepository extends JpaRepository<MatchedRule, Integer> {
    void deleteBySessionId(Integer sessionId);

    List<MatchedRule> findBySessionIdOrderByMatchedAtAsc(Integer sessionId);
}
