package com.tbar.makeupstoreapplication.controller;

import com.tbar.makeupstoreapplication.utility.AppMappings;
import com.tbar.makeupstoreapplication.utility.AttributeNames;
import com.tbar.makeupstoreapplication.utility.ViewNames;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping
    public String homePage(Model model) {
        addCurrentLanguageToModel(model);
        return ViewNames.HOME;
    }

    @GetMapping("/" + AppMappings.CONTACT)
    public String contactPage(Model model) {
        addCurrentLanguageToModel(model);
        return ViewNames.CONTACT;
    }

    private void addCurrentLanguageToModel(Model model) {
        model.addAttribute(AttributeNames.CURRENT_LANGUAGE, LocaleContextHolder.getLocale());
    }
}

