package com.frosted.network_troubleshooting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/diagnosis")
    public String showDiagnosisPage() {
        return "diagnosis";
    }

    @GetMapping("/results/{sessionId}")
    public String showResultsPage(
            @PathVariable Integer sessionId,
            Model model) {

        model.addAttribute("sessionId", sessionId);
        return "results";
    }
}