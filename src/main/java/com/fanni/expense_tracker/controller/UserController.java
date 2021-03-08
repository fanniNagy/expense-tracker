package com.fanni.expense_tracker.controller;

import com.fanni.expense_tracker.model.AppUser;
import com.fanni.expense_tracker.service.AppUserService;
import com.fanni.expense_tracker.service.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRegistrationService registrationService;
    private final AppUserService userService;

    @Autowired
    public UserController(UserRegistrationService registrationService, AppUserService userService) {
        this.userService = userService;
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public AppUser registerNewUser(@RequestBody AppUser user){
        return registrationService.registerUser(user.getUsername(), user.getPassword());
    }

    @GetMapping("/{username}")
    public AppUser getUserByUserName(@PathVariable String username){
        return userService.getUserByUserName(username);
    }

}
