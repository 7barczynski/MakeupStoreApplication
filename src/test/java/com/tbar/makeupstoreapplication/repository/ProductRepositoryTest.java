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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.JoinType;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SameParameterValue")
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Mock
    private NativeWebRequest nativeWebRequestMock;

    @Test
    void when_findAll_returnAllProducts() {
        Product product1 = createProductWithBrand(1L, "pure anada");
        Product product2 = createProductWithBrand(2L, "pure anada");
        Product anotherProduct = createProductWithBrand(3L, "maybelline");
        saveProducts(product1, product2, anotherProduct);

        List<Product> expectedListOfProducts = List.of(product1, product2);
        Specification<Product> specification = createSpecEqual("brand", "pure anada");
        Pageable pageable = PageRequest.of(0, 12);

        Page<Product> actualPageOfProducts = productRepository.findAll(specification, pageable);

        assertEquals(2, actualPageOfProducts.getContent().size());
        assertEquals(expectedListOfProducts, actualPageOfProducts.getContent());
    }

    @Disabled("help needed to work out what is wrong with this test")
    @Test
    void given_specificationWithProductTags_when_findAll_then_findAllProperItems() {
        Product product1 = createProductWithTags(1L, "ProperTag", "OtherTag");
        Product product2 = createProductWithTags(2L, "ProperTag");
        Product anotherProduct = createProductWithTags(3L, "OtherTag");
        saveProducts(product1, product2, anotherProduct);

        List<Product> expectedProducts = List.of(product1, product2);
        Specification<Product> specification = createSpecWithTag("ProperTag");
        Pageable pageable = PageRequest.of(0, 12);

        Page<Product> actualPageOfProducts = productRepository.findAll(specification, pageable);

        assertEquals(2, actualPageOfProducts.getContent().size());
        assertEquals(expectedProducts, actualPageOfProducts);
    }

    @Disabled("help needed to work out what is wrong with this test")
    @Test
    void given_specificationWithManyProductTags_when_findAll_then_findAllProperItems() {
        Product product1 = createProductWithTags(1L, "ProperTag", "ProperTag2", "OtherTag");
        Product product2 = createProductWithTags(2L, "ProperTag", "ProperTag2");
        Product anotherProduct = createProductWithTags(3L, "OtherTag");
        saveProducts(product1, product2, anotherProduct);

        List<Product> expectedProducts = List.of(product1, product2);
        Specification<Product> specification = createSpecWithTag("ProperTag", "ProperTag2");
        PageRequest pageable = PageRequest.of(0, 12);

        Page<Product> actualPageOfProducts = productRepository.findAll(specification, pageable);

        assertEquals(2, actualPageOfProducts.getContent().size());
        assertEquals(expectedProducts, actualPageOfProducts);
    }

    @Test
    void given_pageParameter_when_findAll_then_returnGivenPage() {
        createAndSaveNthProducts(15);

        Specification<Product> specification = new Conjunction<>();
        Pageable pageable = PageRequest.of(1, 12);

        Page<Product> actualPageOfProducts = productRepository.findAll(specification, pageable);

        assertEquals(3, actualPageOfProducts.getContent().size());
        assertEquals(1, actualPageOfProducts.getNumber());
    }

    @Test
    void given_sizeParameter_when_findAll_then_returnPageContentWithTheRightSize() {
        createAndSaveNthProducts(25);

        Specification<Product> specification = new Conjunction<>();
        Pageable pageable = PageRequest.of(0, 21);
        Page<Product> actualPageOfProducts = productRepository.findAll(specification, pageable);

        assertEquals(21, actualPageOfProducts.getContent().size());
    }

    @Test
    void given_sortParameter_when_findAll_then_returnSortedPageContent() {
        Product product1 = createProductWithName(1L, "Zzzzzzzz");
        Product product2 = createProductWithName(2L, "123123123");
        Product product3 = createProductWithName(3L, "Cccccccc");
        Product product4 = createProductWithName(4L, "Abcdefghijklmnoprs");
        saveProducts(product1, product2, product3, product4);

        List<Product> expectedProducts = List.of(product2, product4, product3, product1);

        Specification<Product> specification = new Conjunction<>();
        Pageable pageable = PageRequest.of(0, 12, Sort.by("name").ascending());

        Page<Product> actualPageOfProducts = productRepository.findAll(specification, pageable);

        assertEquals(expectedProducts, actualPageOfProducts.getContent());
    }

    @Test
    void given_nothingFoundInRepository_when_findAll_then_returnEmptyList() {
        Page<Product> expectedPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 12), 0);
        Specification<Product> specification = new Conjunction<>();

        Page<Product> actualPage = productRepository.findAll(specification, PageRequest.of(0, 12));

        assertEquals(expectedPage, actualPage);
    }

    @Test
    void given_savedEntity_when_findById_then_findThatEntity() {
        Product expectedProduct = createProductWithName(2L, "");
        productRepository.save(expectedProduct);

        Optional<Product> actualProduct = productRepository.findById(2L);

        //noinspection OptionalGetWithoutIsPresent
        assertEquals(expectedProduct, actualProduct.get());
    }

    @Test
    void given_nothingFoundInRepository_when_findById_then_returnNull() {
        Optional<Product> actualProduct = productRepository.findById(4L);

        assertTrue(actualProduct.isEmpty());
    }

    private void saveProducts(Product... products) {
        for (Product product : products) {
            productRepository.save(product);
        }
    }

    private Product createProductWithName(long id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        return product;
    }

    private Product createProductWithBrand(long id, String brand) {
        Product product = new Product();
        product.setId(id);
        product.setBrand(brand);
        return product;
    }

    private Product createProductWithTags(long id, String... tags) {
        Product product = new Product();
        product.setId(id);
        HashSet<ProductTag> productTags = createProductTagsFromStringArray(tags);
        product.setProductTags(productTags);
        return product;
    }

    private HashSet<ProductTag> createProductTagsFromStringArray(String[] tags) {
        HashSet<ProductTag> tagsToReturn = new HashSet<>();
        for (String tag : tags) {
            tagsToReturn.add(new ProductTag(tag));
        }
        return tagsToReturn;
    }

    private void createAndSaveNthProducts(int numberOfCopies) {
        List<Product> productsToSave = Collections.nCopies(numberOfCopies, new Product());
        long tempId = 1L;
        for (Product product : productsToSave) {
            product.setId(tempId++);
            productRepository.save(product);
        }
    }

    private Conjunction<Product> createSpecWithTag(String... tagName) {
        return new Conjunction<>(createJoin("productTags", "pt"),
                new Conjunction<>(createSpecIn("pt.name", tagName)));
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

    private Join<Product> createJoin(String pathToJoinOn, String alias) {
        return new Join<>(
                new WebRequestQueryContext(nativeWebRequestMock), pathToJoinOn, alias, JoinType.INNER, true);
    }
}