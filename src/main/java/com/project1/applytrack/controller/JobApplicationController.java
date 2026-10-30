package com.project1.applytrack.controller;

import com.project1.applytrack.dto.JobApplicationRequest;
import com.project1.applytrack.dto.UpdateStatusRequest;
import com.project1.applytrack.model.JobApplication;
import com.project1.applytrack.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("application")
public class JobApplicationController {
    @Autowired
    private ApplicationService applicationService;
    @GetMapping("/home")
    public String home() {
        return "Barama Shanmukh Raghu Sasidhar";
    }
    @PostMapping("/create")
    public JobApplicationRequest create(@Valid @RequestBody JobApplicationRequest jobApplicationRequest) {
        return applicationService.applicationcreated(jobApplicationRequest);
    }

    @GetMapping("/viewall")
    public List<JobApplication> viewall() {
        return applicationService.viewall();
    }

    @GetMapping("/viewstatus/{id}")
    public JobApplication viewstatus(@PathVariable Long id) {
        return applicationService.viewbyid(id);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        applicationService.deletedbyid(id);
        return "Deleted Application";
    }

    @PatchMapping("/UpdateStatus/{id}")
    public String update(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        applicationService.updatestatus(id, request.getStatus());
        JobApplication application = applicationService.viewbyid(id);
        return application.getId() + " "+ application.getStatus() + " Updated successfully";
    }
    


}
