package com.fanni.expense_tracker.service;

import com.fanni.expense_tracker.model.AppUser;
import com.fanni.expense_tracker.model.Entry;
import com.fanni.expense_tracker.repository.EntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EntryServiceUnitTest {

    private EntryService service;
    @Mock
    private EntryRepository repository;

    private AppUser user;

    @BeforeEach
    public void initServiceAndUser() {
        this.service = new EntryService(repository);
        this.user = AppUser.builder().userName("testUser").build();
    }

    @Test
    void givenDatesAreCorrect_WhenRandomDateGenerates_ThenReturnsCorrectDate() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2021, 1, 1);
        LocalDate generatedDate = this.service.generateRandomExpense().getDate();
        assertTrue(generatedDate.isAfter(from) && generatedDate.isBefore(to));
    }

    @Test
    void givenExpenseGenerated_WhenBuilt_ThenNameNotNull() {
        Entry generatedExpense = this.service.generateRandomExpense();
        assertNotNull(generatedExpense.getName());
    }


}
