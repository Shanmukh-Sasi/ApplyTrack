package com.project1.applytrack.service;

import com.project1.applytrack.model.ApplicationStatus;
import com.project1.applytrack.model.JobApplication;
import com.project1.applytrack.model.StatusHistory;
import com.project1.applytrack.errorhandling.ResourceNotFound;
import com.project1.applytrack.repository.StatusHistoryRepository;
import com.project1.applytrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusHistoryService {
    @Autowired
    private StatusHistoryRepository statusHistoryRepository;

    @Autowired
    private UserRepository userRepository;


    public void updatestatus(
            JobApplication jobapplication,
            ApplicationStatus previousStatus,
            ApplicationStatus newStatus) {

        StatusHistory history = new StatusHistory();

        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setJobApplication(jobapplication);

        statusHistoryRepository.save(history);
    }

    public List<StatusHistory> viewall(String email) {
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("Authenticated user not found"))
                .getUserid();
        return statusHistoryRepository.findByJobApplication_User_Userid(userId);
    }

//    public StatusHistory viewbyid(Long id) {
//        return statusHistoryRepository.findById(id).orElse(null);
//    }
}
