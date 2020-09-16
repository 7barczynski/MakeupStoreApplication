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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.JoinType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SameParameterValue")
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
    private ArgumentCaptor<Specification<Product>> specificationArgumentCaptor;
    private MvcResult mvcResult;

    @Test
    void when_requestToShopPage_then_return200OK() throws Exception {
        performGetRequest().andExpect(status().isOk());
    }

    @Test
    void when_requestToShopPage_then_callOnceMakeupService() throws Exception {
        performGetRequest().andExpect(status().isOk());

        verify(makeupService, times(1)).findProducts(any(), any(Pageable.class));
    }

    @Test
    void when_requestToShopPage_then_addPageOfProductsToModel() throws Exception {
        Page<Product> expectedProductsPage = createPageWithContent(0, 12);
        mockServiceResponse(PageRequest.of(0, 12), expectedProductsPage);

        mvcResult = performGetRequestWithParameters(
                "page", "0",
                "size", "12").andReturn();

        Object actualPage = getActualPageFromModel(AttributeNames.PRODUCTS_LIST_ON_PAGE);
        assertEquals(expectedProductsPage, actualPage);
    }

    @Test
    void when_requestToShopPage_then_evaluatesPageableParameter() throws Exception {
        performGetRequestWithParameters(
                "page", "5",
                "size", "10",
                "sort", "name,desc",
                "sort", "price,asc").andExpect(status().isOk());

        Pageable pageRequest = captorPageableFromRequest();

        assertEquals(5, pageRequest.getPageNumber());
        assertEquals(10, pageRequest.getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "name")
                .and(Sort.by(Sort.Direction.ASC, "price")), pageRequest.getSort());
    }

    @Test
    void when_requestToShopPage_then_evaluatesSpecificationParameter() throws Exception {
        Specification<Product> expectedSpecification = createExpectedSpecification(
                "lipstick", "Natural");

        performGetRequestWithParameters("product_type", "lipstick", "product_tags", "Natural")
                .andExpect(status().isOk());

        Specification<Product> actualSpecification = captorSpecificationFromRequest();

        assertEquals(expectedSpecification, actualSpecification);
    }

    @Test
    void given_nothingFoundInDB_when_requestToShopPage_then_returnExceptionCaseInModel() throws Exception {
        mockServiceProductsNotFoundExceptionResponse();

        mvcResult = performGetRequest().andReturn();

        assertEquals(ExceptionHandlerUtilities.ExceptionCase.PRODUCTS_NOT_FOUND_EXCEPTION,
                getActualPageFromModel(AttributeNames.EXCEPTION));
    }

    private void mockServiceResponse(Pageable requestPageable, Page<Product> pageToReturn) throws ProductsNotFoundException {
        when(makeupService.findProducts(any(), eq(requestPageable))).thenReturn(pageToReturn);
    }

    private void mockServiceProductsNotFoundExceptionResponse() throws ProductsNotFoundException {
        when(makeupService.findProducts(any(), any(Pageable.class)))
                .thenThrow(ProductsNotFoundException.class);
    }

    private ResultActions performGetRequest() throws Exception {
        return mockMvc.perform(get("/shop")
                .contentType(MediaType.TEXT_HTML));
    }

    private ResultActions performGetRequestWithParameters(String p1, String v1, String p2, String v2) throws Exception {
        return mockMvc.perform(get("/shop")
                .contentType(MediaType.TEXT_HTML)
                .param(p1, v1)
                .param(p2, v2));
    }

    private ResultActions performGetRequestWithParameters(String p1, String v1, String p2, String v2,
                                                          String p3, String v3, String p4, String v4) throws Exception {
        return mockMvc.perform(get("/shop")
                .contentType(MediaType.TEXT_HTML)
                .param(p1, v1)
                .param(p2, v2)
                .param(p3, v3)
                .param(p4, v4));
    }

    private Page<Product> createPageWithContent(int pageNumber, int size) {
        List<Product> pageContent = Collections.nCopies(size, new Product());
        Pageable pageable = PageRequest.of(pageNumber, size);
        return new PageImpl<>(pageContent, pageable, pageContent.size());
    }

    private Specification<Product> createExpectedSpecification(String productTypeValue, String productTagsValue) {
        return new Conjunction<>(
                new Join<>(
                        new WebRequestQueryContext(nativeWebRequestMock),
                        "productTags", "pt", JoinType.INNER, true),
                new Conjunction<>(
                        new EmptyResultOnTypeMismatch<>(
                                new EqualIgnoreCase<>(
                                        new WebRequestQueryContext(nativeWebRequestMock),
                                        "productType", new String[]{productTypeValue},
                                        Converter.withTypeMismatchBehaviour(OnTypeMismatch.EMPTY_RESULT))),
                        new EmptyResultOnTypeMismatch<>(
                                new In<>(
                                        new WebRequestQueryContext(nativeWebRequestMock),
                                        "pt.name", new String[]{productTagsValue},
                                        Converter.withTypeMismatchBehaviour(OnTypeMismatch.EMPTY_RESULT)))
                )
        );
    }

    private Pageable captorPageableFromRequest() throws ProductsNotFoundException {
        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);
        verify(makeupService).findProducts(any(), pageableCaptor.capture());
        return pageableCaptor.getValue();
    }

    private Specification<Product> captorSpecificationFromRequest() throws ProductsNotFoundException {
        verify(makeupService).findProducts(specificationArgumentCaptor.capture(), any(Pageable.class));
        return specificationArgumentCaptor.getValue();
    }

    private Object getActualPageFromModel(String attributeName) {
        return Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get(attributeName);
    }
}