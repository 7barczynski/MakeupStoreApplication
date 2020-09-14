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
import org.springframework.test.web.servlet.ResultActions;

import java.util.Objects;

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
    private MvcResult mvcResult;

    @Test
    void when_requestToHomePage_then_return200OK() throws Exception {
        performGetRequest("/").andExpect(status().isOk());
    }

    @Test
    void when_requestToAboutPage_then_return200OK() throws Exception {
        performGetRequest("/about").andExpect(status().isOk());
    }

    @Test
    void when_requestToHomePage_then_returnHomeView() throws Exception {
        mvcResult = performGetRequest("/").andReturn();

        String actualViewName = getViewNameFromModel();
        assertEquals(ViewNames.HOME, actualViewName);
    }

    @Test
    void when_requestToAboutPage_then_returnAboutView() throws Exception {
        mvcResult = performGetRequest("/about").andReturn();

        String actualViewName = getViewNameFromModel();
        assertEquals(ViewNames.ABOUT, actualViewName);
    }

    private ResultActions performGetRequest(String s) throws Exception {
        return mockMvc.perform(get(s).contentType(MediaType.TEXT_HTML));
    }

    private String getViewNameFromModel() {
        return Objects.requireNonNull(mvcResult.getModelAndView()).getViewName();
    }
}