package com.frosted.network_troubleshooting.restController;

import com.frosted.network_troubleshooting.dto.CreateUserRequest;
import com.frosted.network_troubleshooting.entity.User;
import com.frosted.network_troubleshooting.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request.name(), request.email());
    }
}
