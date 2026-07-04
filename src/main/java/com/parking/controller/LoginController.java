package com.parking.controller;

import com.parking.entity.User;
import com.parking.service.IAdminService;
import com.parking.service.IUserService;
import com.parking.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IAdminService adminService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    private String mapRole(String dbRole) {
        return switch (dbRole) {
            case "超级管理员" -> "super_admin";
            case "系统管理员" -> "system_admin";
            case "车位管理员" -> "space_admin";
            case "财务管理员" -> "finance_admin";
            case "收费员" -> "cashier";
            case "客服人员" -> "customer_service";
            default -> dbRole;
        };
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam String userType,
                        HttpSession session) {
        if ("admin".equals(userType)) {
            com.parking.entity.Admin admin = adminService.login(username, MD5Util.encrypt(password));
            if (admin == null) {
                admin = adminService.login(username, password);
            }
            if (admin != null) {
                session.setAttribute("user", admin);
                session.setAttribute("userType", "admin");
                session.setAttribute("adminRole", mapRole(admin.getRole()));
                return "redirect:/index";
            }
        } else {
            User user = userService.login(username, MD5Util.encrypt(password));
            if (user == null) {
                user = userService.login(username, password);
            }
            if (user != null) {
                session.setAttribute("user", user);
                session.setAttribute("userType", "user");
                return "redirect:/index";
            }
        }
        return "redirect:/login?error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
