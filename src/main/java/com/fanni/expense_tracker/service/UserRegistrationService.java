package com.fanni.expense_tracker.service;

import com.fanni.expense_tracker.model.AppUser;
import com.fanni.expense_tracker.repository.AppUserRepository;
import com.fanni.expense_tracker.security.PasswordConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserRegistrationService {

    private final AppUserRepository userRepository;
    private final PasswordConfig passwordConfig;
    private final RegistrationFilter registrationFilter;

    @Autowired
    public UserRegistrationService(AppUserRepository userRepository, PasswordConfig passwordConfig, UserExistsFilter registrationFilter) {
        this.userRepository = userRepository;
        this.passwordConfig = passwordConfig;
        this.registrationFilter = registrationFilter;
        registerFilters();
    }

    public void registerFilters() {
        registrationFilter.linkWith(new RoleCheckFilter())
                .linkWith(new RegistrationSuccessFilter(userRepository, passwordConfig));
    }

    public AppUser registerUser(String userName, String password) {
        registrationFilter.check(userName, password);
        return userRepository.findAppUserByUserName(userName)
                .orElseThrow(() -> new NoSuchElementException("Registration unsuccessful"));
    }


}
