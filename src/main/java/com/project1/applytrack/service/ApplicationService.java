package com.project1.applytrack.service;

import com.project1.applytrack.controller.JobApplicationController;
import com.project1.applytrack.dto.JobApplicationRequest;
import com.project1.applytrack.errorhandling.ResourceNotFound;
import com.project1.applytrack.model.ApplicationStatus;
import com.project1.applytrack.model.JobApplication;
import com.project1.applytrack.model.User;
import com.project1.applytrack.repository.JobApplicationRepository;
import com.project1.applytrack.repository.UserRepository;
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
    @Autowired
    private UserRepository userrepo;

    public JobApplicationRequest applicationcreated(JobApplicationRequest Request) {
        User user=userrepo.findById( Request.getUserid()).orElseThrow(()->
                new ResourceNotFound("User Id Not Found: "+ Request.getUserid()));
        JobApplication jobApplication=new JobApplication();
        jobApplication.setUser(user);
        jobApplication.setJobname(Request.getJobname());
        jobApplication.setCompanyname(Request.getCompanyname());
        jobApplication.setStatus(Request.getStatus());
        jobApplication.setJobdescription(Request.getJobdescription());
        jobApplication.setAppliedAt(Request.getAppliedAt());
        jobapplicationrepo.save(jobApplication);
        return Request;
    }

    public List<JobApplication> viewall() {
        return jobapplicationrepo.findAll();
    }

    public JobApplication viewbyid(Long id) {
        return jobapplicationrepo.findById(id).orElseThrow(()-> new ResourceNotFound("Job Application Not Found with id: "+id));
    }

    public void deletedbyid(Long id) {
        jobapplicationrepo.deleteById(id);
    }

    @Transactional
    public void updatestatus(Long id, String status) {
        JobApplication jobapplication = jobapplicationrepo.findById(id).orElseThrow(() -> new ResourceNotFound("Application not found with ID: " + id));
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
}
