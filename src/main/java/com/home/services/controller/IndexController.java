package com.home.services.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;


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
    @PreAuthorize("#oauth2.hasScope('read')")
    public String hello() {
        return "Hello World!";
    }

    @RequestMapping(value = "/user")
    @ResponseBody
    public String user(Principal principal) {
        return "Secured Hello " + principal.getName();
    }
}
