package com.sp.fitlink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/chat")
    public String chatPage() {
        return "chat";  // templates/chat.html을 찾아감

    }
}
