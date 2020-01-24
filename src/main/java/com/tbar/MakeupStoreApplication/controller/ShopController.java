package com.tbar.MakeupStoreApplication.controller;

import com.tbar.MakeupStoreApplication.service.MakeupService;
import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.AppMappings;
import com.tbar.MakeupStoreApplication.utility.AttributeNames;
import com.tbar.MakeupStoreApplication.utility.ViewNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
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
    public String shopPage(Model model,
                           @RequestParam(defaultValue = "1", name = AppMappings.QUERY_PARAM_PAGE) int page,
                           @RequestParam(defaultValue = "9") int size) {
        Page<Item> itemsPage = makeupService.getPaginatedProducts(new HashMap(Map.of(
                "product_type", "foundation")), page, size);

        model.addAttribute(AttributeNames.ITEMS_PAGE_LIST, itemsPage);
        model.addAttribute(AttributeNames.PAGINATION_NUMBERS_LIST, makeupService.getPaginationNumbers(itemsPage));
        model.addAttribute(AttributeNames.CURRENT_LANGUAGE, LocaleContextHolder.getLocale());

        return ViewNames.SHOP;
    }
}
