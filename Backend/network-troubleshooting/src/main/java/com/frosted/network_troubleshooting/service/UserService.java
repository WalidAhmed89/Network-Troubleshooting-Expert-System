package com.frosted.network_troubleshooting.service;

import com.frosted.network_troubleshooting.entity.User;
import com.frosted.network_troubleshooting.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userService) {
        this.userRepository = userService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    @Transactional
    public User createUser(String name, String email){
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("User name is required");
        }

        if(email == null || email.isBlank()){
            throw new IllegalArgumentException("User email is required");
        }

        if(userRepository.existsByEmail(email.trim())){
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim());

        return userRepository.save(user);
    }
}
