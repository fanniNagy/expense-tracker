package com.fanni.expense_tracker.service;

import com.fanni.expense_tracker.model.AppUser;
import com.fanni.expense_tracker.repository.AppUserRepository;
import com.fanni.expense_tracker.security.PasswordConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    private final AppUserRepository userRepository;
    private final PasswordConfig passwordConfig;

    @Autowired
    public UserRegistrationService(AppUserRepository userRepository, PasswordConfig passwordConfig) {
        this.userRepository = userRepository;
        this.passwordConfig = passwordConfig;
    }

    public boolean isUserNameAvailable(String userName) {
        return userRepository.findAppUserByUserName(userName).isEmpty();
    }

    public void registerUser(String userName, String password) {
        userRepository.saveAndFlush(
                AppUser.builder()
                        .userName(userName)
                        .password(passwordConfig.passwordEncoder().encode(password))
                        .build()
        );
    }

}
