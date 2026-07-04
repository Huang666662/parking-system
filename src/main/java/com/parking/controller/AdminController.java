package com.parking.controller;

import com.parking.entity.Admin;
import com.parking.service.IAdminService;
import com.parking.util.MD5Util;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    private boolean isSuperAdmin(HttpSession session) {
        return "super_admin".equals(session.getAttribute("adminRole"));
    }

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        if (!isSuperAdmin(session)) {
            return "redirect:/index";
        }
        List<Admin> list = adminService.listAdmins(null);
        model.addAttribute("admins", list);
        model.addAttribute("current", "admin");
        return "admin-list";
    }

    @GetMapping("/add")
    public String addPage(HttpSession session) {
        if (!isSuperAdmin(session)) {
            return "redirect:/index";
        }
        return "admin-add";
    }

    @PostMapping("/add")
    public String add(@RequestParam String username,
                      @RequestParam String password,
                      @RequestParam String role,
                      HttpSession session) {
        if (!isSuperAdmin(session)) {
            return "redirect:/index";
        }
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        admin.setRole(role);
        admin.setStatus(1);
        adminService.addAdmin(admin);
        return "redirect:/admin/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!isSuperAdmin(session)) {
            return "redirect:/index";
        }
        adminService.deleteAdmin(id);
        return "redirect:/admin/list";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam Long id,
                                @RequestParam String newPassword,
                                HttpSession session) {
        if (!isSuperAdmin(session)) {
            return "redirect:/index";
        }
        Admin admin = adminService.getAdmin(id);
        if (admin != null) {
            admin.setPassword(MD5Util.encrypt(newPassword));
            adminService.updateAdmin(admin);
        }
        return "redirect:/admin/list";
    }
}
