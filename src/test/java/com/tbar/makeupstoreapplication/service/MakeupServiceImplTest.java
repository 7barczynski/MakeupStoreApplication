package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.dao.ProductDAOImpl;
import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MakeupServiceImplTest {

    @Mock
    private ProductDAOImpl productDaoMock;
    @Mock
    private PaginationNumbersBuilder paginationNumbersBuilderMock;
    @Mock
    private ProductPageCreator productPageCreatorMock;
    @Mock
    private DAOMapCreator daoMapCreatorMock;
    private MakeupService makeupService;
    private int sizeOfProductListOnPage = 12;
    private Product expectedProduct = new Product();
    private List<Product> expectedList = List.of(expectedProduct);
    private Map<String, String> parametersMap = new HashMap<>();
    private List<Product> products = new ArrayList<>(List.of(expectedProduct));
    private int page = 0;
    private Long id = 0L;
    private PageRequest pageRequest = PageRequest.of(page, sizeOfProductListOnPage);
    private Page<Product> expectedPage = new PageImpl<>(products,
            pageRequest, expectedList.size());
    private List<Integer> expectedPaginationNumbers = List.of(1, 2, 3);

    @BeforeEach
    void initialize() {
        makeupService = new MakeupServiceImpl(productDaoMock, productPageCreatorMock,
                daoMapCreatorMock, paginationNumbersBuilderMock);
    }

    @Test
    void given_mapOfParametersAndPage_when_getPaginatedProducts_return_pageOfProducts() throws ProductsNotFoundException {
        when(daoMapCreatorMock.createMap(parametersMap)).thenReturn(parametersMap);
        when(productDaoMock.getProducts(parametersMap)).thenReturn(products);
        when(productPageCreatorMock.createProductPage(products, page)).thenReturn(expectedPage);

        Page<Product> actualPage = makeupService.getPaginatedProducts(parametersMap, page);
        assertEquals(expectedPage, actualPage);
    }

    @Test
    void given_nullMapOfParametersAndPage_when_getPaginatedProducts_return_pageOfProducts() throws ProductsNotFoundException {
        when(daoMapCreatorMock.createMap(null)).thenReturn(parametersMap);
        when(productDaoMock.getProducts(parametersMap)).thenReturn(products);
        when(productPageCreatorMock.createProductPage(products, page)).thenReturn(expectedPage);

        Page<Product> actualPage = makeupService.getPaginatedProducts(null, page);
        assertEquals(expectedPage, actualPage);
    }

    @Test
    void given_noProductsToGet_when_getPaginatedProducts_throw_ProductsNotFoundException() {
        when(daoMapCreatorMock.createMap(parametersMap)).thenReturn(parametersMap);
        when(productDaoMock.getProducts(parametersMap)).thenReturn(Collections.emptyList());

        Assertions.assertThrows(ProductsNotFoundException.class,
                () -> makeupService.getPaginatedProducts(parametersMap, page));
    }

    @Test
    void given_nullProducts_when_getPaginatedProducts_throw_ProductsNotFoundException() {
        when(daoMapCreatorMock.createMap(parametersMap)).thenReturn(parametersMap);
        when(productDaoMock.getProducts(parametersMap)).thenReturn(null);

        Assertions.assertThrows(ProductsNotFoundException.class,
                () -> makeupService.getPaginatedProducts(parametersMap, page));
    }

    @Test
    void given_id_when_getProduct_return_product() throws SingleProductNotFoundException {
        when(productDaoMock.getProduct(id)).thenReturn(expectedProduct);

        Product actualProduct = makeupService.getProduct(id);
        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void given_nullProduct_when_getProduct_throw_SingleProductNotFoundException() {
        when(productDaoMock.getProduct(id)).thenReturn(null);

        assertThrows(SingleProductNotFoundException.class, () -> makeupService.getProduct(id));
    }

    @Test
    void given_page_when_getPaginationNumbers_return_listOfPaginationNumbers() {
        when(paginationNumbersBuilderMock.build(expectedPage)).thenReturn(expectedPaginationNumbers);

        List<Integer> actualPaginationNumbers = makeupService.getPaginationNumbers(expectedPage);
        assertEquals(expectedPaginationNumbers, actualPaginationNumbers);
    }
}