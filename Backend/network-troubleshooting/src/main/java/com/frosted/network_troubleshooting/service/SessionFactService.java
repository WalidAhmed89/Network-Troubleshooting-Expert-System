package com.frosted.network_troubleshooting.service;

import com.frosted.network_troubleshooting.entity.DiagnosisSession;
import com.frosted.network_troubleshooting.entity.SessionFact;
import com.frosted.network_troubleshooting.repository.DiagnosisSessionRepository;
import com.frosted.network_troubleshooting.repository.SessionFactRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SessionFactService {
    private final DiagnosisSessionRepository sessionRepository;
    private final SessionFactRepository factRepository;

    @Transactional
    public SessionFact addFact(Integer sessionId, String factName, String factValue) {
        if (factName == null || factName.isBlank()) {
            throw new IllegalArgumentException("Fact name cannot be empty");
        }

        if (factValue == null || factValue.isBlank()) {
            throw new IllegalArgumentException("Fact value cannot be empty");
        }

        DiagnosisSession session = sessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Diagnosis session not found: " + sessionId));

        String normalizedName = factName.trim();

        boolean duplicate = factRepository.findBySessionId(sessionId).stream().anyMatch(fact -> fact.getFactName().trim().equalsIgnoreCase(normalizedName));

        if (duplicate) {
            throw new IllegalArgumentException("This fact already exists in the session" + normalizedName);
        }

        SessionFact fact = new SessionFact();
        fact.setSession(session);
        fact.setFactName(normalizedName);
        fact.setFactValue(factValue.trim());

        return factRepository.save(fact);
    }
}
