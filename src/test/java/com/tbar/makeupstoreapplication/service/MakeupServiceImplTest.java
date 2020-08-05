package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.repository.ProductRepository;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MakeupServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private PaginationNumbersBuilder paginationNumbersBuilderMock;
    private MakeupService makeupService;

    @BeforeEach
    void initialize() {
        makeupService = new MakeupServiceImpl(productRepository, paginationNumbersBuilderMock);
    }

    @Test
    void given_validInput_when_findProducts_then_returnPageOfProducts() throws ProductsNotFoundException {
        Page<Product> expectedPage = new PageImpl<>(Collections.nCopies(12, new Product()));
        when(productRepository.findAll(ArgumentMatchers.<Specification<Product>>any(), any(Pageable.class))).
                thenReturn(expectedPage);

        Specification<Product> specification = Specification.where(
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        Pageable pageable = PageRequest.of(0, 12);
        Page<Product> actualPage = makeupService.findProducts(specification, pageable);

        Assertions.assertEquals(expectedPage, actualPage);
    }

    @Test
    void given_nullFromSearchingRepository_when_findProducts_then_throwProductsNotFoundException() {
        when(productRepository.findAll(ArgumentMatchers.<Specification<Product>>any(), any(Pageable.class)))
                .thenReturn(null);

        Specification<Product> specification = Specification.where(
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        Pageable pageable = PageRequest.of(0, 12);

        assertThrows(ProductsNotFoundException.class, () -> makeupService.findProducts(specification, pageable));
        verify(productRepository).findAll(ArgumentMatchers.<Specification<Product>>any(), any(Pageable.class));
    }

    @Test
    void given_emptyContentFromSearchingRepository_when_findProducts_then_throwProductsNotFoundException() {
        when(productRepository.findAll(ArgumentMatchers.<Specification<Product>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Specification<Product> specification = Specification.where(
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        Pageable pageable = PageRequest.of(0, 12);

        assertThrows(ProductsNotFoundException.class, () -> makeupService.findProducts(specification, pageable));
        verify(productRepository).findAll(ArgumentMatchers.<Specification<Product>>any(), any(Pageable.class));
    }

    @Test
    void given_validInput_when_findProduct_then_returnProduct() throws SingleProductNotFoundException {
        Product expectedProduct = new Product();
        when(productRepository.findById(eq(3L))).thenReturn(Optional.of(expectedProduct));

        Product actualProduct = makeupService.findProduct(3L);

        Assertions.assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void given_emptyOptionalFromSearchingRepository_when_findProduct_then_throwSingleProductNotFoundException() {
        when(productRepository.findById(eq(3L))).thenReturn(Optional.empty());

        assertThrows(SingleProductNotFoundException.class, () -> makeupService.findProduct(3L));
        verify(productRepository).findById(eq(3L));
    }

    @Test
    void given_page_when_getPaginationNumbers_return_listOfPaginationNumbers() {
        List<Product> products = Collections.nCopies(12, new Product());
        PageRequest pageRequest = PageRequest.of(0, 12);
        PageImpl<Product> expectedPage = new PageImpl<>(products, pageRequest, products.size());

        List<Integer> expectedPaginationNumbers = List.of(1, 2, 3);
        when(paginationNumbersBuilderMock.build(expectedPage)).thenReturn(expectedPaginationNumbers);

        List<Integer> actualPaginationNumbers = makeupService.getPaginationNumbers(expectedPage);
        assertEquals(expectedPaginationNumbers, actualPaginationNumbers);
    }
}