package com.fanni.expense_tracker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EntryControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    public EntryControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void givenGetRequest_WhenCallingEndpoint_ExpectOKStatus() throws Exception {
        this.mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void givenGetRequestOnRandom_WhenCallingEndpoint_ExpectOKStatus() throws Exception {
        this.mockMvc.perform(get("/random")).andExpect(status().isOk());
    }

    @Test
    void givenDBIsNotEmptyAndGetRequestOnDatesBetween_WhenGetRequestOnDatesBetween_ExpectOKStatus() throws Exception {
        this.mockMvc.perform(get("/random"));
        this.mockMvc.perform(get("/between/dates/2020-01-01/2021-01-01")).andExpect(status().isOk());
    }


}