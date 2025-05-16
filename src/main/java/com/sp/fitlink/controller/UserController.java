package com.sp.fitlink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("fitLink")
public class UserController {

    @RequestMapping("/mainPage")
    public String mainPage(){

        return "/user/mainPage";
        
    }
    
}
