package com.fanni.expense_tracker.repository;

import com.fanni.expense_tracker.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Set;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    Set<Entry> findEntriesByDateIsBetween(LocalDate from, LocalDate to);

    Set<Entry> findEntriesByPriceBetween(int from, int to);

}
