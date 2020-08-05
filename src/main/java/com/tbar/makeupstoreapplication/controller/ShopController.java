package com.tbar.makeupstoreapplication.controller;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.service.MakeupService;
import com.tbar.makeupstoreapplication.utility.AppMappings;
import com.tbar.makeupstoreapplication.utility.AttributeNames;
import com.tbar.makeupstoreapplication.utility.ExceptionHandlerUtilities;
import com.tbar.makeupstoreapplication.utility.ViewNames;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public String shopPage(Model model, @And({
            @Spec(path = "productType", spec = Equal.class),
            @Spec(path = "productCategory", spec = Equal.class),
            @Spec(path = "brand", spec = Equal.class),
            @Spec(path = "productTags", spec = In.class),
            @Spec(path = "priceGreaterThan", spec = GreaterThanOrEqual.class),
            @Spec(path = "priceLessThan", spec = LessThanOrEqual.class),
            @Spec(path = "ratingGreaterThan", spec = GreaterThanOrEqual.class),
            @Spec(path = "ratingLessThan", spec = LessThanOrEqual.class)
    }) Specification<Product> productSpecification, Pageable pageable) throws ProductsNotFoundException {
        Page<Product> pageOfProducts = makeupService.findProducts(productSpecification, pageable);
        addAttributesToShopModel(model, pageOfProducts);
        return ViewNames.SHOP;
    }

    private void addAttributesToShopModel(Model model, Page<Product> pageOfProducts) {
        model.addAttribute(AttributeNames.PRODUCTS_LIST_ON_PAGE, pageOfProducts);
        model.addAttribute(AttributeNames.PAGINATION_NUMBERS_LIST,
                makeupService.getPaginationNumbers(pageOfProducts));
        model.addAttribute(AttributeNames.CURRENT_LANGUAGE, LocaleContextHolder.getLocale());
    }

    @ExceptionHandler(Exception.class)
    String handleExceptions(Model model, Exception exception) {
        addAttributesToExceptionHandlerModel(model, exception);
        return ViewNames.SHOP;
    }

    private void addAttributesToExceptionHandlerModel(Model model, Exception exception) {
        model.addAttribute(AttributeNames.CURRENT_LANGUAGE, LocaleContextHolder.getLocale());
        model.addAttribute(AttributeNames.EXCEPTION, ExceptionHandlerUtilities.chooseSpecificException(exception));
    }
}
