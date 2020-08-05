package com.tbar.makeupstoreapplication.controller;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.service.MakeupService;
import com.tbar.makeupstoreapplication.utility.AppProperties;
import com.tbar.makeupstoreapplication.utility.AttributeNames;
import com.tbar.makeupstoreapplication.utility.ExceptionHandlerUtilities;
import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SingleProductController.class)
@Import(AppProperties.class)
class SingleProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AppProperties appProperties;
    @MockBean
    private MakeupService makeupService;

    @Test
    void when_requestToSingleProduct_then_return200OK() throws Exception {
        when(makeupService.findProduct(eq(3L)))
                .thenReturn(null);

        mockMvc.perform(get("/shop/3")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    @Test
    void when_requestToSingleProduct_then_callOnceMakeupService() throws Exception {
        when(makeupService.findProduct(eq(3L)))
                .thenReturn(null);

        mockMvc.perform(get("/shop/3")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());

        verify(makeupService, times(1)).findProduct(eq(3L));
    }

    @Test
    void when_requestToSingleProduct_then_addProductToModel() throws Exception {
        Product expectedProduct = new Product();
        when(makeupService.findProduct(eq(3L))).thenReturn(expectedProduct);

        MvcResult mvcResult = mockMvc.perform(get("/shop/3")
                .contentType(MediaType.TEXT_HTML))
                .andReturn();

        //noinspection ConstantConditions
        Object actualProduct = mvcResult.getModelAndView().getModel().get(AttributeNames.SINGLE_PRODUCT);
        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void given_nothingFoundInDB_when_shopPage_then_returnExceptionCaseInModel() throws Exception {
        when(makeupService.findProduct(anyLong()))
                .thenThrow(SingleProductNotFoundException.class);

        MvcResult mvcResult = mockMvc.perform(get("/shop/34")
                .contentType(MediaType.TEXT_HTML))
                .andReturn();

        //noinspection ConstantConditions
        assertEquals(ExceptionHandlerUtilities.ExceptionCase.SINGLE_PRODUCT_NOT_FOUND_EXCEPTION,
                mvcResult.getModelAndView().getModel().get(AttributeNames.EXCEPTION));
    }
}