package com.project1.applytrack.controller;

import com.project1.applytrack.model.Interview;
import com.project1.applytrack.dto.InterviewRequest;
import com.project1.applytrack.dto.InterviewOutcomeRequest;
import com.project1.applytrack.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
            @Valid @RequestBody InterviewRequest request) {

        return interviewService.createInterview(applicationId, request);
    }


    @GetMapping("/viewall")
    public List<Interview> viewAll() {
        return interviewService.viewAll();
    }


    @GetMapping("/application/{applicationId}")
    public List<Interview> viewByApplicationId(
            @PathVariable Long applicationId) {

        return interviewService.viewByApplicationId(applicationId);
    }


    @GetMapping("/{interviewId}")
    public Interview viewById(@PathVariable Long interviewId) {
        return interviewService.viewById(interviewId);
    }

    @PutMapping("/update/{interviewId}")
    public Interview update(
            @PathVariable Long interviewId,
            @Valid @RequestBody InterviewRequest request) {

        return interviewService.updateInterview(interviewId, request);
    }


    @PatchMapping("/outcome/{interviewId}")
    public Interview updateOutcome(
            @PathVariable Long interviewId,
            @Valid @RequestBody InterviewOutcomeRequest request) {

        return interviewService.updateOutcome(interviewId, request);
    }


    @DeleteMapping("/delete/{interviewId}")
    public String delete(@PathVariable Long interviewId) {

        interviewService.deleteInterview(interviewId);

        return "Interview deleted successfully";
    }
}


