package com.tejas.hiretrack.controller;

import com.tejas.hiretrack.dto.JobMatchResult;
import com.tejas.hiretrack.service.JobMatchService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ai-match")
public class AiMatchController {

    private final JobMatchService jobMatchService;

    public AiMatchController(JobMatchService jobMatchService) {
        this.jobMatchService = jobMatchService;
    }

    @GetMapping
    public String showAnalyzer(
            Model model,
            Authentication authentication) {

        model.addAttribute("resumeText", "");
        model.addAttribute("jobDescription", "");
        model.addAttribute("userEmail", authentication.getName());

        return "job-match";
    }

    @PostMapping
    public String analyzeMatch(
            @RequestParam String resumeText,
            @RequestParam String jobDescription,
            Model model,
            Authentication authentication) {

        JobMatchResult result = jobMatchService.analyze(
                resumeText,
                jobDescription
        );

        model.addAttribute("result", result);
        model.addAttribute("resumeText", resumeText);
        model.addAttribute("jobDescription", jobDescription);
        model.addAttribute("userEmail", authentication.getName());

        return "job-match";
    }
}