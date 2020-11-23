package com.tbar.makeupstoreapplication.controller;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.service.MakeupService;
import com.tbar.makeupstoreapplication.utility.AttributeNames;
import com.tbar.makeupstoreapplication.utility.ExceptionHandlerUtilities;
import com.tbar.makeupstoreapplication.utility.ViewNames;
import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(ViewNames.SHOP + "/{id}")
public class SingleProductController {

    private final MakeupService makeupService;

    @GetMapping
    public String singleProductPage(Model model, @PathVariable("id") long id) throws SingleProductNotFoundException {
        Product product = makeupService.findProduct(id);
        addAttributesToModel(model, product);
        return ViewNames.SINGLE_PRODUCT;
    }

    private void addAttributesToModel(Model model, Product product) {
        model.addAttribute(AttributeNames.SINGLE_PRODUCT, product);
    }

    @ExceptionHandler(Exception.class)
    public String handleExceptions(Model model, Exception exception) {
        addAttributesToModel(model, exception);
        return ViewNames.SINGLE_PRODUCT;
    }

    private void addAttributesToModel(Model model, Exception exception) {
        model.addAttribute(AttributeNames.EXCEPTION, ExceptionHandlerUtilities.chooseSpecificException(exception));
    }
}
