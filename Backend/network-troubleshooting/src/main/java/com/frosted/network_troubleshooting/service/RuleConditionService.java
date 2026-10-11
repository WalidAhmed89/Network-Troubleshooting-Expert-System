package com.frosted.network_troubleshooting.service;

import com.frosted.network_troubleshooting.entity.RuleCondition;
import com.frosted.network_troubleshooting.repository.RuleConditionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleConditionService {
    private final RuleConditionRepository ruleConditionRepository;

    public RuleConditionService(RuleConditionRepository ruleConditionRepository) {
        this.ruleConditionRepository = ruleConditionRepository;
    }

    public List<RuleCondition> getAllConditions() {
        return ruleConditionRepository.findAll();
    }

    public RuleCondition getConditionById(Integer id) {
        return ruleConditionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Rule condition not found with id " + id));
    }

    public List<RuleCondition> getConditionsByRuleId(Integer Id) {
        return ruleConditionRepository.findByRuleId(Id);
    }
}
