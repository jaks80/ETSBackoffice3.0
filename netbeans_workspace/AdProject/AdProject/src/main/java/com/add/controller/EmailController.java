package com.add.controller;


import com.add.model.ContactUs;
import com.add.util.EmailService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Yusuf
 */
@Controller
@RequestMapping("/emailController")
public class EmailController {

    @Resource(name = "emailService")
    private EmailService emailService;    
    
    @RequestMapping(value = "/sendWebmail", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD})
    public String sendMail(@ModelAttribute("contactUsAttribute") ContactUs contactUs, HttpServletRequest request) {

        StringBuilder sb = new StringBuilder();
        sb.append("Webmail from website...");
        sb.append("<br>");
        sb.append("Date: " + new java.util.Date());
        sb.append("-----------------------------------------");
        sb.append("<br>");
        sb.append("Name: " + contactUs.getName());
        sb.append("<br>");
        sb.append("-----------------------------------------");
        sb.append("Sender Email: " + contactUs.getEmail());
        sb.append("<br>");
        sb.append("-----------------------------------------");
        sb.append("Message: " + contactUs.getMessage());
        sb.append("<br>");        
               
        String s = "";
        if(emailService.sendEmail("Webmail", sb.toString(), null)){
         s = "emailconfirmation";
        }else{
         s = "emailfailure";
        }   
        
        return s;
    }
    
    @RequestMapping(value = "/newWebmail", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD})
    public String getNew(Model model) {

        model.addAttribute("contactUsAttribute", new ContactUs());       
        return "contact";
    }
}
