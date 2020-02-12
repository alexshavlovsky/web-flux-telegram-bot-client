package com.ctzn.webfluxtelegrambotclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MainController {

    @GetMapping("/")
    public String index(final Model model) {
        model.addAttribute("token", "");
        return "index";
    }

    @GetMapping("/token/{token}")
    public String index(@PathVariable String token, final Model model) {
        model.addAttribute("token", token);
        return "index";
    }

}
