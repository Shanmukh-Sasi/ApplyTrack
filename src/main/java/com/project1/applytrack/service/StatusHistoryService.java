package com.project1.applytrack.service;

import com.project1.applytrack.model.ApplicationStatus;
import com.project1.applytrack.model.JobApplication;
import com.project1.applytrack.model.StatusHistory;
import com.project1.applytrack.repository.StatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusHistoryService {
    @Autowired
    private StatusHistoryRepository statusHistoryRepository;


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

    public List<StatusHistory> viewall() {
        return statusHistoryRepository.findAll();
    }

//    public StatusHistory viewbyid(Long id) {
//        return statusHistoryRepository.findById(id).orElse(null);
//    }
}
