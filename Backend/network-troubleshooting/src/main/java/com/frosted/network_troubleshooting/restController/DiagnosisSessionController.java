package com.frosted.network_troubleshooting.restController;

import com.frosted.network_troubleshooting.dto.AddFactRequest;
import com.frosted.network_troubleshooting.dto.CreateSessionRequest;
import com.frosted.network_troubleshooting.entity.DiagnosisSession;
import com.frosted.network_troubleshooting.entity.SessionFact;
import com.frosted.network_troubleshooting.service.DiagnosisSessionService;
import com.frosted.network_troubleshooting.service.InferenceEngineService;
import com.frosted.network_troubleshooting.service.SessionFactService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/diagnosis-sessions")
public class DiagnosisSessionController {

    private final DiagnosisSessionService sessionService;
    private final SessionFactService factService;
    private final InferenceEngineService inferenceEngineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiagnosisSession createSession(@Valid @RequestBody CreateSessionRequest request) {
        return sessionService.createSession(request.userId());
    }

    @GetMapping("/{id}")
    public DiagnosisSession getSession(@PathVariable Integer id) {
        return sessionService.getSessionById(id);
    }

    @PostMapping("/{id}/facts")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionFact addFact(@PathVariable Integer id, @Valid @RequestBody AddFactRequest request) {
        return factService.addFact(id, request.factName(), request.factValue());
    }

    @PostMapping("/{id}/diagnose")
    public Map<String, Object> diagnose(@PathVariable Integer id) {
        return inferenceEngineService.diagnose(id);
    }

    @GetMapping("/{id}/explanation")
    public Map<String, Object> getExplanation(@PathVariable Integer id) {
        return inferenceEngineService.getExplanation(id);
    }
}
