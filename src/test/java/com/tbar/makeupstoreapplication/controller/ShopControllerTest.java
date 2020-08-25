package com.tbar.makeupstoreapplication.controller;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.service.MakeupService;
import com.tbar.makeupstoreapplication.utility.AppProperties;
import com.tbar.makeupstoreapplication.utility.AttributeNames;
import com.tbar.makeupstoreapplication.utility.ExceptionHandlerUtilities;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import net.kaczmarzyk.spring.data.jpa.domain.*;
import net.kaczmarzyk.spring.data.jpa.utils.Converter;
import net.kaczmarzyk.spring.data.jpa.web.WebRequestQueryContext;
import net.kaczmarzyk.spring.data.jpa.web.annotation.OnTypeMismatch;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.JoinType;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ShopController.class)
@Import(AppProperties.class)
class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AppProperties appProperties;
    @MockBean
    private MakeupService makeupService;
    @Mock
    private NativeWebRequest nativeWebRequestMock;
    @Captor
    private ArgumentCaptor<Specification<Product>> specificationCaptor;

    @Test
    void when_requestToShopPage_then_return200OK() throws Exception {
        when(makeupService.findProducts(any(), any(Pageable.class)))
                .thenReturn(null);

        mockMvc.perform(get("/shop")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    @Test
    void when_requestToShopPage_then_callOnceMakeupService() throws Exception {
        when(makeupService.findProducts(any(), any(Pageable.class)))
                .thenReturn(null);

        mockMvc.perform(get("/shop")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());

        verify(makeupService, times(1)).findProducts(any(), any(Pageable.class));
    }

    @Test
    void when_requestToShopPage_then_addPageOfProductsToModel() throws Exception {
        List<Product> pageContent = Collections.nCopies(12, new Product());
        Pageable pageable = PageRequest.of(0, 12);
        Page<Product> expectedProductsPage = new PageImpl<>(pageContent, pageable, pageContent.size());
        when(makeupService.findProducts(any(), eq(pageable))).thenReturn(expectedProductsPage);

        MvcResult mvcResult = mockMvc.perform(get("/shop")
                .contentType(MediaType.TEXT_HTML)
                .param("page", "0")
                .param("size", "12"))
                .andReturn();

        //noinspection ConstantConditions
        Object actualPage = mvcResult.getModelAndView().getModel().get(AttributeNames.PRODUCTS_LIST_ON_PAGE);
        assertEquals(expectedProductsPage, actualPage);
    }

    @Test
    void when_requestToShopPage_then_evaluatesPageableParameter() throws Exception {
        mockMvc.perform(get("/shop")
                .param("page", "5")
                .param("size", "10")
                .param("sort", "name,desc")
                .param("sort", "price,asc"))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);
        verify(makeupService).findProducts(any(), pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();

        assertEquals(5, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "name")
                .and(Sort.by(Sort.Direction.ASC, "price")), pageable.getSort());
    }

    @Test
    void when_requestToShopPage_then_evaluatesSpecificationParameter() throws Exception {
        Specification<Product> expectedSpecification = new Conjunction<>(
                new Join<>(
                        new WebRequestQueryContext(nativeWebRequestMock),
                        "productTags", "pt", JoinType.INNER, true),
                new Conjunction<>(
                        new EmptyResultOnTypeMismatch<>(
                                new EqualIgnoreCase<>(
                                        new WebRequestQueryContext(nativeWebRequestMock),
                                        "productType", new String[]{"lipstick"},
                                        Converter.withTypeMismatchBehaviour(OnTypeMismatch.EMPTY_RESULT))),
                        new EmptyResultOnTypeMismatch<>(
                                new In<>(
                                        new WebRequestQueryContext(nativeWebRequestMock),
                                        "pt.name", new String[]{"Natural"},
                                        Converter.withTypeMismatchBehaviour(OnTypeMismatch.EMPTY_RESULT)))
                )
        );

        mockMvc.perform(get("/shop")
                .contentType(MediaType.TEXT_HTML)
                .param("product_type", "lipstick")
                .param("product_tags", "Natural"))
                .andExpect(status().isOk());

        verify(makeupService).findProducts(specificationCaptor.capture(), any(Pageable.class));
        Specification<Product> actualSpecification = specificationCaptor.getValue();

        assertEquals(expectedSpecification, actualSpecification);
    }

    @Test
    void given_nothingFoundInDB_when_requestToShopPage_then_returnExceptionCaseInModel() throws Exception {
        when(makeupService.findProducts(any(), any(Pageable.class)))
                .thenThrow(ProductsNotFoundException.class);

        MvcResult mvcResult = mockMvc.perform(get("/shop")
                .contentType(MediaType.TEXT_HTML))
                .andReturn();

        //noinspection ConstantConditions
        assertEquals(ExceptionHandlerUtilities.ExceptionCase.PRODUCTS_NOT_FOUND_EXCEPTION,
                mvcResult.getModelAndView().getModel().get(AttributeNames.EXCEPTION));
    }
}