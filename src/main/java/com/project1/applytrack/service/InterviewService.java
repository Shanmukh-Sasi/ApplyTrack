package com.project1.applytrack.service;

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
    public Interview createInterview(Long applicationId, Interview interview) {

        JobApplication application = jobApplicationRepository
                .findById(applicationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Job application not found with ID: " + applicationId
                        )
                );

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
            throw new RuntimeException(
                    "Job application not found with ID: " + applicationId
            );
        }

        return interviewRepository.findByJobApplication_Id(applicationId);
    }

    public Interview viewById(Long interviewId) {

        return interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Interview not found with ID: " + interviewId
                        )
                );
    }

    public Interview updateInterview(Long interviewId, Interview updatedInterview) {

        Interview existingInterview = interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Interview not found with ID: " + interviewId
                        )
                );

        existingInterview.setInterviewRound(
                updatedInterview.getInterviewRound()
        );

        existingInterview.setScheduledAt(
                updatedInterview.getScheduledAt()
        );

        existingInterview.setMeetingLink(
                updatedInterview.getMeetingLink()
        );

        existingInterview.setOutcome(
                updatedInterview.getOutcome()
        );

        existingInterview.setFeedback(
                updatedInterview.getFeedback()
        );

        
        return interviewRepository.save(existingInterview);
    }


    public Interview updateOutcome(Long interviewId, InterviewOutcome outcome) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Interview not found with ID: " + interviewId
                        )
                );

        interview.setOutcome(outcome);

        return interviewRepository.save(interview);
    }


    @Transactional
    public void deleteInterview(Long interviewId) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Interview not found with ID: " + interviewId
                        )
                );

        interviewRepository.delete(interview);
    }
}