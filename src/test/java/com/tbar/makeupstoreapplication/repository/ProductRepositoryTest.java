package com.tbar.makeupstoreapplication.repository;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.model.ProductTag;
import net.kaczmarzyk.spring.data.jpa.domain.*;
import net.kaczmarzyk.spring.data.jpa.utils.Converter;
import net.kaczmarzyk.spring.data.jpa.web.WebRequestQueryContext;
import net.kaczmarzyk.spring.data.jpa.web.annotation.OnTypeMismatch;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.JoinType;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SameParameterValue")
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Mock
    private NativeWebRequest nativeWebRequestMock;

    @Test
    void when_findAll_returnAllProducts() {
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
        List<Product> expectedListOfProducts = List.of(product1, product2);

        Specification<Product> specification = new Conjunction<>(
                createSpecEqual("brand", "pure anada"));

        Page<Product> actualProducts = productRepository.findAll(specification, PageRequest.of(0, 12));

        assertEquals(2, actualProducts.getContent().size());
        assertEquals(expectedListOfProducts, actualProducts.getContent());
    }
    @Disabled // TODO help needed to work out what is wrong with this test
    @Test
    void given_specificationWithProductTags_when_findAll_then_findAllProperItems() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductTags(Set.of(new ProductTag("ProperTag"), new ProductTag("OtherTag")));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductTags(Set.of(new ProductTag("OtherTag")));

        Product product3 = new Product();
        product3.setId(3L);
        product2.setProductTags(Set.of(new ProductTag("ProperTag")));

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        List<Product> expectedProducts = List.of(product1, product3);

        Specification<Product> specification = new Conjunction<>(createJoin("productTags", "pt"),
                new Conjunction<>(createSpecIn("pt.name", new String[]{"ProperTag"})));

        Page<Product> actualProducts = productRepository.findAll(specification, PageRequest.of(0, 12));

        assertEquals(2, actualProducts.getContent().size());
        assertEquals(expectedProducts, actualProducts);
    }

    @Disabled // TODO help needed to work out what is wrong with this test
    @Test
    void given_specificationWithManyProductTags_when_findAll_then_findAllProperItems() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductTags(Set.of(new ProductTag("ProperTag"),
                new ProductTag("AnotherProperTag"), new ProductTag("OtherTag")));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductTags(Set.of(new ProductTag("OtherTag")));

        Product product3 = new Product();
        product3.setId(3L);
        product2.setProductTags(Set.of(new ProductTag("ProperTag"), new ProductTag("AnotherProperTag")));

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        List<Product> expectedProducts = List.of(product1, product3);

        Specification<Product> specification = new Conjunction<>(createJoin("productTags", "pt"),
                new Conjunction<>(createSpecIn("pt.name", new String[]{"Proper Tag", "AnotherProperTag"})));

        Page<Product> actualProducts = productRepository.findAll(specification, PageRequest.of(0, 12));

        assertEquals(2, actualProducts.getContent().size());
        assertEquals(expectedProducts, actualProducts);
    }

    @Test
    void given_nothingFoundInRepository_when_findAll_then_returnEmptyList() {
        Page<Product> expectedPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 12), 0);
        Specification<Product> specification = new Conjunction<>(
                createSpecEqual("brand", "pure anada"));

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

    private Join<Product> createJoin(String pathToJoinOn, String alias) {
        return new Join<>(
                new WebRequestQueryContext(nativeWebRequestMock), pathToJoinOn, alias, JoinType.INNER, true);
    }

    private EmptyResultOnTypeMismatch<Product> createSpecEqual(String path, String expectedValue) {
        return new EmptyResultOnTypeMismatch<>(
                new EqualIgnoreCase<>(
                        new WebRequestQueryContext(nativeWebRequestMock), path, new String[]{expectedValue},
                        Converter.withTypeMismatchBehaviour(OnTypeMismatch.EMPTY_RESULT)));
    }

    private EmptyResultOnTypeMismatch<Product> createSpecIn(String path, String[] expectedValue) {
        return new EmptyResultOnTypeMismatch<>(
                new In<>(
                        new WebRequestQueryContext(nativeWebRequestMock), path, expectedValue,
                        Converter.withTypeMismatchBehaviour(OnTypeMismatch.EMPTY_RESULT)));
    }
}