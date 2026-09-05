package com.tejas.hiretrack.controller;

import java.time.LocalDate;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tejas.hiretrack.model.JobApplication;
import com.tejas.hiretrack.service.JobApplicationService;

import jakarta.validation.Valid;

@Controller
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(
            JobApplicationService service) {
        this.service = service;
    }

    @GetMapping({"/", "/applications"})
    public String showApplications(
            @RequestParam(required = false) String keyword,
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        model.addAttribute(
                "applications",
                service.getAllApplications(keyword, email));

        model.addAttribute("keyword", keyword);
        model.addAttribute("userEmail", email);

        model.addAttribute(
                "totalCount",
                service.getTotalApplications(email));

        model.addAttribute(
                "appliedCount",
                service.getStatusCount("Applied", email));

        model.addAttribute(
                "interviewCount",
                service.getStatusCount("Interview", email));

        model.addAttribute(
                "selectedCount",
                service.getStatusCount("Selected", email));

        model.addAttribute(
                "rejectedCount",
                service.getStatusCount("Rejected", email));

        return "index";
    }

    @GetMapping("/applications/new")
    public String showNewApplicationForm(Model model) {

        JobApplication application = new JobApplication();
        application.setApplicationDate(LocalDate.now());
        application.setStatus("Applied");

        model.addAttribute("application", application);
        model.addAttribute("pageTitle", "Add Application");

        return "application-form";
    }

    @PostMapping("/applications/save")
    public String saveApplication(
            @Valid @ModelAttribute("application")
            JobApplication application,
            BindingResult result,
            Authentication authentication,
            Model model) {

        if (result.hasErrors()) {
            String pageTitle = application.getId() == null
                    ? "Add Application"
                    : "Edit Application";

            model.addAttribute("pageTitle", pageTitle);
            return "application-form";
        }

        service.saveApplication(
                application,
                authentication.getName());

        return "redirect:/?saved";
    }

    @GetMapping("/applications/edit/{id}")
    public String showEditApplicationForm(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "application",
                service.getApplicationById(
                        id,
                        authentication.getName()));

        model.addAttribute(
                "pageTitle",
                "Edit Application");

        return "application-form";
    }

    @PostMapping("/applications/delete/{id}")
    public String deleteApplication(
            @PathVariable Long id,
            Authentication authentication) {

        service.deleteApplication(
                id,
                authentication.getName());

        return "redirect:/?deleted";
    }
}