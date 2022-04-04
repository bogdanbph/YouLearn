package com.youlearn.youlearn.controller;

import com.youlearn.youlearn.model.UserRole;
import com.youlearn.youlearn.model.dto.CertificationDto;
import com.youlearn.youlearn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDetails> getUserByEmail(@RequestParam("email") String email) {
        return new ResponseEntity<>(userService.loadUserByUsername(URLDecoder.decode(email, StandardCharsets.UTF_8)), HttpStatus.OK);
    }

    @PostMapping("/role")
    public ResponseEntity<UserRole> getRoleForUser(@RequestParam("email") String email) {
        return new ResponseEntity<>(userService.getUserRoleForUser(email), HttpStatus.OK);
    }

    @PostMapping("/certifications")
    public ResponseEntity<List<CertificationDto>> getCertificationsForUser(@RequestParam("email") String email) {
        return new ResponseEntity<>(userService.getCertificationsForUser(email), HttpStatus.OK);
    }
}
