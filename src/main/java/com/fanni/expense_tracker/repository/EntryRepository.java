package com.fanni.expense_tracker.repository;

import com.fanni.expense_tracker.model.Category;
import com.fanni.expense_tracker.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    Set<Entry> findEntriesByDateIsBetween(LocalDate from, LocalDate to);

    Set<Entry> findEntriesByPriceBetween(int from, int to);

    @Transactional
    @Modifying
    @Query("UPDATE Entry e SET e.category = ?2 where e.id = ?1")
    void updateCategory(long id, Category category);
}
