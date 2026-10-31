package com.project1.applytrack.controller;

import com.project1.applytrack.model.StatusHistory;
import com.project1.applytrack.service.StatusHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public List<StatusHistory> findAll(@AuthenticationPrincipal UserDetails currentUser) {
        return statusHistoryService.viewall(currentUser.getUsername());
    }

//    @GetMapping("/statusbyid/{id}")
//    public List<StatusHistory> findOne(@PathVariable Long application_id) {
//        return statusHistoryService.viewbyid(application_id);
//    }
}
