package com.fanni.expense_tracker.service;

import com.fanni.expense_tracker.model.Entry;
import com.fanni.expense_tracker.repository.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EntryService {

    @Autowired
    EntryRepository entryRepository;

    private LocalDate generateRandomDateBetween(LocalDate from, LocalDate to) {
        long minDay = from.toEpochDay();
        long maxDay = to.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
        System.out.println(randomDate);
        return randomDate;
    }

    public Entry generateRandomEntry() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2021, 1, 1);
        Random randomPrice = new Random(100L);
        return Entry.builder()
                .date(generateRandomDateBetween(from, to))
                .price(randomPrice.nextInt())
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

}
