package com.tejas.hiretrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tejas.hiretrack.model.AppUser;
import com.tejas.hiretrack.model.JobApplication;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    List<JobApplication>
            findAllByUserOrderByApplicationDateDesc(AppUser user);

    List<JobApplication>
            findByUserAndCompanyNameContainingIgnoreCase(
                    AppUser user,
                    String companyName);

    Optional<JobApplication> findByIdAndUser(
            Long id,
            AppUser user);

    long countByUser(AppUser user);

    long countByUserAndStatusIgnoreCase(
            AppUser user,
            String status);
}