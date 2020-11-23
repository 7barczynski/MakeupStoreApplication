package com.tbar.makeupstoreapplication.controller;

import com.tbar.makeupstoreapplication.utility.ViewNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping
    public String homePage() {
        return ViewNames.HOME;
    }
}

