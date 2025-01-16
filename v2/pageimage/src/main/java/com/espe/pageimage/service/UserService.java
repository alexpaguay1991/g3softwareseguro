package com.espe.pageimage.service;

import com.espe.pageimage.model.User;
import com.espe.pageimage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String username, String password, String role) {
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Encrypt password
        user.setRole(role);

        return userRepository.save(user);
    }

    /*public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }*/

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
