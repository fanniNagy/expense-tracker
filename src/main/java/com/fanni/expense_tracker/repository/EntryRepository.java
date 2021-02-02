package com.fanni.expense_tracker.repository;

import com.fanni.expense_tracker.model.Category;
import com.fanni.expense_tracker.model.CategoryCount;
import com.fanni.expense_tracker.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NamedNativeQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    Set<Entry> findEntriesByDateIsBetween(LocalDate from, LocalDate to);

    Set<Entry> findEntriesByPriceBetween(int from, int to);

    @Transactional
    @Modifying
    @Query("UPDATE Entry e SET e.category = ?2 where e.id = ?1")
    void updateCategory(long id, Category category);

    @Query(value = "SELECT new com.fanni.expense_tracker.model.CategoryCount(e.category, SUM(e.price)) " +
            "FROM Entry AS e " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.price) ASC")
    List<CategoryCount> getSpendingByCategories();


}
