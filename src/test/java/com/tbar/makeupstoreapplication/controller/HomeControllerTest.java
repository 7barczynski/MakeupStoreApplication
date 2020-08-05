package com.tbar.makeupstoreapplication.controller;

import com.tbar.makeupstoreapplication.utility.AppProperties;
import com.tbar.makeupstoreapplication.utility.ViewNames;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HomeController.class)
@Import(AppProperties.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AppProperties appProperties;

    @Test
    void when_requestToHomePage_then_return200OK() throws Exception {
        mockMvc.perform(get("/").contentType(MediaType.TEXT_HTML)).andExpect(status().isOk());
    }

    @Test
    void when_requestToAboutPage_then_return200OK() throws Exception {
        mockMvc.perform(get("/about").contentType(MediaType.TEXT_HTML)).andExpect(status().isOk());
    }

    @Test
    void when_requestToHomePage_then_returnHomeView() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/").contentType(MediaType.TEXT_HTML)).andReturn();

        //noinspection ConstantConditions
        String actualViewName = mvcResult.getModelAndView().getViewName();
        assertEquals(ViewNames.HOME, actualViewName);
    }

    @Test
    void when_requestToAboutPage_then_returnAboutView() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/about").contentType(MediaType.TEXT_HTML)).andReturn();

        //noinspection ConstantConditions
        String actualViewName = mvcResult.getModelAndView().getViewName();
        assertEquals(ViewNames.ABOUT, actualViewName);
    }
}