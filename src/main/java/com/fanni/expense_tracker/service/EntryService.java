package com.fanni.expense_tracker.service;

import com.fanni.expense_tracker.model.Category;
import com.fanni.expense_tracker.model.Entry;
import com.fanni.expense_tracker.repository.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EntryService {

    private final EntryRepository entryRepository;

    @Autowired
    public EntryService(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    private LocalDate generateRandomDateBetween(LocalDate from, LocalDate to) {
        long minDay = from.toEpochDay();
        long maxDay = to.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public Entry generateRandomEntry() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2021, 1, 1);
        LocalDate randomDate = generateRandomDateBetween(from, to);
        Random randomPrice = new Random();
        return Entry.builder()
                .date(randomDate)
                .price(randomPrice.nextInt(10000) - 5000)
                .name(randomDate.format(DateTimeFormatter.ISO_DATE))
                .build();
    }

    public Entry createRandomEntry() {
        Entry randomEntry = generateRandomEntry();
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

    public Map<Category, Integer> countEntriesByCategory() {
        Map<Category, Integer> entryCountByCategory = new TreeMap<>();
        List<Entry> allEntries = getAllEntries();
        for (Entry entry : allEntries) {
            Category category = entry.getCategory();
            entryCountByCategory.merge(category, 1, Integer::sum);
        }
        return entryCountByCategory;
    }
}
