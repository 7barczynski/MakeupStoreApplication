package com.tbar.makeupstoreapplication.controller;

import com.tbar.makeupstoreapplication.utility.ViewNames;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @Test
    void when_requestToHomePage_then_return200OK() throws Exception {
        performGetRequest().andExpect(status().isOk());
    }

    @Test
    void when_requestToHomePage_then_returnHomeView() throws Exception {
        mvcResult = performGetRequest().andReturn();

        String actualViewName = getViewNameFromModel();
        assertEquals(ViewNames.HOME, actualViewName);
    }

    private ResultActions performGetRequest() throws Exception {
        return mockMvc.perform(get("/").contentType(MediaType.TEXT_HTML));
    }

    private String getViewNameFromModel() {
        return Objects.requireNonNull(mvcResult.getModelAndView()).getViewName();
    }
}