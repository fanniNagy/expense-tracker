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
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;

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
        this.user = AppUser.builder().id(0L).userName("testUser").build();
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
    void givenRepositoryAvailable_WhenAllEntriesOfUserQueried_ThenRepositoryReturnsNotNull() {
        assertNotNull(this.service.getAllEntries(this.user));
        Mockito.verify(this.repository, Mockito.times(1)).findAllEntryByUserId(this.user.getId());
    }

    @Test
    void givenAnyEntryAndUserObjects_WhenEntryAdded_ExpectRepositoryReturnsNotNull() {
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
    void givenRepositoryAvailable_WhenRepositoryCleared_ThenNoEntryCanBeFound() {
        this.service.clearRepository();
        Mockito.verify(this.repository,
                Mockito.times(1))
                .deleteAll();
        assertTrue(this.repository.findAll().isEmpty());
    }

    @Test
    void givenThereAreNoMatchingEntriesInRepository_WhenEntriesAreQueriedByDateBetween_ThenNotNullReturned() {
        LocalDate testDate = LocalDate.now();
        assertNotNull(this.service.findEntriesOfUserByDateBetween(testDate, testDate, this.user));
        Mockito.verify(this.repository,
                Mockito.times(1))
                .findEntriesOfUserByDateIsBetween(user.getId(), testDate, testDate);
    }

    @Test
    void givenThereAreNoMatchingEntriesInRepository_WhenEntriesAreQueriedByDateBetween_ThenEmptyHashSetReturned() {
        LocalDate testDate = LocalDate.now();
        Mockito
                .when(this.repository.findEntriesOfUserByDateIsBetween(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(null);
        assertEquals(HashSet.class, this.service.findEntriesOfUserByDateBetween(testDate, testDate, this.user).getClass());
        assertTrue(this.service.findEntriesOfUserByDateBetween(testDate, testDate, this.user).isEmpty());
    }

    @Test
    void givenThereAreNoMatchingEntriesInRepository_WhenEntriesAreQueriedByPriceBetween_ThenNotNullReturned() {
        int testPriceFrom = -2000;
        int testPriceTo = -200;
        assertNotNull(this.service.findEntriesOfUserByPriceBetween(testPriceFrom, testPriceTo, this.user));
        Mockito.verify(this.repository,
                Mockito.times(1))
                .findEntriesOfUserByPriceBetween(user.getId(), testPriceFrom, testPriceTo);
    }

    @Test
    void givenThereAreNoMatchingEntriesInRepository_WhenEntriesAreQueriedByPriceBetween_ThenEmptyHashSetReturned() {
        int testPriceFrom = -2000;
        int testPriceTo = -200;
        Mockito
                .when(this.repository.findEntriesOfUserByPriceBetween(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(null);
        assertEquals(HashSet.class,
                this.service.findEntriesOfUserByPriceBetween(testPriceFrom, testPriceTo, this.user).getClass());
        assertTrue(this.service.findEntriesOfUserByPriceBetween(testPriceFrom, testPriceTo, this.user).isEmpty());
    }

    @Test
    void givenNoEntryCanBeFound_WhenEntryQueriedForUpdatingCategory_AssertThrowsProperException() {
        Mockito
                .when(this.repository.findById(Mockito.any()))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> this.service.updateEntryCategoryOfUser(0L, Category.FOOD, this.user),
                "No such entry found!");
    }

    @Test
    void givenEntryFoundToUpdate_WhenEntryUpdated_ThenReturnsCorrectEntry() {
        Entry entry = Entry.builder()
                .id(0L)
                .user(this.user)
                .price(-200)
                .category(Category.UNCATEGORIZED)
                .build();
        Category givenCategory = Category.FOOD;

        Mockito
                .when(this.repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entry));

        entry.setCategory(givenCategory);

        assertEquals(givenCategory,
                this.service.updateEntryCategoryOfUser(entry.getId(), givenCategory, this.user).getCategory());

        Mockito.verify(this.repository, Mockito.times(1))
                .updateCategory(entry.getId(), givenCategory, entry.getUser().getId());
    }

    @Test
    void givenEntryFoundAndUpdated_WhenQueriedToReturnButNotFound_AssertThrowsProperException() {
        Entry entry = Entry.builder()
                .id(0L)
                .user(this.user)
                .price(-200)
                .category(Category.UNCATEGORIZED)
                .build();
        Category givenCategory = Category.FOOD;

        entry.setCategory(givenCategory);

        Mockito
                .doReturn(Optional.of(entry))
                .doReturn(Optional.empty())
                .when(this.repository).findById(entry.getId());

        assertThrows(NoSuchElementException.class,
                () -> this.service.updateEntryCategoryOfUser(entry.getId(), Category.FOOD, this.user),
                "Categorizing went wrong, no such entry found");

        Throwable exceptionThatWasThrown = assertThrows(NoSuchElementException.class, () -> {
            this.service.updateEntryCategoryOfUser(entry.getId(), Category.FOOD, this.user);
        });

        assertEquals("Categorizing went wrong, no such entry found", exceptionThatWasThrown.getMessage());
    }

    @Test
    void givenThereAreNoEntries_WhenEntriesQueriedByCategory_NoNullReturned() {
        Mockito
                .verify(this.repository, Mockito.times(1))
                .getEntriesOfUserByCategories(this.user.getId());
    }

    @Test
    void givenThereAreNoEntries_WhenEntriesQueriedToCountByCategories_NoNullReturned() {
        assertNotNull(this.service.getExpenseCountOfUserByCategory(this.user));
        Mockito
                .verify(this.repository, Mockito.times(1))
                .getSpendingOfUserByCategories(this.user.getId());
    }

}
