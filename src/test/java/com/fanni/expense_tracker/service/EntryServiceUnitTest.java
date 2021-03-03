package com.fanni.expense_tracker.service;

import com.fanni.expense_tracker.model.AppUser;
import com.fanni.expense_tracker.model.Category;
import com.fanni.expense_tracker.model.Entry;
import com.fanni.expense_tracker.repository.EntryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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
    void givenAPositiveInteger_WhenRandomCategoryIsGenerated_AssertReturnsIncomeCategory() {
        assertTrue(Arrays.asList(Category.ONETIME_INCOME, Category.PAYMENT)
                .contains(this.service.generateRandomCategoryToFitPrice(2)));
    }

    @Test
    void givenANegativeInteger_WhenRandomCategoryIsGenerated_AssertReturnsExpenseCategory() {
        assertFalse(Arrays.asList(Category.ONETIME_INCOME, Category.PAYMENT)
                .contains(this.service.generateRandomCategoryToFitPrice(-2)));
    }

    @Test
    void givenRandomExpenseGenerated_WhenBuilt_ThenNameNotNull() {
        Entry generatedExpense = this.service.generateRandomExpense();
        assertNotNull(generatedExpense.getName());
    }

    @Test
    void givenRandomExpenseCreated_WhenMethodCalled_AssertEntryCreatedWithCorrectUser() {
        Entry randomExpense = this.service.createRandomExpense(this.user);
        Mockito.verify(this.repository,
                Mockito.times(1))
                .saveAndFlush(Mockito.any(Entry.class));
        Assertions.assertEquals(this.user, randomExpense.getUser());
    }

    @Test
    void givenRepositoryAvailable_WhenAllEntriesOfUserQueried_ThenRepositoryReturnsNotNull(){
        assertNotNull(this.service.getAllEntries(this.user));
        Mockito.verify(this.repository, Mockito.times(1)).findAllEntryByUserId(this.user.getId());
    }

    @Test
    void givenAnyEntryAndUserObjects_WhenEntryAdded_ExpectRepositoryReturnsNotNull(){
        Entry entry = Entry.builder()
                .user(this.user)
                .price(-200)
                .build();
        assertNotNull(this.service.addEntry(entry, this.user));
        Mockito.verify(this.repository,
                Mockito.times(1))
                .saveAndFlush(Mockito.any(Entry.class));
    }

    @Test
    void givenRepositoryAvailable_WhenRepositoryCleared_ThenNoEntryCanBeFound(){
        this.service.clearRepository();
        Mockito.verify(this.repository,
                Mockito.times(1))
                .deleteAll();
        assertTrue(this.repository.findAll().isEmpty());
    }

    @Test
    void givenThereAreNoMatchingEntriesInRepository_WhenEntriesAreQueriedByDateBetween_ThenNotNullReturned(){
        LocalDate testDate = LocalDate.now();
        assertNotNull(this.service.findEntriesOfUserByDateBetween(testDate, testDate, this.user));
        Mockito.verify(this.repository,
                Mockito.times(1))
                .findEntriesOfUserByDateIsBetween(user.getId(), testDate, testDate);
    }



}
