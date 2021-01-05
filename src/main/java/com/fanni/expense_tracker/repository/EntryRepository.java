package com.fanni.expense_tracker.repository;

import com.fanni.expense_tracker.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryRepository extends JpaRepository<Entry, Long> {
}
