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
import org.springframework.test.web.servlet.ResultActions;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
    private MvcResult mvcResult;

    @Test
    void when_requestToSingleProductPage_then_return200OK() throws Exception {
        performGetRequest().andExpect(status().isOk());
    }

    @Test
    void when_requestToSingleProductPage_then_callOnceMakeupService() throws Exception {
        performGetRequest().andExpect(status().isOk());

        verify(makeupService, times(1)).findProduct(eq(3L));
    }

    @Test
    void when_requestToSingleProductPage_then_addProductToModel() throws Exception {
        Product expectedProduct = new Product();
        mockServiceResponse(expectedProduct);

        mvcResult = performGetRequest().andReturn();

        Product actualProduct = (Product) getActualProductFromModel(AttributeNames.SINGLE_PRODUCT);
        assertSame(expectedProduct, actualProduct);
    }

    @Test
    void given_nothingFoundInDB_when_requestToSingleProductPage_then_returnExceptionCaseInModel() throws Exception {
        mockServiceSingleProductNotFoundExceptionResponse();

        mvcResult = performGetRequest().andReturn();

        assertEquals(ExceptionHandlerUtilities.ExceptionCase.SINGLE_PRODUCT_NOT_FOUND_EXCEPTION,
                getActualProductFromModel(AttributeNames.EXCEPTION));
    }

    private ResultActions performGetRequest() throws Exception {
        return mockMvc.perform(get("/shop/3")
                .contentType(MediaType.TEXT_HTML));
    }

    private void mockServiceResponse(Product productToReturn) throws SingleProductNotFoundException {
        when(makeupService.findProduct(eq(3L)))
                .thenReturn(productToReturn);
    }

    private Object getActualProductFromModel(String attributeName) {
        return Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get(attributeName);
    }

    private void mockServiceSingleProductNotFoundExceptionResponse() throws SingleProductNotFoundException {
        when(makeupService.findProduct(anyLong()))
                .thenThrow(SingleProductNotFoundException.class);
    }
}