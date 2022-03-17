package com.youlearn.youlearn.controller;

import com.youlearn.youlearn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDetails> getUserByEmail(@RequestBody String email) {
        return new ResponseEntity<>(userService.loadUserByUsername(email), HttpStatus.OK);
    }
}
