package com.fanni.expense_tracker.service;

import com.fanni.expense_tracker.model.Category;
import com.fanni.expense_tracker.model.CategoryCount;
import com.fanni.expense_tracker.model.Entry;
import com.fanni.expense_tracker.repository.EntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EntryServiceTest {

    private final EntryService service;
    private final EntryRepository repository;

    @Autowired
    public EntryServiceTest(EntryService service, EntryRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @BeforeEach
    void clearRepository() {
        service.clearRepository();
    }

    @Test
    void givenDatesAreCorrect_WhenRandomDateGenerates_ThenReturnsCorrectDate() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2021, 1, 1);
        LocalDate generated = service.generateRandomExpense().getDate();
        assertTrue(generated.isAfter(from) && generated.isBefore(to));
    }

    @Test
    void givenEntryGenerated_WhenBuilt_ThenNameNotNull() {
        Entry generatedEntry = service.generateRandomExpense();
        assertNotNull(generatedEntry.getName());
    }

    @Test
    void givenWhenRandomEntryCreated_ThenEntrySavedToRepository() {
        Entry createdEntry = service.createRandomExpense();
        Optional<Entry> savedEntry = repository.findById(createdEntry.getId());
        assertEquals(createdEntry, savedEntry.orElseThrow(() -> new RuntimeException("Entry is not present")));
    }

    @Test
    void givenEntriesAreSaved_WhenEntriesAreQueried_ThenAllGetsReturned() {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Entry entry = service.createRandomExpense();
            entries.add(entry);
        }
        List<Entry> repositoryEntries = new ArrayList<>(service.getAllEntries());
        assertIterableEquals(entries, repositoryEntries);
    }

    @Test
    void givenThereAreNoMatchingEntries_WhenEntriesAreQueriedByDatesBetween_ThenNoNullGetsReturned() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2021, 1, 1);
        assertNotNull(service.findEntriesByDateBetween(from, to));
    }

    @Test
    void givenEntriesAreSaved_WhenEntriesAreQueriedByDatesBetween_ThenAllGetsReturned() {
        IntStream.range(0, 5).forEach(i -> service.createRandomExpense());
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2021, 1, 1);
        assertEquals(5, service.findEntriesByDateBetween(from, to).size());
    }

    @Test
    void givenThereAreNoMatchingEntries_WhenEntriesAreQueriedByPriceBetween_ThenNoNullGetsReturned() {
        assertNotNull(service.findEntriesByPriceBetween(-100000, 100000));
    }

    @Test
    void givenEntriesAreSaved_WhenEntriesAreQueriedByPriceBetween_ThenAllGetsReturned() {
        IntStream.range(0, 5).forEach(i -> System.out.println(service.createRandomExpense()));
        assertEquals(5, service.findEntriesByPriceBetween(-5000, 5000).size());
    }

    @Test
    void givenNoElementWithIdFound_WhenEntryIsQueried_ThenNoSuchElementExceptionThrown() {
        assertThrows(NoSuchElementException.class, () -> service.updateEntryCategory(1L, Category.FOOD));
    }

    @Test
    void givenElementIsFound_WhenEntryQueried_ThenCategoryIsUpdated() {
        Entry entry = service.createRandomExpense();
        assertEquals(Category.FOOD, service.updateEntryCategory(entry.getId(), Category.FOOD).getCategory());
    }

    @Test
    void givenCategoriesWhenRandomCategoryGeneratedThenReturnsExistingCategory() {
        Random random = new Random();
        int randomPrice = random.nextInt(10000) - 5000;
        Category category = service.generateRandomCategoryToFitPrice(randomPrice);
        assertDoesNotThrow(() -> Arrays.stream(Category.class.getEnumConstants())
                .filter(category::equals)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No such Category found!")));
    }

    @Test
    void givenCategoriesWhenRandomCategoryGeneratedWithPositiveValueThenReturnsIncomeCategory() {
        Category category = service.generateRandomCategoryToFitPrice(300);
        List<Category> incomeCategories = new ArrayList<>(Arrays.asList(Category.ONETIME_INCOME, Category.PAYMENT));
        assertTrue(incomeCategories.contains(category));
    }

    @Test
    void givenEntryHasPriceWhenEntryAddedThenEntrySavedInRepository() {
        Entry testEntry = service
                .addEntry(Entry
                        .builder()
                        .price(-600)
                        .build());
        assertNotNull(repository.findById(testEntry.getId()));
    }
    @Test
    void givenThereAreEntriesInDatabaseWhenQueriedByCategoryThenReturnsCorrectCategoryCount() {
        service.addEntry(Entry.builder()
                .price(-200)
                .category(Category.FOOD)
                .build());
        service.addEntry(Entry.builder()
                .price(-400)
                .category(Category.FOOD)
                .build());
        List<CategoryCount> testList = new ArrayList<>();
        testList.add(CategoryCount.builder()
                .category(Category.FOOD)
                .price(-600L)
                .build());
        assertEquals(testList,
                service.countEntriesByCategory());
    }

    @Test
    void givenThereAreEntriesInDBWhenExpensesAreQueriedThenOnlyExpensesReturned() {
        service.addEntry(Entry.builder()
                .price(-200)
                .category(Category.FOOD)
                .build());
        service.addEntry(Entry.builder()
                .price(400)
                .category(Category.ONETIME_INCOME)
                .build());
        List<CategoryCount> testList = new ArrayList<>();
        testList.add(CategoryCount.builder()
                .category(Category.FOOD)
                .price(-200L)
                .build());
        assertEquals(testList,
                service.getExpenseCountByCategory());
    }

    @Test
    void givenThereAreEntriesInDBWhenTop5AreQueriedThenCategoriesWithBiggestSpendingReturned() {
        service.addEntry(Entry.builder().price(1000).category(Category.PAYMENT).build());
        service.addEntry(Entry.builder().price(-7000).category(Category.PETS).build());
        service.addEntry(Entry.builder().price(-2000).category(Category.FOOD).build());
        service.addEntry(Entry.builder().price(-700).category(Category.TRANSPORTATION).build());
        service.addEntry(Entry.builder().price(-600).category(Category.HOUSEHOLD).build());
        service.addEntry(Entry.builder().price(-200).category(Category.MISCELLANEOUS).build());

        List<CategoryCount> testList = new ArrayList<>(Arrays.asList(
                new CategoryCount(Category.PETS, -7000L),
                new CategoryCount(Category.FOOD, -2000L),
                new CategoryCount(Category.TRANSPORTATION, -700L),
                new CategoryCount(Category.HOUSEHOLD, -600L),
                new CategoryCount(Category.MISCELLANEOUS, -200L)));

        assertEquals(testList, service.getTop5Spending());
    }
}