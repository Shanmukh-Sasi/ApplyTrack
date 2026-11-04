package com.project1.applytrack.service;

import com.project1.applytrack.dto.JobApplicationRequest;
import com.project1.applytrack.errorhandling.ResourceNotFound;
import com.project1.applytrack.model.ApplicationStatus;
import com.project1.applytrack.model.JobApplication;
import com.project1.applytrack.model.User;
import com.project1.applytrack.repository.JobApplicationRepository;
import com.project1.applytrack.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ApplicationService {
    @Autowired
    private JobApplicationRepository jobapplicationrepo;
    @Autowired
    private StatusHistoryService statushistoryservice;
    @Autowired
    private UserRepository userrepo;

    public JobApplicationRequest applicationcreated(JobApplicationRequest request, String email) {
        User user = findUserByEmail(email);
        JobApplication jobApplication=new JobApplication();
        jobApplication.setUser(user);
        jobApplication.setJobname(request.getJobname());
        jobApplication.setCompanyname(request.getCompanyname());
        jobApplication.setStatus(request.getStatus());
        jobApplication.setJobdescription(request.getJobdescription());
        jobApplication.setAppliedAt(request.getAppliedAt());
        jobapplicationrepo.save(jobApplication);
        return request;
    }

    @Transactional
    public JobApplication updateApplication(Long id, JobApplicationRequest request, String email) {
        JobApplication application = viewbyid(id, email);
        ApplicationStatus previousStatus = application.getStatus();
        if (request.getStatus() != previousStatus) {
            statushistoryservice.updatestatus(application, previousStatus, request.getStatus());
        }
        application.setCompanyname(request.getCompanyname());
        application.setJobname(request.getJobname());
        application.setJobdescription(request.getJobdescription());
        application.setStatus(request.getStatus());
        application.setAppliedAt(request.getAppliedAt());
        return jobapplicationrepo.save(application);
    }

    public Page<JobApplication> viewall(Pageable pageable, String email) {
        return jobapplicationrepo.findAllByUser_Userid(findUserByEmail(email).getUserid(), pageable);
    }

    public Page<JobApplication> search(String company, String jobTitle, Pageable pageable, String email) {
        Long userId = findUserByEmail(email).getUserid();
        String companyFilter = normalizeFilter(company);
        String jobTitleFilter = normalizeFilter(jobTitle);

        if (companyFilter == null && jobTitleFilter == null) {
            return jobapplicationrepo.findAllByUser_Userid(userId, pageable);
        }
        if (companyFilter == null) {
            return jobapplicationrepo.findByUser_UseridAndJobnameContainingIgnoreCase(
                    userId, jobTitleFilter, pageable);
        }
        if (jobTitleFilter == null) {
            return jobapplicationrepo.findByUser_UseridAndCompanynameContainingIgnoreCase(
                    userId, companyFilter, pageable);
        }
        return jobapplicationrepo.findByUser_UseridAndCompanynameContainingIgnoreCaseAndJobnameContainingIgnoreCase(
                userId, companyFilter, jobTitleFilter, pageable);
    }

    public Page<JobApplication> findByStatus(ApplicationStatus status, Pageable pageable, String email) {
        return jobapplicationrepo.findByUser_UseridAndStatus(
                findUserByEmail(email).getUserid(), status, pageable);
    }

    private String normalizeFilter(String filter) {
        return filter == null || filter.isBlank() ? null : filter.trim();
    }

    public JobApplication viewbyid(Long id, String email) {
        Long userId = findUserByEmail(email).getUserid();
        return jobapplicationrepo.findByIdAndUser_Userid(id, userId)
                .orElseThrow(() -> new ResourceNotFound("Job Application Not Found with id: " + id));
    }

    public void deletedbyid(Long id, String email) {
        jobapplicationrepo.delete(viewbyid(id, email));
    }

    @Transactional
    public void updatestatus(Long id, String status, String email) {
        JobApplication jobapplication = viewbyid(id, email);
        ApplicationStatus newStatus;
        try {
            newStatus = ApplicationStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        ApplicationStatus previousStatus = jobapplication.getStatus();
        statushistoryservice.updatestatus(jobapplication, previousStatus, newStatus);
        jobapplication.setStatus(newStatus);
        jobapplicationrepo.save(jobapplication);
    }

    private User findUserByEmail(String email) {
        return userrepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("Authenticated user not found"));
    }
}
