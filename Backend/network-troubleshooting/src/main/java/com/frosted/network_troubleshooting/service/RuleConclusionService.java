package com.frosted.network_troubleshooting.service;

import com.frosted.network_troubleshooting.entity.RuleConclusion;
import com.frosted.network_troubleshooting.repository.RuleConclusionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleConclusionService {
    private final RuleConclusionRepository ruleConclusionRepository;

    public RuleConclusionService(RuleConclusionRepository ruleConclusionRepository) {
        this.ruleConclusionRepository = ruleConclusionRepository;
    }

    public List<RuleConclusion> getAllConclusions() {
        return ruleConclusionRepository.findAll();
    }

    public RuleConclusion getConclusionById(Integer id) {
        return ruleConclusionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Rule Conclusion not found with id: " + id));
    }

    public List<RuleConclusion> getConclusionsByRuleId(Integer Id) {
        return ruleConclusionRepository.findByRuleId(Id);
    }
}
