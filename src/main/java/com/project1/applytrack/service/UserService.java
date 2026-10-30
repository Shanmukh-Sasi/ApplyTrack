package com.project1.applytrack.service;

import com.project1.applytrack.model.User;
import com.project1.applytrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}
