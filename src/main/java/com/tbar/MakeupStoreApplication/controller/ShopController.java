package com.tbar.MakeupStoreApplication.controller;

import com.tbar.MakeupStoreApplication.service.MakeupService;
import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.AppMappings;
import com.tbar.MakeupStoreApplication.utility.AttributeNames;
import com.tbar.MakeupStoreApplication.utility.ViewNames;
import com.tbar.MakeupStoreApplication.utility.exceptions.serviceLayer.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/" + AppMappings.SHOP)
public class ShopController {

    // === fields ===
    private final MakeupService makeupService;

    // === constructors ===
    @Autowired
    public ShopController(MakeupService makeupService) {
        this.makeupService = makeupService;
    }

    @GetMapping
    public String shopPage(Model model, @RequestParam(required = false) Map<String, String> allParams,
                           @RequestParam(defaultValue = "1", name = AppMappings.QUERY_PARAM_PAGE) int page,
                           @RequestParam(defaultValue = "9") int size) {

        Page<Item> itemsPage = null;
        try {
            itemsPage = makeupService.getPaginatedProducts(allParams, page, size);
            model.addAttribute(AttributeNames.PAGINATION_NUMBERS_LIST, makeupService.getPaginationNumbers(itemsPage));
        } catch (ProductNotFoundException e) {
            log.debug("ProductNotFoundException." + e.getMessage());
        }

        model.addAttribute(AttributeNames.ITEMS_PAGE_LIST, itemsPage);
        model.addAttribute(AttributeNames.CURRENT_LANGUAGE, LocaleContextHolder.getLocale());

        return ViewNames.SHOP;
    }

    @GetMapping("/{id}")
    public String productSinglePage(Model model, @PathVariable("id") long id) {

        Item item = makeupService.getProduct(id);
        model.addAttribute(AttributeNames.ITEM_SINGLE, item);
        model.addAttribute(AttributeNames.CURRENT_LANGUAGE, LocaleContextHolder.getLocale());

        return ViewNames.PRODUCT_SINGLE;
    }
}
