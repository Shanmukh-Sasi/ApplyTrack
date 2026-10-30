package com.project1.applytrack.controller;

import com.project1.applytrack.model.Interview;
import com.project1.applytrack.model.InterviewOutcome;
import com.project1.applytrack.service.InterviewService;
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
            @RequestBody Interview interview) {

        return interviewService.createInterview(applicationId, interview);
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
            @RequestBody Interview interview) {

        return interviewService.updateInterview(interviewId, interview);
    }


    @PatchMapping("/outcome/{interviewId}")
    public Interview updateOutcome(
            @PathVariable Long interviewId,
            @RequestParam InterviewOutcome outcome) {

        return interviewService.updateOutcome(interviewId, outcome);
    }


    @DeleteMapping("/delete/{interviewId}")
    public String delete(@PathVariable Long interviewId) {

        interviewService.deleteInterview(interviewId);

        return "Interview deleted successfully";
    }
}


