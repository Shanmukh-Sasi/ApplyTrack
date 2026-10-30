package com.project1.applytrack.service;

import com.project1.applytrack.controller.JobApplicationController;
import com.project1.applytrack.model.ApplicationStatus;
import com.project1.applytrack.model.JobApplication;
import com.project1.applytrack.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {
    @Autowired
    private JobApplicationRepository jobapplicationrepo;
    @Autowired
    private StatusHistoryService statushistoryservice;

    public JobApplication applicationcreated(JobApplication jobapplication) {
        return jobapplicationrepo.save(jobapplication);
    }

    public List<JobApplication> viewall() {
        return jobapplicationrepo.findAll();
    }

    public JobApplication viewbyid(Long id) {
        return jobapplicationrepo.findById(id).orElseThrow(null);
    }

    public void deletedbyid(Long id) {
        jobapplicationrepo.deleteById(id);
    }

    @Transactional
    public void updatestatus(Long id, String status) {

        JobApplication jobapplication = jobapplicationrepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Application not found with ID: " + id
                        )
                );

        ApplicationStatus newStatus;

        try {
            newStatus = ApplicationStatus.valueOf(
                    status.trim().toUpperCase()
            );
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid status: " + status
            );
        }

        ApplicationStatus previousStatus = jobapplication.getStatus();

        statushistoryservice.updatestatus(
                jobapplication, previousStatus, newStatus
        );


        jobapplication.setStatus(newStatus);

        jobapplicationrepo.save(jobapplication);
    }
}
