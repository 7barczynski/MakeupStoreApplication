package com.tbar.makeupstoreapplication.repository;

import com.tbar.makeupstoreapplication.model.Product;
import net.kaczmarzyk.spring.data.jpa.domain.Conjunction;
import net.kaczmarzyk.spring.data.jpa.domain.EmptyResultOnTypeMismatch;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.utils.Converter;
import net.kaczmarzyk.spring.data.jpa.web.WebRequestQueryContext;
import net.kaczmarzyk.spring.data.jpa.web.annotation.OnTypeMismatch;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Mock
    private NativeWebRequest nativeWebRequestMock;

    @Test
    void given_savedEntities_when_findAll_then_findTheseEntities() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setBrand("pure anada");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setBrand("pure anada");

        Product anotherProduct = new Product();
        anotherProduct.setId(3L);
        anotherProduct.setBrand("maybelline");

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(anotherProduct);

        Specification<Product> specification = new Conjunction<>(
                new EmptyResultOnTypeMismatch<>(
                        new Equal<>(
                                new WebRequestQueryContext(nativeWebRequestMock), "brand", new String[]{"pure anada"},
                                Converter.withTypeMismatchBehaviour(OnTypeMismatch.EMPTY_RESULT))));

        Page<Product> actualProduct = productRepository.findAll(specification, PageRequest.of(0, 12));

        assertEquals(List.of(product1, product2), actualProduct.getContent());
    }

    @Test
    void given_nothingFoundInRepository_when_findAll_then_returnEmptyList() {
        Page<Product> expectedPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 12), 0);

        Specification<Product> specification = new Conjunction<>(
                new EmptyResultOnTypeMismatch<>(
                        new Equal<>(
                                new WebRequestQueryContext(nativeWebRequestMock), "brand", new String[]{"pure anada"},
                                Converter.withTypeMismatchBehaviour(OnTypeMismatch.EMPTY_RESULT))));
        Page<Product> actualPage = productRepository.findAll(specification, PageRequest.of(0, 12));

        assertEquals(expectedPage, actualPage);
    }

    @Test
    void given_savedEntity_when_findById_then_findThatEntity() {
        Product expectedProduct = new Product();
        expectedProduct.setId(2L);
        productRepository.save(expectedProduct);

        Optional<Product> actualProduct = productRepository.findById(2L);

        //noinspection OptionalGetWithoutIsPresent
        assertEquals(expectedProduct.getProductColors(), actualProduct.get().getProductColors());
    }

    @Test
    void given_nothingFoundInRepository_when_findById_then_returnNull() {
        Optional<Product> actualProduct = productRepository.findById(4L);
        assertTrue(actualProduct.isEmpty());
    }
}