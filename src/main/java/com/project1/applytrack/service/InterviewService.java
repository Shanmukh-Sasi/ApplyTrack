package com.project1.applytrack.service;

import com.project1.applytrack.dto.InterviewRequest;
import com.project1.applytrack.dto.InterviewOutcomeRequest;
import com.project1.applytrack.errorhandling.ResourceNotFound;
import com.project1.applytrack.model.Interview;
import com.project1.applytrack.model.InterviewOutcome;
import com.project1.applytrack.model.JobApplication;
import com.project1.applytrack.repository.InterviewRepository;
import com.project1.applytrack.repository.JobApplicationRepository;
import com.project1.applytrack.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class InterviewService {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    public Interview createInterview(Long applicationId, InterviewRequest request, String email) {

        Long userId = findUserId(email);
        JobApplication application = jobApplicationRepository.findByIdAndUser_Userid(applicationId, userId)
                .orElseThrow(() -> new ResourceNotFound("Job application not found with ID: " + applicationId));

        Interview interview = new Interview();
        interview.setInterviewRound(request.getInterviewRound());
        interview.setScheduledAt(request.getScheduledAt());
        if (request.getOutcome() != null) {
            interview.setOutcome(request.getOutcome());
        }
        interview.setFeedback(request.getFeedback());
        interview.setMeetingLink(request.getMeetingLink());
        interview.setJobApplication(application);

        if (interview.getOutcome() == null) {
            interview.setOutcome(InterviewOutcome.SCHEDULED);
        }

        return interviewRepository.save(interview);
    }

    public List<Interview> viewAll(String email) {
        return interviewRepository.findAllByJobApplication_User_Userid(findUserId(email));
    }

    public List<Interview> findUpcoming(String email) {
        return interviewRepository.findByOutcomeAndScheduledAtGreaterThanEqualAndJobApplication_User_UseridOrderByScheduledAtAsc(
                InterviewOutcome.SCHEDULED,
                LocalDateTime.now(),
                findUserId(email));
    }

    public List<Interview> viewByApplicationId(Long applicationId, String email) {
        Long userId = findUserId(email);

        return interviewRepository.findByJobApplication_IdAndJobApplication_User_Userid(applicationId, userId);
    }

    public Interview viewById(Long interviewId, String email) {

        return findOwnedInterview(interviewId, email);
    }

    public Interview updateInterview(Long interviewId, InterviewRequest request, String email) {

        Interview interview = findOwnedInterview(interviewId, email);


        interview.setInterviewRound(request.getInterviewRound());
        interview.setScheduledAt(request.getScheduledAt());
        interview.setMeetingLink(request.getMeetingLink());
        if (request.getOutcome() != null) {
            interview.setOutcome(request.getOutcome());
        }
        interview.setFeedback(request.getFeedback());

        return interviewRepository.save(interview);

    }


    public Interview updateOutcome(Long interviewId, InterviewOutcomeRequest request, String email) {

        Interview interview = findOwnedInterview(interviewId, email);

        interview.setOutcome(request.getOutcome());

        return interviewRepository.save(interview);
    }


    @Transactional
    public void deleteInterview(Long interviewId, String email) {

        Interview interview = findOwnedInterview(interviewId, email);

        interviewRepository.delete(interview);
    }

    private Interview findOwnedInterview(Long interviewId, String email) {
        return interviewRepository.findByIdAndJobApplication_User_Userid(interviewId, findUserId(email))
                .orElseThrow(() -> new ResourceNotFound("Interview not found with ID: " + interviewId));
    }

    private Long findUserId(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("Authenticated user not found"))
                .getUserid();
    }
}
