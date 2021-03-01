package com.fanni.expense_tracker.service;

import com.fanni.expense_tracker.model.AppUser;
import com.fanni.expense_tracker.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
public class AppUserService {

    private final AppUserRepository userRepository;

    public AppUserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) throw new NoSuchElementException("No authentication found");
        String username = authentication.getName();
        return userRepository.findAppUserByUserName(username).orElseThrow(() -> {
            throw new NoSuchElementException(String.format("%s username not found!", username));
        });
    }
}
