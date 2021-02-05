package com.fanni.expense_tracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CategoryCount {

    private Category category;
    private Long price;
}
