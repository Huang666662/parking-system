package com.parking.controller;

import com.parking.entity.User;
import com.parking.service.IUserService;
import com.parking.util.MD5Util;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

    @Autowired
    private IUserService userService;

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        if (!"user".equals(session.getAttribute("userType"))) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("current", "profile");
        return "profile";
    }

    @PostMapping("/profile/update")
    public String update(@RequestParam String phone,
                         @RequestParam(required = false) String password,
                         HttpSession session) {
        if (!"user".equals(session.getAttribute("userType"))) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        user.setPhone(phone);
        if (password != null && !password.isEmpty()) {
            user.setPassword(MD5Util.encrypt(password));
        }
        userService.updateUser(user);
        session.setAttribute("user", user);
        return "redirect:/profile?success";
    }
}
