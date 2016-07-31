package com.home.services.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by vitaliy on 7/25/16.
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("message", "World");
        return "index";
    }

    @RequestMapping(value = "/hello")
    @ResponseBody
    public String hello() {
        return "Hello World!";
    }
}
