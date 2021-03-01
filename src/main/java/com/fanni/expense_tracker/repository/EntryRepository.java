package com.fanni.expense_tracker.repository;

import com.fanni.expense_tracker.model.Category;
import com.fanni.expense_tracker.model.CategoryCount;
import com.fanni.expense_tracker.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    Set<Entry> findAllEntryByUserId(Long userId);

    Set<Entry> findEntriesByDateIsBetween(LocalDate from, LocalDate to);

    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM Entry AS e " +
                    "WHERE (e.price BETWEEN :from AND :to) AND (e.user_id = :userId)" +
                    "GROUP BY e.user_id, e.id")
    Set<Entry> findEntriesOfUserByPriceBetween(@Param("userId")Long userId, @Param("from")int from, @Param("to")int to);

    @Transactional
    @Modifying
    @Query("UPDATE Entry e SET e.category = ?2 where e.id = ?1")
    void updateCategory(long id, Category category);

    @Query(value = "SELECT new com.fanni.expense_tracker.model.CategoryCount(e.category, SUM(e.price)) " +
            "FROM Entry AS e " +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.price) ASC")
    List<CategoryCount> getEntriesByCategories();

    @Query(value = "SELECT new com.fanni.expense_tracker.model.CategoryCount(e.category, SUM(e.price)) " +
            "FROM Entry AS e " +
            "WHERE e.price < 0" +
            "GROUP BY e.category " +
            "ORDER BY SUM(e.price) ASC")
    List<CategoryCount> getSpendingByCategories();
}
