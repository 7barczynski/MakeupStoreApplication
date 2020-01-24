package com.tbar.MakeupStoreApplication.controller;

import com.tbar.MakeupStoreApplication.service.MakeupService;
import com.tbar.MakeupStoreApplication.utility.AppMappings;
import com.tbar.MakeupStoreApplication.utility.AttributeNames;
import com.tbar.MakeupStoreApplication.utility.ViewNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

    // === fields ===
    private final MakeupService makeupService;

    // === constructors ===
    @Autowired
    public HomeController(MakeupService makeupService) {
        this.makeupService = makeupService;
    }

    @GetMapping
    public String homePage(Model model) {
        model.addAttribute(AttributeNames.CURRENT_LANGUAGE, LocaleContextHolder.getLocale());
        return ViewNames.HOME;
    }

    @GetMapping("/" + AppMappings.ABOUT)
    public String aboutPage() {
        return ViewNames.ABOUT;
    }
}

