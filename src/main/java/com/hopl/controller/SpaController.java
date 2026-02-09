package com.hopl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    /**
     * Forwards all non-API, non-static routes to the React SPA index.html.
     */
    @GetMapping(value = {"/", "/login", "/register", "/dashboard", "/pricing",
            "/scan/**", "/documents/**", "/settings", "/about", "/checkout/**"})
    public String forward() {
        return "forward:/index.html";
    }
}
