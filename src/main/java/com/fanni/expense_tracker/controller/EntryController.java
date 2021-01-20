package com.fanni.expense_tracker.controller;

import com.fanni.expense_tracker.model.Category;
import com.fanni.expense_tracker.model.Entry;
import com.fanni.expense_tracker.service.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    public List<Entry> getAllEntries() {
        return service.getAllEntries();
    }

    @GetMapping("/random")
    public Entry createRandomEntry() {
        return service.createRandomEntry();
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
        return service.updateEntryCategory(id, category);
    }

    @GetMapping("/category/all/count")
    public Map<Category, Integer> getEntryCountByCategory(){
        return service.countEntriesByCategory();
    }


}
