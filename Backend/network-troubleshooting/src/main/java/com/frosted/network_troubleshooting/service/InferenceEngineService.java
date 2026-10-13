package com.frosted.network_troubleshooting.service;

import com.frosted.network_troubleshooting.entity.*;
import com.frosted.network_troubleshooting.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@AllArgsConstructor
@Service
public class InferenceEngineService {
    private final DiagnosisSessionRepository sessionRepository;
    private final SessionFactRepository factRepository;
    private final RuleRepository ruleRepository;
    private final RuleConditionRepository conditionRepository;
    private final RuleConclusionRepository conclusionRepository;
    private final MatchedRuleRepository matchedRuleRepository;

    @Transactional
    public Map<String, Object> diagnose(Integer sessionId) {
        //Find the diagnosis session
        DiagnosisSession session = sessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Diagnosis session not found: " + sessionId));

        //Load the user facts
        List<SessionFact> facts = factRepository.findBySessionId(sessionId);
        if (facts.isEmpty()) {
            throw new IllegalArgumentException("Cannot diagnose without session facts.");
        }

        //Convert facts into a map
        Map<String, String> factMap = new LinkedHashMap<>();

        for (SessionFact fact : facts) {
            String key = normalize(fact.getFactName());
            if (factMap.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate fact name in session: " + fact.getFactName());
            }
            factMap.put(key, fact.getFactValue());
        }

        //Load enabled rules ordered by priority
        List<Rule> rules = ruleRepository.findByEnabledTrueOrderByPriorityAsc();

        List<Rule> matchedRules = new ArrayList<>();
        Map<String, String> conclusions = new LinkedHashMap<>();

        for (Rule rule : rules) {
            List<RuleCondition> conditions = conditionRepository.findByRuleId(rule.getId());

            // a rule without conditions must not match automatically
            if (conditions.isEmpty()) {
                continue;
            }

            boolean allConditionsMatch = true;

            for (RuleCondition condition : conditions) {
                String actualValue = factMap.get(normalize(condition.getFactName()));
                if (!matches(actualValue, condition.getOperator(), condition.getExpectedValue())) {
                    allConditionsMatch = false;
                    break;
                }
            }
            // Skipping rules whose conditions did not all match
            if (!allConditionsMatch) {
                continue;
            }
            matchedRules.add(rule);

            // Collect this rule's conclusions
            List<RuleConclusion> ruleConclusions = conclusionRepository.findByRuleId(rule.getId());
            for (RuleConclusion conclusion : ruleConclusions) {
                String name = normalize(conclusion.getConclusionName());

                conclusions.putIfAbsent(name, conclusion.getConclusionValue());
            }
        }
        // Replace old matched-rule records for this session
        matchedRuleRepository.deleteBySessionId(sessionId);

        for (Rule rule : matchedRules) {
            MatchedRule matchedRule = new MatchedRule();
            matchedRule.setSession(session);
            matchedRule.setRule(rule);
            matchedRule.setMatchedAt(LocalDateTime.now());

            matchedRuleRepository.save(matchedRule);
        }

        //Extract the final diagnosis
        String diagnosis = conclusions.get("diagnosis");

        if (diagnosis == null || diagnosis.isBlank()) {
            diagnosis = "No matching diagnosis found";
        }

        session.setFinalDiagnosis(diagnosis);
        sessionRepository.save(session);

        // Return the result
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("sessionId", sessionId);
        result.put("diagnosis", diagnosis);
        result.put("matchedRuleIds", matchedRules.stream().map(Rule::getId).toList());
        result.put("matchedRuleNames", matchedRules.stream().map(Rule::getName).toList());
        result.put("conclusions", conclusions);
        result.put("facts", factMap);

        if (matchedRules.isEmpty()) {
            result.put("explanation", "No enabled rule mathced the submitted facts.");
        } else {
            result.put("explanation", "The diagnosis was derived from the matching rules " + "and their stored conclusions");
        }

        return result;
    }

    private boolean matches(String actualValue, String operator, String expectedValue) {
        // A missing fact cannot satisfy a condition
        if (actualValue == null || operator == null || expectedValue == null) {
            return false;
        }
        String normalizedOperator = operator.trim().toUpperCase(Locale.ROOT);

        return switch (normalizedOperator) {
            case "EQUALS", "=" -> actualValue.trim().equalsIgnoreCase(expectedValue.trim());
            case "NOT_EQUALS", "!=" -> !actualValue.trim().equalsIgnoreCase(expectedValue.trim());
            default -> false;
        };
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    @Transactional
    public Map<String, Object> getExplanation(Integer sessionId) {

        DiagnosisSession session = sessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Diagnosis session not found: " + sessionId));
        List<SessionFact> facts = factRepository.findBySessionId(sessionId);
        List<MatchedRule> matchedRules = matchedRuleRepository.findBySessionIdOrderByMatchedAtAsc(sessionId);
        Map<String, String> factMap = new LinkedHashMap<>();

        for (SessionFact fact : facts) {
            factMap.put(normalize(fact.getFactName()), fact.getFactValue());
        }

        List<Map<String, Object>> ruleDetails = new ArrayList<>();

        for (MatchedRule matchedRule : matchedRules) {
            Rule rule = matchedRule.getRule();
            Integer ruleId = rule.getId();

            List<Map<String, Object>> conditionDetails = new ArrayList<>();

            for (RuleCondition condition : conditionRepository.findByRuleId(ruleId)) {
                Map<String, Object> conditionInfo = new LinkedHashMap<>();

                String actualValue = factMap.get(normalize(condition.getFactName()));

                conditionInfo.put("factName", condition.getFactName());
                conditionInfo.put("operator", condition.getOperator());
                conditionInfo.put("expectedValue", condition.getExpectedValue());
                conditionInfo.put("actualValue", actualValue);
                conditionInfo.put("matched", matches(actualValue, condition.getOperator(), condition.getExpectedValue()));

                conditionDetails.add(conditionInfo);
            }
            List<Map<String, Object>> conclusionDetails = new ArrayList<>();

            for (RuleConclusion conclusion : conclusionRepository.findByRuleId(ruleId)) {
                Map<String, Object> conclusionInfo = new LinkedHashMap<>();

                conclusionInfo.put("name", conclusion.getConclusionName());
                conclusionInfo.put("value", conclusion.getConclusionValue());
                conclusionDetails.add(conclusionInfo);

            }
            Map<String, Object> ruleInfo = new LinkedHashMap<>();
            ruleInfo.put("ruleId", ruleId);
            ruleInfo.put("ruleName", rule.getName());
            ruleInfo.put("description", rule.getDescription());
            ruleInfo.put("priority", rule.getPriority());
            ruleInfo.put("conditions", conditionDetails);
            ruleInfo.put("conclusions", conclusionDetails);

            ruleDetails.add(ruleInfo);
        }
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("sessionId", sessionId);
        response.put("diagnosis", session.getFinalDiagnosis());
        response.put("facts", factMap);
        response.put("matchedRules", ruleDetails);

        if (matchedRules.isEmpty()) {
            response.put("explanation", "No rules were recorded as matched for this session.");
        } else {
            response.put("explanation", "The listed rules were recorded as matched during diagnosis.");
        }
        return response;
    }
}
