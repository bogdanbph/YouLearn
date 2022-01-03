package com.youlearn.youlearn.controller;

import com.youlearn.youlearn.dto.RegistrationRequest;
import com.youlearn.youlearn.model.User;
import com.youlearn.youlearn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /*@GetMapping
    public ResponseEntity<User> getUserByEmail(@RequestBody RegistrationRequest request) {
        return new ResponseEntity(userService.loadUserByUsername(request.getEmail()), HttpStatus.OK);
    }*/
}
