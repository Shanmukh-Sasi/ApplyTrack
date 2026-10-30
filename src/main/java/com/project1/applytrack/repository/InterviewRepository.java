package com.project1.applytrack.repository;

import com.project1.applytrack.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview,Long> {
    List<Interview> findByJobApplication_Id(Long applicationId);
}
