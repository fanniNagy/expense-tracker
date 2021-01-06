package com.fanni.expense_tracker.controller;

import com.fanni.expense_tracker.model.Entry;
import com.fanni.expense_tracker.service.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class EntryController {

    private final EntryService service;

    @Autowired
    public EntryController(EntryService service){
        this.service = service;
    }

    @GetMapping
    public List<Entry> getAllEntries(){
        return service.getAllEntries();
    }

    @GetMapping("/random")
    public Entry createRandomEntry(){
        return service.createRandomEntry();
    }

    @PostMapping("/add")
    public Entry addEntry(@RequestBody Entry entry){
        return service.addEntry(entry);
    }
}
