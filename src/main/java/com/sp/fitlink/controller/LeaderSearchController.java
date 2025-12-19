package com.sp.fitlink.controller;

import java.util.List;
import java.util.Map; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sp.fitlink.service.FitLinkService;

@RestController
@RequestMapping("/api/leaders")
public class LeaderSearchController {

    @Autowired
    private FitLinkService fitLinkService;

    @GetMapping("/search")
    public List<Map<String, Object>> searchLeaders(@RequestParam("keyword") String keyword) {
        return fitLinkService.searchByName(keyword);
    }
}
