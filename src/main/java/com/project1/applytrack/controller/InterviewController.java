package com.project1.applytrack.controller;

import com.project1.applytrack.model.Interview;
import com.project1.applytrack.dto.InterviewRequest;
import com.project1.applytrack.dto.InterviewOutcomeRequest;
import com.project1.applytrack.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interview")
public class InterviewController {
    @Autowired
    private InterviewService interviewService;


    @PostMapping("/create/{applicationId}")
    public Interview create(
            @PathVariable Long applicationId,
            @Valid @RequestBody InterviewRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        return interviewService.createInterview(applicationId, request, currentUser.getUsername());
    }


    @GetMapping("/viewall")
    public List<Interview> viewAll(@AuthenticationPrincipal UserDetails currentUser) {
        return interviewService.viewAll(currentUser.getUsername());
    }

    @GetMapping("/upcoming")
    public List<Interview> upcoming(@AuthenticationPrincipal UserDetails currentUser) {
        return interviewService.findUpcoming(currentUser.getUsername());
    }


    @GetMapping("/application/{applicationId}")
    public List<Interview> viewByApplicationId(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserDetails currentUser) {

        return interviewService.viewByApplicationId(applicationId, currentUser.getUsername());
    }


    @GetMapping("/{interviewId}")
    public Interview viewById(@PathVariable Long interviewId,
                              @AuthenticationPrincipal UserDetails currentUser) {
        return interviewService.viewById(interviewId, currentUser.getUsername());
    }

    @PutMapping("/update/{interviewId}")
    public Interview update(
            @PathVariable Long interviewId,
            @Valid @RequestBody InterviewRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        return interviewService.updateInterview(interviewId, request, currentUser.getUsername());
    }


    @PatchMapping("/outcome/{interviewId}")
    public Interview updateOutcome(
            @PathVariable Long interviewId,
            @Valid @RequestBody InterviewOutcomeRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        return interviewService.updateOutcome(interviewId, request, currentUser.getUsername());
    }


    @DeleteMapping("/delete/{interviewId}")
    public String delete(@PathVariable Long interviewId,
                         @AuthenticationPrincipal UserDetails currentUser) {

        interviewService.deleteInterview(interviewId, currentUser.getUsername());

        return "Interview deleted successfully";
    }
}


