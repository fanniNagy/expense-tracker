package com.fanni.expense_tracker.service;

import com.fanni.expense_tracker.model.AppUser;
import com.fanni.expense_tracker.model.Category;
import com.fanni.expense_tracker.model.CategoryCount;
import com.fanni.expense_tracker.model.Entry;
import com.fanni.expense_tracker.repository.AppUserRepository;
import com.fanni.expense_tracker.repository.EntryRepository;
import com.fanni.expense_tracker.security.PasswordConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EntryServiceIntegrationTest {

    private final EntryService service;
    private final EntryRepository repository;
    private AppUser testUser;
    private final AppUserRepository userRepository;


    @Autowired
    public EntryServiceIntegrationTest(EntryService service, EntryRepository repository, AppUserRepository userRepository) {
        this.service = service;
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void clearRepository() {
        service.clearRepository();
        this.testUser = AppUser.builder()
                .userName("test")
                .password(new PasswordConfig().passwordEncoder().encode("test"))
                .authorities(new HashSet<>() {
                    {
                        add(new SimpleGrantedAuthority("USER"));
                    }
                })
                .build();
        userRepository.saveAndFlush(testUser);
    }

    @Test
    void givenWhenRandomEntryCreated_ThenEntrySavedToRepository() {
        Entry createdEntry = service.createRandomExpense(testUser);
        Optional<Entry> savedEntry = repository.findById(createdEntry.getId());
        assertEquals(createdEntry, savedEntry.orElseThrow(() -> new RuntimeException("Entry is not present")));
    }

    @Test
    void givenEntriesAreSaved_WhenEntriesAreQueried_ThenAllGetsReturned() {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Entry entry = service.createRandomExpense(testUser);
            entries.add(entry);
        }
        List<Entry> repositoryEntries = new ArrayList<>(service.getAllEntries(testUser));
        assertIterableEquals(entries, repositoryEntries);
    }

    @Test
    void givenThereAreNoMatchingEntries_WhenEntriesAreQueriedByDatesBetween_ThenNoNullGetsReturned() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2021, 1, 1);
        assertNotNull(service.findEntriesOfUserByDateBetween(from, to, testUser));
    }

    @Test
    void givenEntriesAreSaved_WhenEntriesAreQueriedByDatesBetween_ThenAllGetsReturned() {
        IntStream.range(0, 5).forEach(i -> service.createRandomExpense(testUser));
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2021, 1, 1);
        assertEquals(5, service.findEntriesOfUserByDateBetween(from, to, testUser).size());
    }

    @Test
    void givenThereAreNoMatchingEntries_WhenEntriesAreQueriedByPriceBetween_ThenNoNullGetsReturned() {
        assertNotNull(service.findEntriesOfUserByPriceBetween(-100000, 100000, testUser));
    }

    @Test
    void givenEntriesAreSaved_WhenEntriesAreQueriedByPriceBetween_ThenAllGetsReturned() {
        IntStream.range(0, 5).forEach(i -> System.out.println(service.createRandomExpense(testUser)));
        assertEquals(5, service.findEntriesOfUserByPriceBetween(-5000, 5000, testUser).size());
    }

    @Test
    void givenNoElementWithIdFound_WhenEntryIsQueried_ThenNoSuchElementExceptionThrown() {
        assertThrows(NoSuchElementException.class, () -> service.updateEntryCategoryOfUser(1L, Category.FOOD, testUser));
    }

    @Test
    void givenElementIsFound_WhenEntryQueried_ThenCategoryIsUpdated() {
        Entry entry = service.createRandomExpense(testUser);
        assertEquals(Category.FOOD, service.updateEntryCategoryOfUser(entry.getId(), Category.FOOD, testUser).getCategory());
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
                        .build(), testUser);
        assertNotNull(repository.findById(testEntry.getId()));
    }

    @Test
    void givenThereAreEntriesInDatabaseWhenQueriedByCategoryThenReturnsCorrectCategoryCount() {
        service.addEntry(Entry.builder()
                .price(-200)
                .category(Category.FOOD)
                .build(), testUser);
        service.addEntry(Entry.builder()
                .price(-400)
                .category(Category.FOOD)
                .build(), testUser);
        List<CategoryCount> testList = new ArrayList<>();
        testList.add(CategoryCount.builder()
                .category(Category.FOOD)
                .price(-600L)
                .build());
        assertEquals(testList,
                service.countEntriesOfUserByCategory(testUser));
    }

    @Test
    void givenThereAreEntriesInDBWhenExpensesAreQueriedThenOnlyExpensesReturned() {
        service.addEntry(Entry.builder()
                .price(-200)
                .category(Category.FOOD)
                .build(), testUser);
        service.addEntry(Entry.builder()
                .price(400)
                .category(Category.ONETIME_INCOME)
                .build(), testUser);
        List<CategoryCount> testList = new ArrayList<>();
        testList.add(CategoryCount.builder()
                .category(Category.FOOD)
                .price(-200L)
                .build());
        assertEquals(testList,
                service.getExpenseCountOfUserByCategory(testUser));
    }

    @Test
    void givenThereAreEntriesInDBWhenTop5AreQueriedThenCategoriesWithBiggestSpendingReturned() {
        service.addEntry(Entry.builder().price(1000).category(Category.PAYMENT).build(), testUser);
        service.addEntry(Entry.builder().price(-7000).category(Category.PETS).build(), testUser);
        service.addEntry(Entry.builder().price(-2000).category(Category.FOOD).build(), testUser);
        service.addEntry(Entry.builder().price(-700).category(Category.TRANSPORTATION).build(), testUser);
        service.addEntry(Entry.builder().price(-600).category(Category.HOUSEHOLD).build(), testUser);
        service.addEntry(Entry.builder().price(-200).category(Category.MISCELLANEOUS).build(), testUser);

        List<CategoryCount> testList = new ArrayList<>(Arrays.asList(
                new CategoryCount(Category.PETS, -7000L),
                new CategoryCount(Category.FOOD, -2000L),
                new CategoryCount(Category.TRANSPORTATION, -700L),
                new CategoryCount(Category.HOUSEHOLD, -600L),
                new CategoryCount(Category.MISCELLANEOUS, -200L)));

        assertEquals(testList, service.getTop5SpendingOfUser(testUser));
    }
}