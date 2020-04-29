package com.tbar.makeupstoreapplication.controller;

import com.tbar.makeupstoreapplication.service.MakeupService;
import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.AttributeNames;
import com.tbar.makeupstoreapplication.utility.ExceptionHandlerUtilities;
import com.tbar.makeupstoreapplication.utility.ViewNames;
import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping(ViewNames.SHOP + "/{id}")
public class SingleProductController {

    private final MakeupService makeupService;

    @Autowired
    public SingleProductController(MakeupService makeupService) {
        this.makeupService = makeupService;
    }

    @GetMapping
    public String singleProductPage(Model model, @PathVariable("id") long id) throws SingleProductNotFoundException {
        Product product = makeupService.getProduct(id);
        addAttributesToSingleProductModel(model, product);
        return ViewNames.SINGLE_PRODUCT;
    }

    private void addAttributesToSingleProductModel(Model model, Product product) {
        model.addAttribute(AttributeNames.SINGLE_PRODUCT, product);
        model.addAttribute(AttributeNames.CURRENT_LANGUAGE, LocaleContextHolder.getLocale());
    }

    @ExceptionHandler(Exception.class)
    public String handleExceptions(Model model, Exception exception) {
        addAttributesToExceptionHandlerModel(model, exception);
        return ViewNames.SINGLE_PRODUCT;
    }

    private void addAttributesToExceptionHandlerModel(Model model, Exception exception) {
        model.addAttribute(AttributeNames.CURRENT_LANGUAGE, LocaleContextHolder.getLocale());
        model.addAttribute(AttributeNames.EXCEPTION, ExceptionHandlerUtilities.chooseSpecificException(exception));
    }
}
