package com.fanni.expense_tracker.controller;

import com.fanni.expense_tracker.model.Category;
import com.fanni.expense_tracker.model.CategoryCount;
import com.fanni.expense_tracker.model.Entry;
import com.fanni.expense_tracker.service.AppUserService;
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

    private final EntryService entryService;
    private final AppUserService userService;

    @Autowired
    public EntryController(EntryService entryService, AppUserService userService) {
        this.entryService = entryService;
        this.userService = userService;
    }

    @GetMapping
    public Set<Entry> getAllEntries() {
        return entryService.getAllEntries(userService.getCurrentUser());
    }

    @GetMapping("/random")
    public Entry createRandomEntry() {
        return entryService.createRandomExpense(userService.getCurrentUser());
    }

    @PostMapping("/add")
    public Entry addEntry(@RequestBody Entry entry) {
        return entryService.addEntry(entry, userService.getCurrentUser());
    }

    @GetMapping("/between/dates/{fromDate}/{toDate}")
    public Set<Entry> getAllEntriesBetweenDates(@PathVariable("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                @PathVariable("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        return entryService.findEntriesOfUserByDateBetween(from, to, userService.getCurrentUser());
    }
    @GetMapping("/between/price/{fromPrice}/{toPrice}")
    public Set<Entry> getAllEntriesBetweenPrice(@PathVariable("fromPrice") int priceFrom,
                                                @PathVariable("toPrice") int priceTo) {
        return entryService.findEntriesOfUserByPriceBetween(priceFrom, priceTo, userService.getCurrentUser());
    }

    @PutMapping("/addCategory/{id}/{category}")
    public Entry addCategoryToEntry(@PathVariable("id") long id, @PathVariable("category") Category category) {
        return entryService.updateEntryCategoryOfUser(id, category, userService.getCurrentUser());
    }

    @GetMapping("/category/all/count")
    public List<CategoryCount> getEntryCountByCategory(){
        return entryService.countEntriesOfUserByCategory(userService.getCurrentUser());
    }

    @GetMapping("/category/expense/count")
    public List<CategoryCount> getExpenseCountByCategory(){
        return entryService.getExpenseCountOfUserByCategory(userService.getCurrentUser());
    }

    @GetMapping("/category/top5spending")
    public List<CategoryCount> getTop5SpendingCategories(){
        return entryService.getTop5SpendingOfUser(userService.getCurrentUser());
    }

}
