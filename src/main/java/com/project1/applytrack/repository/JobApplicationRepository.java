package com.project1.applytrack.repository;

import com.project1.applytrack.model.JobApplication;
import com.project1.applytrack.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Page<JobApplication> findAllByUser_Userid(Long userId, Pageable pageable);
    Page<JobApplication> findByUser_UseridAndStatus(Long userId, ApplicationStatus status, Pageable pageable);
    Page<JobApplication> findByUser_UseridAndCompanynameContainingIgnoreCase(
            Long userId, String company, Pageable pageable);
    Page<JobApplication> findByUser_UseridAndJobnameContainingIgnoreCase(
            Long userId, String jobTitle, Pageable pageable);
    Page<JobApplication> findByUser_UseridAndCompanynameContainingIgnoreCaseAndJobnameContainingIgnoreCase(
            Long userId, String company, String jobTitle, Pageable pageable);
    java.util.Optional<JobApplication> findByIdAndUser_Userid(Long id, Long userId);
}
