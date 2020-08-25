package com.tbar.makeupstoreapplication;

import com.tbar.makeupstoreapplication.model.Color;
import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.model.ProductTag;
import com.tbar.makeupstoreapplication.repository.ProductRepository;
import com.tbar.makeupstoreapplication.utility.AttributeNames;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class MakeupStoreApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void when_userRequestShopPage_return_allProductsFromDatabase() throws Exception {
        int expectedProductsListSize = productRepository.findAll().size();

        MvcResult mvcResult = mockMvc.perform(get("/shop")
                .contentType(MediaType.TEXT_HTML))
                .andReturn();

        //noinspection unchecked
        Page<Product> actualPageOfProducts = (Page<Product>) Objects.requireNonNull(mvcResult.getModelAndView()).getModel()
                .get(AttributeNames.PRODUCTS_LIST_ON_PAGE);
        long actualProductsListSize = actualPageOfProducts.getTotalElements();

        assertEquals(expectedProductsListSize, actualProductsListSize);
    }

    @Test
    void when_userRequestBrandInShopPage_return_allProductsFromThisBrand() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/shop?brand=orly")
                .contentType(MediaType.TEXT_HTML))
                .andReturn();

        //noinspection unchecked
        Page<Product> actualPageOfProducts = (Page<Product>) Objects.requireNonNull(mvcResult.getModelAndView()).getModel()
                .get(AttributeNames.PRODUCTS_LIST_ON_PAGE);
        List<Product> actualListOfProducts = actualPageOfProducts.getContent();

        assertEquals(4, actualListOfProducts.size());
        for (Product product : actualListOfProducts) {
            assertEquals("orly", product.getBrand());
        }
    }

    @Test
    void when_userRequestShopPage_return_pageWith12Products() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/shop")
                .contentType(MediaType.TEXT_HTML))
                .andReturn();

        //noinspection unchecked
        Page<Product> actualPageOfProducts = (Page<Product>) Objects.requireNonNull(mvcResult.getModelAndView()).getModel()
                .get(AttributeNames.PRODUCTS_LIST_ON_PAGE);

        assertEquals(12, actualPageOfProducts.getNumberOfElements());
    }

    @Test
    void when_userMakeComplicatedRequest_return_properProduct() throws Exception {
        Product expectedProduct = expectedRealProduct();

        MvcResult mvcResult = mockMvc.perform(
                get("/shop?brand=zorah&rating_less_than=4&product_tags=Vegan&product_type=eyeliner" +
                        "&product_category=liquid&price_greater_than=23&rating_greater_than=3&price_less_than=25")
                        .contentType(MediaType.TEXT_HTML))
                .andReturn();

        //noinspection unchecked
        Page<Product> actualPageOfProducts = (Page<Product>) Objects.requireNonNull(mvcResult.getModelAndView()).getModel()
                .get(AttributeNames.PRODUCTS_LIST_ON_PAGE);
        Product actualProduct = actualPageOfProducts.getContent().get(0);

        assertEquals(1, actualPageOfProducts.getContent().size());
        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void given_oneProductTagInARequest_should_returnProductsThatHaveGotNotOnlyTheGivenTag() throws Exception {
        ProductTag expectedTag = new ProductTag("Natural");

        MvcResult mvcResult = mockMvc.perform(
                get("/shop?brand=zorah&product_tags=" + expectedTag.getName())
                        .contentType(MediaType.TEXT_HTML))
                .andReturn();

        //noinspection unchecked
        Page<Product> actualPageOfProducts = (Page<Product>) Objects.requireNonNull(mvcResult.getModelAndView()).getModel()
                .get(AttributeNames.PRODUCTS_LIST_ON_PAGE);
        List<Product> actualPageContent = actualPageOfProducts.getContent();

        assertEquals(2, actualPageContent.size());
        assertTrue(actualPageContent.get(0).getProductTags().contains(expectedTag));
        assertTrue(actualPageContent.get(1).getProductTags().contains(expectedTag));
        assertTrue(actualPageContent.get(0).getProductTags().size() > 1);
        assertTrue(actualPageContent.get(1).getProductTags().size() > 1);
    }

    @Test
    void given_twoProductTagsInARequest_should_returnProductsWithAtLeastOneOfThemInTheirProductTagsSet() throws Exception {
        ProductTag expectedTag1 = new ProductTag("Vegan");
        ProductTag expectedTag2 = new ProductTag("Natural");

        MvcResult mvcResult = mockMvc.perform(
                get(String.format("/shop?brand=zorah&product_tags=%s&product_tags=%s",
                        expectedTag1.getName(), expectedTag2.getName()))
                        .contentType(MediaType.TEXT_HTML))
                .andReturn();

        //noinspection unchecked
        Page<Product> actualPageOfProducts = (Page<Product>) Objects.requireNonNull(mvcResult.getModelAndView()).getModel()
                .get(AttributeNames.PRODUCTS_LIST_ON_PAGE);
        List<Product> actualPageContent = actualPageOfProducts.getContent();
        Set<ProductTag> actualTagsForFirstProduct = actualPageContent.get(0).getProductTags();
        Set<ProductTag> actualTagsForSecondProduct = actualPageContent.get(1).getProductTags();

        assertEquals(2, actualPageContent.size());
        assertTrue(!actualTagsForFirstProduct.contains(expectedTag1) &&
                actualTagsForFirstProduct.contains(expectedTag2)); // this product has got 1 tag
        assertTrue(actualTagsForSecondProduct.contains(expectedTag1) &&
                actualTagsForSecondProduct.contains(expectedTag2)); // this product has got all 2 tags
    }

    @Disabled // TODO this test needs changes in th.xml files to eradicate an exception
    @Test
    void should_returnSingleProductWhenRequestingId() throws Exception {
        Product expectedProduct = expectedRealProduct();

        MvcResult mvcResult = mockMvc.perform(
                get("/shop/191")
                        .contentType(MediaType.TEXT_HTML))
                .andReturn();

        Product actualProduct = (Product) Objects.requireNonNull(mvcResult.getModelAndView()).getModel()
                .get(AttributeNames.SINGLE_PRODUCT);

        assertEquals(expectedProduct, actualProduct);
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
