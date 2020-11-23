package com.tbar.makeupstoreapplication.controller;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.service.MakeupService;
import com.tbar.makeupstoreapplication.utility.AppMappings;
import com.tbar.makeupstoreapplication.utility.AttributeNames;
import com.tbar.makeupstoreapplication.utility.ExceptionHandlerUtilities;
import com.tbar.makeupstoreapplication.utility.ViewNames;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.EqualIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/" + AppMappings.SHOP)
@RequiredArgsConstructor
public class ShopController {

    private final MakeupService makeupService;

    @GetMapping
    public String shopPage(Model model,
        @Join(path = "productTags", alias = "pt")
        @And({
            @Spec(path = "productType", params = "product_type", spec = EqualIgnoreCase.class),
            @Spec(path = "category", params = "product_category", spec = EqualIgnoreCase.class),
            @Spec(path = "brand", spec = EqualIgnoreCase.class),
            @Spec(path = "pt.name", params = "product_tags", spec = EqualIgnoreCase.class),
            @Spec(path = "price", params = "price_greater_than", spec = GreaterThanOrEqual.class),
            @Spec(path = "price", params = "price_less_than", spec = LessThanOrEqual.class),
            @Spec(path = "rating", params = "rating_greater_than", spec = GreaterThanOrEqual.class),
            @Spec(path = "rating", params = "rating_less_than", spec = LessThanOrEqual.class)
    }) Specification<Product> productSpecification,
       @PageableDefault(size = 12, sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable)
            throws ProductsNotFoundException {
        Page<Product> pageOfProducts = makeupService.findProducts(productSpecification, pageable);
        addAttributesToModel(model, pageOfProducts);
        return ViewNames.SHOP;
    }

    private void addAttributesToModel(Model model, Page<Product> pageOfProducts) {
        model.addAttribute(AttributeNames.PRODUCTS_LIST_ON_PAGE, pageOfProducts);
        model.addAttribute(AttributeNames.PAGINATION_NUMBERS_LIST,
                makeupService.getPaginationNumbers(pageOfProducts));
    }

    @ExceptionHandler(Exception.class)
    String handleExceptions(Model model, Exception exception) {
        addAttributesToModel(model, exception);
        return ViewNames.SHOP;
    }

    private void addAttributesToModel(Model model, Exception exception) {
        model.addAttribute(AttributeNames.EXCEPTION, ExceptionHandlerUtilities.chooseSpecificException(exception));
    }
}
