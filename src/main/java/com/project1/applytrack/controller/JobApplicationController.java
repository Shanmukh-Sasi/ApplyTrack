package com.project1.applytrack.controller;

import com.project1.applytrack.dto.JobApplicationRequest;
import com.project1.applytrack.dto.UpdateStatusRequest;
import com.project1.applytrack.model.JobApplication;
import com.project1.applytrack.model.ApplicationStatus;
import com.project1.applytrack.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


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
    public JobApplicationRequest create(@Valid @RequestBody JobApplicationRequest jobApplicationRequest,
                                        @AuthenticationPrincipal UserDetails currentUser) {
        return applicationService.applicationcreated(jobApplicationRequest, currentUser.getUsername());
    }

    @GetMapping("/viewall")
    public Page<JobApplication> viewall(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails currentUser) {
        return applicationService.viewall(pageable, currentUser.getUsername());
    }

    @GetMapping("/search")
    public Page<JobApplication> search(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String jobTitle,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails currentUser) {
        return applicationService.search(company, jobTitle, pageable, currentUser.getUsername());
    }

    @GetMapping("/status/{status}")
    public Page<JobApplication> findByStatus(
            @PathVariable ApplicationStatus status,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetails currentUser) {
        return applicationService.findByStatus(status, pageable, currentUser.getUsername());
    }

    @GetMapping("/viewstatus/{id}")
    public JobApplication viewstatus(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails currentUser) {
        return applicationService.viewbyid(id, currentUser.getUsername());
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails currentUser) {
        applicationService.deletedbyid(id, currentUser.getUsername());
        return "Deleted Application";
    }

    @PatchMapping("/UpdateStatus/{id}")
    public String update(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request,
                         @AuthenticationPrincipal UserDetails currentUser) {
        applicationService.updatestatus(id, request.getStatus(), currentUser.getUsername());
        JobApplication application = applicationService.viewbyid(id, currentUser.getUsername());
        return application.getId() + " "+ application.getStatus() + " Updated successfully";
    }
    


}
