package com.hyperledger.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
public class indexController {
    @RequestMapping("/")
    public String HomePage() {
        return "index";
    }

    @RequestMapping("/browser")
    public String BrowserPage() {
        return "browser";
    }

    @RequestMapping("/auditorPage")
    public String AuditorPage() {
        return "auditorPage";
    }
}
