package com.frosted.network_troubleshooting.service;

import com.frosted.network_troubleshooting.entity.DiagnosisSession;
import com.frosted.network_troubleshooting.entity.User;
import com.frosted.network_troubleshooting.repository.DiagnosisSessionRepository;
import com.frosted.network_troubleshooting.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class DiagnosisSessionService {
    private final DiagnosisSessionRepository sessionRepository;
    private final UserRepository userRepository;


    @Transactional
    public DiagnosisSession createSession(Integer userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        DiagnosisSession session = new DiagnosisSession();
        session.setUser(user);
        session.setCreatedAt(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    public DiagnosisSession getSessionById(Integer sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Diagnosis session not found: " + sessionId));
    }

}
