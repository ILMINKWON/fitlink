package com.sp.fitlink.controller;

import com.sp.fitlink.service.FitLinkService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/fitLink/pay")
@RequiredArgsConstructor
public class KaKaoPayController {

    private final FitLinkService fitLinkService;

    @GetMapping("/success")
    public String success(@RequestParam("pg_token") String pgToken, HttpSession session) {

        String tid = (String) session.getAttribute("tid");

        if (tid != null) {
            fitLinkService.kakaoPayApprove(tid, pgToken, session);
            session.removeAttribute("tid");
            session.removeAttribute("partner_user_id");
        }

        return "redirect:/fitLink/fitLinkUser?paySuccess=true";
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "redirect:/fitLink/fitLinkUser?payCancel=true";
    }

    @GetMapping("/fail")
    public String fail() {
        return "redirect:/fitLink/fitLinkUser?payFail=true";
    }
}
