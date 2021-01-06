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

//    @Test
//    void givenRandomRequested_WhenCallingEndpoint_ThenRandomEntryIsCreated() throws Exception {
//        Mockito.when(mockEntryService.createRandomEntry()).thenReturn(testEntry);
//        Mockito.when(controller.createRandomEntry()).thenCallRealMethod();
//        this.mockMvc.perform(get("/random"))
//                .andExpect(ResultMatcher.matchAll(
//                        jsonPath("$.price").value("300"),
//                        jsonPath("$.date").value("2021-01-30"),
//                        jsonPath("$.name").value("2021-01-30")
//                ));
//        Entry body = this.restTemplate.getForEntity("/random", Entry.class).getBody();
//
//    }

}