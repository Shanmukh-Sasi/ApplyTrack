package com.project1.applytrack.dto;

import com.project1.applytrack.model.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class JobApplicationRequest {
    @NotBlank(message = "CompanyName is Required")
    @Size(max =  100, message = "Company name cannot exceed 100 characters")
    private String companyname;

    @NotBlank(message = "Job Name is Required")
    @Size(max = 100, message = "Job name must in size of 100 characters")
    private String jobname;

    private String jobdescription;

    @NotNull(message = "Application status is required")
    private ApplicationStatus status;

    @NotNull(message = "Application date is required")
    private LocalDate appliedAt;

    public String getCompanyname() {
        return companyname;
    }
    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }
    public String getJobname() {
        return jobname;
    }
    public void setJobname(String jobname) {
        this.jobname = jobname;
    }
    public String getJobdescription() {
        return jobdescription;
    }
    public void setJobdescription(String jobdescription) {
        this.jobdescription = jobdescription;
    }
    public ApplicationStatus getStatus() {
        return status;
    }
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
    public LocalDate getAppliedAt() {
        return appliedAt;
    }
    public void setAppliedAt(LocalDate appliedAt) {
        this.appliedAt = appliedAt;
    }
}
