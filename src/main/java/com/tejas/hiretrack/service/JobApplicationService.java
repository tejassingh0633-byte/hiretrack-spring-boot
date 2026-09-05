package com.tejas.hiretrack.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tejas.hiretrack.model.AppUser;
import com.tejas.hiretrack.model.JobApplication;
import com.tejas.hiretrack.repository.AppUserRepository;
import com.tejas.hiretrack.repository.JobApplicationRepository;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final AppUserRepository userRepository;

    public JobApplicationService(
            JobApplicationRepository repository,
            AppUserRepository userRepository) {

        this.repository = repository;
        this.userRepository = userRepository;
    }

    private AppUser getUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    public List<JobApplication> getAllApplications(
            String keyword,
            String email) {

        AppUser user = getUser(email);

        if (keyword != null && !keyword.isBlank()) {
            return repository
                    .findByUserAndCompanyNameContainingIgnoreCase(
                            user,
                            keyword.trim());
        }

        return repository
                .findAllByUserOrderByApplicationDateDesc(user);
    }

    public JobApplication getApplicationById(
            Long id,
            String email) {

        AppUser user = getUser(email);

        return repository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Application not found: " + id));
    }

    public JobApplication saveApplication(
            JobApplication application,
            String email) {

        AppUser user = getUser(email);

        if (application.getId() != null) {
            repository
                    .findByIdAndUser(application.getId(), user)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Application not found"));
        }

        application.setUser(user);
        return repository.save(application);
    }

    public void deleteApplication(Long id, String email) {
        JobApplication application =
                getApplicationById(id, email);

        repository.delete(application);
    }

    public long getTotalApplications(String email) {
        return repository.countByUser(getUser(email));
    }

    public long getStatusCount(String status, String email) {
        return repository.countByUserAndStatusIgnoreCase(
                getUser(email),
                status);
    }
}