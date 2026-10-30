package com.project1.applytrack.controller;

import com.project1.applytrack.dto.UserRequest;
import com.project1.applytrack.model.User;
import com.project1.applytrack.service.ApplicationService;
import com.project1.applytrack.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/createUser")
    public String createUser(@Valid @RequestBody UserRequest request) {

        userService.createUser(request);
        return "User Created Successfully";
    }

}
