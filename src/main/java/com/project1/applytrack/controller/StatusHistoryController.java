package com.project1.applytrack.controller;

import com.project1.applytrack.model.StatusHistory;
import com.project1.applytrack.repository.StatusHistoryRepository;
import com.project1.applytrack.service.StatusHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/history")
public class StatusHistoryController {
    @Autowired
    private StatusHistoryService statusHistoryService;
    @GetMapping("/statuslist")
    public List<StatusHistory> findAll() {
        return statusHistoryService.viewall();
    }

//    @GetMapping("/statusbyid/{id}")
//    public List<StatusHistory> findOne(@PathVariable Long application_id) {
//        return statusHistoryService.viewbyid(application_id);
//    }
}
