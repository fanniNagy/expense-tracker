package com.fanni.expense_tracker.service;

import com.fanni.expense_tracker.model.AppUser;
import com.fanni.expense_tracker.repository.AppUserRepository;
import com.fanni.expense_tracker.security.PasswordConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashSet;

public abstract class RegistrationFilter {

    private RegistrationFilter next;

    /**
     * Builds chains of middleware objects.
     */
    public RegistrationFilter linkWith(RegistrationFilter next) {
        this.next = next;
        return next;
    }

    /**
     * Subclasses will implement this method with concrete checks.
     */
    public abstract boolean check(String userName, String password);

    /**
     * Runs check on the next object in chain or ends traversing if we're in
     * last object in chain.
     */
    protected boolean checkNext(String userName, String password) {
        if (next == null) {
            return true;
        }
        return next.check(userName, password);
    }
}

@Slf4j
@Component
class UserExistsFilter extends RegistrationFilter {
    private final AppUserRepository userRepository;

    @Autowired
    public UserExistsFilter(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean check(String userName, String password) {
        log.info("Checking from UserExistsFilter");
        if (userRepository.findAppUserByUserName(userName).isPresent()) {
            log.info("This username is already registered!");
            return false;
        }
        return checkNext(userName, password);
    }
}

@Slf4j
@Component
class RoleCheckFilter extends RegistrationFilter {
    public boolean check(String userName, String password) {
        log.info("Checking from RoleCheckMiddleware");
        if (userName.equals("admin")) {
            log.info("Hello, admin!");
            return true;
        }
        System.out.println("Hello, user!");
        return checkNext(userName, password);
    }
}

@Slf4j
@Component
class RegistrationSuccessFilter extends RegistrationFilter {

    private final AppUserRepository userRepository;
    private final PasswordConfig passwordConfig;

    @Autowired
    RegistrationSuccessFilter(AppUserRepository userRepository, PasswordConfig passwordConfig) {
        this.userRepository = userRepository;
        this.passwordConfig = passwordConfig;
    }

    public boolean check(String userName, String password) {
        log.info("Checking from RegistrationSuccessFilter");
        userRepository.saveAndFlush(
                AppUser.builder()
                        .userName(userName)
                        .password(passwordConfig.passwordEncoder().encode(password))
                        .authorities(new HashSet<>(){
                            {add(new SimpleGrantedAuthority("USER"));}
                        })
                        .build()
        );
        return checkNext(userName, password);
    }
}


