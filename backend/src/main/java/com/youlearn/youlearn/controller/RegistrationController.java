package com.youlearn.youlearn.controller;

import com.youlearn.youlearn.dto.RegistrationRequest;
import com.youlearn.youlearn.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(path = "/register")
@AllArgsConstructor
@CrossOrigin
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public String registerUser(@RequestBody RegistrationRequest request) {
        return registrationService.registerUser(request);
    }

    @GetMapping(path = "/confirm")
    public ModelAndView confirm(@RequestParam("token") String token) {
        registrationService.confirmToken(token);
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("confirmed", true);
        return new ModelAndView("redirect:" + "http://localhost:3000/confirmed", modelMap);
    }
}
