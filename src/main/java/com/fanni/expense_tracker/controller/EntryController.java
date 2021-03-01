package com.fanni.expense_tracker.controller;

import com.fanni.expense_tracker.model.Category;
import com.fanni.expense_tracker.model.CategoryCount;
import com.fanni.expense_tracker.model.Entry;
import com.fanni.expense_tracker.service.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
public class EntryController {

    private final EntryService service;

    @Autowired
    public EntryController(EntryService service) {
        this.service = service;
    }

    @GetMapping
    public Set<Entry> getAllEntries() {
        return service.getAllEntries();
    }

    @GetMapping("/random")
    public Entry createRandomEntry() {
        return service.createRandomExpense();
    }

    @PostMapping("/add")
    public Entry addEntry(@RequestBody Entry entry) {
        return service.addEntry(entry);
    }

    @GetMapping("/between/dates/{fromDate}/{toDate}")
    public Set<Entry> getAllEntriesBetweenDates(@PathVariable("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                @PathVariable("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        return service.findEntriesByDateBetween(from, to);
    }

    @PutMapping("/addCategory/{id}/{category}")
    public Entry addCategoryToEntry(@PathVariable("id") long id, @PathVariable("category") Category category) {
        return service.updateEntryCategoryOfUser(id, category);
    }

    @GetMapping("/category/all/count")
    public List<CategoryCount> getEntryCountByCategory(){
        return service.countEntriesOfUserByCategory();
    }

    @GetMapping("/category/expense/count")
    public List<CategoryCount> getExpenseCountByCategory(){
        return service.getExpenseCountOfUserByCategory();
    }

    @GetMapping("/category/top5spending")
    public List<CategoryCount> getTop5SpendingCategories(){
        return service.getTop5Spending();
    }

}
