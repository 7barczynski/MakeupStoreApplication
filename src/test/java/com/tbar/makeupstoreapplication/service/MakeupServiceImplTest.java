package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.repository.ProductRepository;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;
import net.kaczmarzyk.spring.data.jpa.domain.Conjunction;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    void when_findProducts_then_returnPageOfProducts() throws ProductsNotFoundException {
        Page<Product> expectedPage = new PageImpl<>(Collections.nCopies(12, new Product()));
        mockProductRepositoryFindAll(expectedPage);

        Page<Product> actualPage = makeupService.findProducts(new Conjunction<>(), PageRequest.of(0, 12));

        assertEquals(expectedPage, actualPage);
    }

    @Test
    void given_nullFromSearchingRepository_when_findProducts_then_throwProductsNotFoundException() {
        mockProductRepositoryFindAll(null);

        assertThrows(ProductsNotFoundException.class, () -> makeupService.findProducts(
                new Conjunction<>(), PageRequest.of(0, 12)));
        verify(productRepository).findAll(ArgumentMatchers.<Specification<Product>>any(), any(Pageable.class));
    }

    @Test
    void given_emptyContentFromSearchingRepository_when_findProducts_then_throwProductsNotFoundException() {
        mockProductRepositoryFindAll(new PageImpl<>(Collections.emptyList()));

        assertThrows(ProductsNotFoundException.class, () -> makeupService.findProducts(
                new Conjunction<>(), PageRequest.of(0, 12)));
        verify(productRepository).findAll(ArgumentMatchers.<Specification<Product>>any(), any(Pageable.class));
    }

    @Test
    void when_findById_then_returnProduct() throws SingleProductNotFoundException {
        Product expectedProduct = new Product();
        mockProductRepositoryFindById(3L, Optional.of(expectedProduct));

        Product actualProduct = makeupService.findProduct(3L);

        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void given_emptyOptionalFromSearchingRepository_when_findProduct_then_throwSingleProductNotFoundException() {
        mockProductRepositoryFindById(0L, Optional.empty());

        assertThrows(SingleProductNotFoundException.class, () -> makeupService.findProduct(3L));
        verify(productRepository).findById(eq(3L));
    }

    @Test
    void when_getPaginationNumbers_then_returnListOfPaginationNumbers() {
        PageImpl<Product> page = new PageImpl<>(Collections.emptyList());
        List<Integer> expectedPaginationNumbers = List.of(1, 2, 3);

        when(paginationNumbersBuilderMock.build(page)).thenReturn(expectedPaginationNumbers);
        List<Integer> actualPaginationNumbers = makeupService.getPaginationNumbers(page);

        assertEquals(expectedPaginationNumbers, actualPaginationNumbers);
    }

    private void mockProductRepositoryFindAll(Page<Product> toReturnFromMock) {
        when(productRepository.findAll(ArgumentMatchers.<Specification<Product>>any(), any(Pageable.class))).
                thenReturn(toReturnFromMock);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void mockProductRepositoryFindById(long id, Optional<Product> toReturnFromMock) {
        when(productRepository.findById(eq(id))).thenReturn(toReturnFromMock);
    }
}