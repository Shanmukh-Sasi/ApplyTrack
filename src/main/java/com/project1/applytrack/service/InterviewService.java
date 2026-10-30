package com.project1.applytrack.service;

import com.project1.applytrack.dto.InterviewRequest;
import com.project1.applytrack.dto.InterviewOutcomeRequest;
import com.project1.applytrack.errorhandling.ResourceNotFound;
import com.project1.applytrack.model.Interview;
import com.project1.applytrack.model.InterviewOutcome;
import com.project1.applytrack.model.JobApplication;
import com.project1.applytrack.repository.InterviewRepository;
import com.project1.applytrack.repository.JobApplicationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InterviewService {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    public Interview createInterview(Long applicationId, InterviewRequest request) {

        JobApplication application = jobApplicationRepository.findById(applicationId).orElseThrow(() -> new ResourceNotFound("Job application not found with ID: " + applicationId));

        Interview interview = new Interview();
        interview.setInterviewRound(request.getInterviewRound());
        interview.setScheduledAt(request.getScheduledAt());
        interview.setOutcome(request.getOutcome());
        interview.setFeedback(request.getFeedback());
        interview.setMeetingLink(request.getMeetingLink());
        interview.setJobApplication(application);

        if (interview.getOutcome() == null) {
            interview.setOutcome(InterviewOutcome.SCHEDULED);
        }

        return interviewRepository.save(interview);
    }

    public List<Interview> viewAll() {
        return interviewRepository.findAll();
    }

    public List<Interview> viewByApplicationId(Long applicationId) {

        if (!jobApplicationRepository.existsById(applicationId)) {
            throw new ResourceNotFound("Job application not found with ID: " + applicationId);
        }

        return interviewRepository.findByJobApplication_Id(applicationId);
    }

    public Interview viewById(Long interviewId) {

        return interviewRepository.findById(interviewId).orElseThrow(() -> new ResourceNotFound("Interview not found with ID: " + interviewId));
    }

    public Interview updateInterview(Long interviewId, InterviewRequest request) {

        Interview interview = interviewRepository.findById(interviewId).orElseThrow(() -> new ResourceNotFound("Interview not found with ID: " + interviewId));


        interview.setInterviewRound(request.getInterviewRound());
        interview.setScheduledAt(request.getScheduledAt());
        interview.setMeetingLink(request.getMeetingLink());
        interview.setOutcome(request.getOutcome());
        interview.setFeedback(request.getFeedback());

        return interviewRepository.save(interview);

    }


    public Interview updateOutcome(Long interviewId, InterviewOutcomeRequest request) {

        Interview interview = interviewRepository.findById(interviewId).orElseThrow(() -> new ResourceNotFound("Interview not found with ID: " + interviewId));

        interview.setOutcome(request.getOutcome());

        return interviewRepository.save(interview);
    }


    @Transactional
    public void deleteInterview(Long interviewId) {

        Interview interview = interviewRepository.findById(interviewId).orElseThrow(() -> new ResourceNotFound("Interview not found with ID: " + interviewId));

        interviewRepository.delete(interview);
    }
}
