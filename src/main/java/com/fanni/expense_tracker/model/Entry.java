package com.fanni.expense_tracker.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class Entry {

    private final Long id;
    private int price;
    private String name;
    @Builder.Default
    private LocalDate date = LocalDate.now();

}
