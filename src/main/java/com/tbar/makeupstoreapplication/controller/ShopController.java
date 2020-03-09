package com.tbar.makeupstoreapplication.controller;

import com.tbar.makeupstoreapplication.service.MakeupService;
import com.tbar.makeupstoreapplication.service.consumer.model.Product;
import com.tbar.makeupstoreapplication.utility.AppMappings;
import com.tbar.makeupstoreapplication.utility.AttributeNames;
import com.tbar.makeupstoreapplication.utility.ExceptionHandlerUtilities;
import com.tbar.makeupstoreapplication.utility.ViewNames;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/" + AppMappings.SHOP)
public class ShopController {

    private final MakeupService makeupService;

    @Autowired
    public ShopController(MakeupService makeupService) {
        this.makeupService = makeupService;
    }

    @GetMapping
    public String shopPage(Model model, @RequestParam(required = false) Map<String, String> allParams,
                                 @RequestParam(defaultValue = "1", name = AppMappings.QUERY_PARAM_PAGE) int page)
            throws ProductsNotFoundException {
        Page<Product> pageOfProducts = makeupService.getPaginatedProducts(allParams, page);
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
    public String handleExceptions(Model model, Exception exception) {
        addAttributesToExceptionHandlerModel(model, exception);
        return ViewNames.SHOP;
    }

    private void addAttributesToExceptionHandlerModel(Model model, Exception exception) {
        model.addAttribute(AttributeNames.CURRENT_LANGUAGE, LocaleContextHolder.getLocale());
        model.addAttribute(AttributeNames.EXCEPTION, ExceptionHandlerUtilities.chooseSpecificException(exception));
    }
}
