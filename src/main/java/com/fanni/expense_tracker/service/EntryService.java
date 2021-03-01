package com.fanni.expense_tracker.service;

import com.fanni.expense_tracker.model.Category;
import com.fanni.expense_tracker.model.CategoryCount;
import com.fanni.expense_tracker.model.Entry;
import com.fanni.expense_tracker.repository.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class EntryService {

    private final EntryRepository entryRepository;
    private final AppUserService userService;

    @Autowired
    public EntryService(EntryRepository entryRepository, AppUserService userService) {
        this.entryRepository = entryRepository;
        this.userService = userService;
    }

    private LocalDate generateRandomDateBetween(LocalDate from, LocalDate to) {
        long minDay = from.toEpochDay();
        long maxDay = to.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public Category generateRandomCategoryToFitPrice(int price) {
        Category[] incomeConstants = {Category.ONETIME_INCOME, Category.PAYMENT};
        Category[] expenseConstants = Arrays.stream(Category.class.getEnumConstants())
                .filter(category -> !Arrays.asList(incomeConstants).contains(category))
                .toArray(Category[]::new);

        Random random = new Random();
        int randomIndex;
        if (price >= 0) {
            randomIndex = random.nextInt(incomeConstants.length);
            return incomeConstants[randomIndex];
        } else {
            randomIndex = random.nextInt(expenseConstants.length);
            return expenseConstants[randomIndex];
        }
    }

    public Entry generateRandomExpense() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2021, 1, 1);
        LocalDate randomDate = generateRandomDateBetween(from, to);
        Random randomPrice = new Random();
        int generatedPrice = randomPrice.nextInt(499) - 500;
        return Entry.builder()
                .date(randomDate)
                .price(generatedPrice)
                .name(randomDate.format(DateTimeFormatter.ISO_DATE))
                .category(generateRandomCategoryToFitPrice(generatedPrice))
                .build();
    }

    public Entry generateRandomExpenseForUser(AppUser user){
        Entry entry = generateRandomExpense();
        entry.setUser(user);
        return entry;
    }

    public Entry createRandomExpense() {
        AppUser user = userService.getCurrentUser();
        Entry randomEntry = generateRandomExpenseForUser(user);
        entryRepository.saveAndFlush(randomEntry);
        return randomEntry;
    }

    public List<Entry> getAllEntries() {
        return entryRepository.findAll();
    }

    public Entry addEntry(Entry entry) {
        Entry toAddEntry = Entry.builder()
                .price(entry.getPrice())
                .name(entry.getName() != null ? entry.getName() : entry.getDate().format(DateTimeFormatter.ISO_DATE))
                .date(entry.getDate())
                .category(entry.getCategory())
                .build();
        entryRepository.saveAndFlush(toAddEntry);
        return toAddEntry;
    }

    public void clearRepository() {
        entryRepository.deleteAll();
    }

    public Set<Entry> findEntriesByDateBetween(LocalDate from, LocalDate to) {
        Set<Entry> entriesByDateIsBetween = entryRepository.findEntriesByDateIsBetween(from, to);
        if (entriesByDateIsBetween == null) {
            entriesByDateIsBetween = new HashSet<>();
        }
        return entriesByDateIsBetween;
    }

    public Set<Entry> findEntriesByPriceBetween(int priceFrom, int priceTo) {
        Set<Entry> entriesByPriceBetween = entryRepository.findEntriesByPriceBetween(priceFrom, priceTo);
        if (entriesByPriceBetween == null) {
            entriesByPriceBetween = new HashSet<>();
        }
        return entriesByPriceBetween;
    }

    public Entry updateEntryCategory(long id, Category category) {
        Entry updatedEntry = entryRepository.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException("No such entry found!");
        });
        entryRepository.updateCategory(updatedEntry.getId(), category);
        return entryRepository.findById(updatedEntry.getId()).orElseThrow(() -> {
            throw new NoSuchElementException("Categorizing went wrong, no such entry found");
        });
    }

    public List<CategoryCount> countEntriesByCategory() {
        return entryRepository.getEntriesByCategories();
    }

    public List<CategoryCount> getExpenseCountByCategory() {
        return entryRepository.getSpendingByCategories();
    }

    public List<CategoryCount> getTop5Spending() {
        return entryRepository.getSpendingByCategories()
                .stream()
                .limit(5)
                .collect(Collectors.toList());
    }
}
