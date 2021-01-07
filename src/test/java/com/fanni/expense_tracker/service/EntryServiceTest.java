package com.fanni.expense_tracker.service;

import com.fanni.expense_tracker.model.Entry;
import com.fanni.expense_tracker.repository.EntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    void clearRepository(){
        service.clearRepository();
    }

    @Test
    void givenDatesAreCorrect_WhenRandomDateGenerates_ThenReturnsCorrectDate() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2021, 1, 1);
        LocalDate generated = service.generateRandomEntry().getDate();
        assertTrue(generated.isAfter(from) && generated.isBefore(to));
    }

    @Test
    void givenEntryGenerated_WhenBuilt_ThenNameNotNull() {
        Entry generatedEntry = service.generateRandomEntry();
        assertNotNull(generatedEntry.getName());
    }

    @Test
    void givenWhenRandomEntryCreated_ThenEntrySavedToRepository() {
        Entry createdEntry = service.createRandomEntry();
        Optional<Entry> savedEntry = repository.findById(createdEntry.getId());
        assertEquals(createdEntry, savedEntry.orElseThrow(() -> new RuntimeException("Entry is not present")));
    }

    @Test
    void givenEntriesAreSaved_WhenEntriesAreQueried_ThenAllGetsReturned() {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Entry entry = service.createRandomEntry();
            entries.add(entry);
        }
        List<Entry> repositoryEntries = new ArrayList<>(service.getAllEntries());
        assertIterableEquals(entries, repositoryEntries);
    }

    @Test
    void givenThereAreNoMatchingEntries_WhenEntriesAreQueriedByDatesBetween_ThenNoNullGetsReturned(){
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2021, 1, 1);
        assertNotNull(service.findEntriesByDateBetween(from, to));
    }

    @Test
    void givenEntriesAreSaved_WhenEntriesAreQueriedByDatesBetween_ThenAllGetsReturned(){
        IntStream.range(0, 5).forEach(i -> service.createRandomEntry());
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2021, 1, 1);
        assertEquals(5, service.findEntriesByDateBetween(from, to).size());
    }

    @Test
    void givenThereAreNoMatchingEntries_WhenEntriesAreQueriedByPriceBetween_ThenNoNullGetsReturned(){
        assertNotNull(service.findEntriesByPriceBetween(-100000, 100000));
    }

    @Test
    void givenEntriesAreSaved_WhenEntriesAreQueriedByPriceBetween_ThenAllGetsReturned(){
        IntStream.range(0, 5).forEach(i -> System.out.println(service.createRandomEntry()));
        assertEquals(5, service.findEntriesByPriceBetween(-5000, 5000).size());
    }



}