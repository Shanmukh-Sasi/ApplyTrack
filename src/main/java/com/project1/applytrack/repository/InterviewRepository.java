package com.project1.applytrack.repository;

import com.project1.applytrack.model.Interview;
import com.project1.applytrack.model.InterviewOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview,Long> {
    List<Interview> findAllByJobApplication_User_Userid(Long userId);
    List<Interview> findByJobApplication_IdAndJobApplication_User_Userid(Long applicationId, Long userId);
    List<Interview> findByOutcomeAndScheduledAtGreaterThanEqualAndJobApplication_User_UseridOrderByScheduledAtAsc(
            InterviewOutcome outcome, LocalDateTime scheduledAt, Long userId);
    Optional<Interview> findByIdAndJobApplication_User_Userid(Long interviewId, Long userId);
}
