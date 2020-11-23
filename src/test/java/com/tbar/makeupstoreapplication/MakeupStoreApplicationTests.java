package com.tbar.makeupstoreapplication;

import com.tbar.makeupstoreapplication.model.Color;
import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.model.ProductTag;
import com.tbar.makeupstoreapplication.repository.ProductRepository;
import com.tbar.makeupstoreapplication.utility.AttributeNames;
import com.tbar.makeupstoreapplication.utility.ExceptionHandlerUtilities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class MakeupStoreApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;
    private MvcResult mvcResult;

    @Test
    void when_userRequestShopPage_then_returnAllProductsFromDatabase() throws Exception {
        int expectedProductsListSize = productRepository.findAll().size();

        mvcResult = performGetRequest("/shop");
        long actualProductsListSize = getPageOfProductsFromModel().getTotalElements();

        assertEquals(expectedProductsListSize, actualProductsListSize);
    }

    @Test
    void when_userRequestBrandInShopPage_then_returnAllProductsFromThisBrand() throws Exception {
        mvcResult = performGetRequest("/shop?brand=orly");
        List<Product> actualListOfProducts = getPageOfProductsFromModel().getContent();

        assertEquals(4, actualListOfProducts.size());
        for (Product product : actualListOfProducts) {
            assertEquals("orly", product.getBrand());
        }
    }

    @Test
    void when_userRequestShopPage_then_returnPageWith12Products() throws Exception {
        mvcResult = performGetRequest("/shop");
        Page<Product> actualPageOfProducts = getPageOfProductsFromModel();

        assertEquals(12, actualPageOfProducts.getNumberOfElements());
    }

    @Test
    @Transactional
    void given_complicatedRequest_when_requestForShopPage_then_returnProperProduct() throws Exception {
        Product expectedProduct = expectedRealProduct();

        mvcResult = performGetRequest(String.format("/shop?brand=%s&product_category=%s&product_type=%s" +
                "&product_tags=Vegan&rating_greater_than=3&rating_less_than=4&price_greater_than=23&price_less_than=25",
                expectedProduct.getBrand(), expectedProduct.getCategory(), expectedProduct.getProductType()));
        Product actualProduct = getPageOfProductsFromModel().getContent().get(0);

        assertEquals(1, getPageOfProductsFromModel().getContent().size());
        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    @Transactional
    void given_oneProductTagInARequest_when_requestShopPage_then_returnProductsThatMightGotNotOnlyTheGivenTag() throws Exception {
        ProductTag expectedTag = new ProductTag("Natural");

        mvcResult = performGetRequest("/shop?brand=zorah&product_tags=" + expectedTag.getName());
        List<Product> actualPageContent = getPageOfProductsFromModel().getContent();

        assertEquals(2, actualPageContent.size());
        assertTrue(actualPageContent.get(0).getProductTags().contains(expectedTag));
        assertTrue(actualPageContent.get(1).getProductTags().contains(expectedTag));
        assertTrue(actualPageContent.get(0).getProductTags().size() > 1);
        assertTrue(actualPageContent.get(1).getProductTags().size() > 1);
    }

    @Test
    void given_pageableParameters_when_requestShopPage_then_returnPageWithGivenParameters() throws Exception {
        mvcResult = performGetRequest("/shop?brand=nyx&page=3&size=30&sort=price,desc");
        List<Product> actualPageContent = getPageOfProductsFromModel().getContent();

        assertEquals(3, getPageOfProductsFromModel().getNumber());
        assertEquals(30, getPageOfProductsFromModel().getSize());
        for (int i = 0; i < actualPageContent.size() - 1; i++) {
            assertTrue(actualPageContent.get(i).getPrice() >= actualPageContent.get(i+1).getPrice());
        }
    }

    @Test
    void when_requestForSingleProduct_then_returnSingleProduct() throws Exception {
        Product expectedProduct = expectedRealProduct();

        mvcResult = performGetRequest("/shop/191");
        Product actualProduct = getSingleProductFromModel();

        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void given_wrongRequestParameters_when_requestingShopPage_then_returnResponseWithProductsNotFoundExceptionCode() throws Exception {
        mvcResult = performGetRequest("/shop?brand=something");
        ExceptionHandlerUtilities.ExceptionCase actualExceptionCase = getExceptionFromModel();

        assertEquals(ExceptionHandlerUtilities.ExceptionCase.PRODUCTS_NOT_FOUND_EXCEPTION, actualExceptionCase);
    }

    @Test
    void given_wrongId_when_requestingSingleProduct_then_returnResponseWithSingleProductNotFoundExceptionCode() throws Exception {
        mvcResult = performGetRequest("/shop/1231231");
        ExceptionHandlerUtilities.ExceptionCase actualExceptionCase = getExceptionFromModel();

        assertEquals(ExceptionHandlerUtilities.ExceptionCase.SINGLE_PRODUCT_NOT_FOUND_EXCEPTION, actualExceptionCase);
    }

    @Test
    void given_requestFor1stPage_when_requestForShopPageWithGlutenFreeTagParam_return_differentContentThanRequestFor5thPage() throws Exception {
        mvcResult = performGetRequest("/shop?product_tags=Gluten Free&page=0");
        Page<Product> firstPageResults = getPageOfProductsFromModel();
        mvcResult = performGetRequest("/shop?product_tags=Gluten Free&page=4");
        Page<Product> secondPageResults = getPageOfProductsFromModel();

        assertNotEquals(firstPageResults.getContent(), secondPageResults.getContent());
    }

    private MvcResult performGetRequest(String url) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(MediaType.TEXT_HTML))
                .andReturn();
    }

    private Page<Product> getPageOfProductsFromModel() {
        //noinspection unchecked
        return (Page<Product>) Objects.requireNonNull(mvcResult.getModelAndView()).getModel()
                .get(AttributeNames.PRODUCTS_LIST_ON_PAGE);
    }

    private Product getSingleProductFromModel() {
        return (Product) Objects.requireNonNull(mvcResult.getModelAndView()).getModel()
                .get(AttributeNames.SINGLE_PRODUCT);
    }

    private ExceptionHandlerUtilities.ExceptionCase getExceptionFromModel() {
        return (ExceptionHandlerUtilities.ExceptionCase) Objects.requireNonNull(mvcResult.getModelAndView())
                .getModel().get(AttributeNames.EXCEPTION);
    }

    private Product expectedRealProduct() {
        Product product = new Product();
        product.setId(191L);
        product.setBrand("zorah");
        product.setName("Zorah Liquid Liner");
        product.setPrice(24.0);
        product.setImageLink("https://d3t32hsnjxo7q6.cloudfront.net/i/" +
                "3dc157e4a7e28f90dbe7e9849ccfc87a_ra,w158,h184_pa,w158,h184.jpg");
        product.setProductLink("https://well.ca/products/zorah-liquid-liner_99339.html");
        product.setWebsiteLink("https://well.ca");
        product.setDescription("Zorah Liquid Liner gives you 12 hours of long-lasting intense color, is " +
                "transfer-free (leaves no trace on crease above the eyelid) and has Pure Light Capture® minerals " +
                "that deliver color and radiance. Silky lines and refreshingly light, Pure Argan eyeliner leaves " +
                "a weightless feel on the eyelids.What look do you go for: the natural bronzed babe, tropical " +
                "brights, or classic beauty? Try all three with this get the look with natural makeup piece found " +
                "on our blog, The Well! Directions: Lightly trace the lash line. Repeat with a second layer to " +
                "intensify. Make small dots from the outside working inward. Wait 3 seconds before opening eye so " +
                "as to avoid streaking. Repeat line if necessary.Ingredients: Aqua, Hydrolyzed Corn Starch, " +
                "Glyceryl Stearate Citrate, Argania Spinosa Kernel Oil*, Cucurbita Pepo (Pumpkin) Seed Oil*, " +
                "Glycerin,  Silica, Copernicia Cerifera (Carnauba) Wax*, Hectorite, Sodium Phytate, Sodium Citrate, " +
                "Benzyl Alcohol, Salicylic Acid, Sorbic Acid, Dehydroacetic Acid. [+/-(peut contenir/may contain) " +
                "Iron Oxides,Mica, Ultramarines, Bismuth Oxychloride.]Tested dermatologically and ophthalmologically." +
                "Certified by Ecocert Greenlife: 98% of the total ingredients are from \nnatural origin, 5% of total " +
                "ingredients are from organic farming");
        product.setRating(3.3);
        product.setCategory("liquid");
        product.setProductType("eyeliner");
        product.setProductTags(Set.of(new ProductTag("Natural"), new ProductTag("Canadian"),
                new ProductTag("Gluten Free"), new ProductTag("Organic"), new ProductTag("Vegan")));
        Color expectedColor1 = new Color();
        expectedColor1.setColorName("Brown ");
        expectedColor1.setHexValue("#74482f");
        Color expectedColor2 = new Color();
        expectedColor2.setColorName("Black ");
        expectedColor2.setHexValue("#000000");
        product.setProductColors(Set.of(expectedColor1, expectedColor2));
        product.setCreatedAt("2016-10-01T18:28:07.638Z");
        product.setUpdatedAt("2017-12-23T20:51:18.506Z");
        product.setProductApiUrl("http://makeup-api.herokuapp.com/api/v1/products/191.json");
        product.setApiFeaturedImage("//s3.amazonaws.com/donovanbailey/products/" +
                "api_featured_images/000/000/191/original/data?1514062278");
        return product;
    }
}
