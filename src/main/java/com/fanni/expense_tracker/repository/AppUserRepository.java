package com.fanni.expense_tracker.repository;

import com.fanni.expense_tracker.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findAppUserByUserName(String userName);
}
