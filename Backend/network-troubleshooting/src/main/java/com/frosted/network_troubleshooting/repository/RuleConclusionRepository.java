package com.frosted.network_troubleshooting.repository;

import com.frosted.network_troubleshooting.entity.RuleConclusion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleConclusionRepository extends JpaRepository<RuleConclusion, Integer> {
    List<RuleConclusion> findByRuleId(Integer ruleId);
}
