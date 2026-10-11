package com.frosted.network_troubleshooting.service;

import com.frosted.network_troubleshooting.entity.Rule;
import com.frosted.network_troubleshooting.repository.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleService {
    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public List<Rule> getEnabledRules(){
        return ruleRepository.findByEnabledTrueOrderByPriorityAsc();
    }
}
