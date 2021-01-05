package com.fanni.expense_tracker.controller;

import com.fanni.expense_tracker.model.Entry;
import com.fanni.expense_tracker.service.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EntryController {

    @Autowired
    private EntryService service;

    @GetMapping
    public List<Entry> getAllEntries(){
        return service.getAllEntries();
    }

    @GetMapping("/random")
    public Entry createRandomEntry(){
        return service.createRandomEntry();
    }
}
