package com.fanni.expense_tracker.controller;

import com.fanni.expense_tracker.model.AppUser;
import com.fanni.expense_tracker.service.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRegistrationService registrationService;

    @Autowired
    public UserController(UserRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public AppUser registerNewUser(@RequestBody AppUser user){
        return registrationService.registerUser(user.getUserName(), user.getPassword());
    }

}
