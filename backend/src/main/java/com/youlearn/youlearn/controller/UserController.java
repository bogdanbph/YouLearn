package com.youlearn.youlearn.controller;

import com.youlearn.youlearn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDetails> getUserByEmail(@RequestParam("email") String email) {
        return new ResponseEntity<>(userService.loadUserByUsername(URLDecoder.decode(email, StandardCharsets.UTF_8)), HttpStatus.OK);
    }
}
