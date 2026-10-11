package com.frosted.network_troubleshooting.repository;

import com.frosted.network_troubleshooting.entity.RuleCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleConditionRepository extends JpaRepository<RuleCondition,Integer> {
    List<RuleCondition> findByRuleId(Integer ruleId);
}
