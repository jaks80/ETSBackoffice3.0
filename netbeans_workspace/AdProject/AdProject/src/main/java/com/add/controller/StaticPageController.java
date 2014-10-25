package com.add.controller;

import com.add.model.page.FullSearch;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Yusuf
 */
@Controller
@RequestMapping("/static")
public class StaticPageController {
    
    @RequestMapping(value = "/index")
    public String getIndex(Model model) {
        model.addAttribute("searchAttribute", new FullSearch());
        return "/index";
    }
    
    @RequestMapping(value = "/contact")
    public String getContact(Model model) {
        model.addAttribute("searchAttribute", new FullSearch());
        return "/contact";
    }

    @RequestMapping(value = "/privacy")
    public String getPrivacy(Model model) {
        model.addAttribute("searchAttribute", new FullSearch());
        return "/privacy";
    }    
}
