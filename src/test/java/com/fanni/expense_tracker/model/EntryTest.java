package com.fanni.expense_tracker.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class EntryTest {

    @Test
    void givenNewEntry_WhenCreated_ThenDateEqualsNow() {
        Entry testEntry = Entry.builder()
                .id(0L)
                .price(300)
                .name("teszt")
                .build();
        assertEquals(LocalDate.now(), testEntry.getDate());
    }
}