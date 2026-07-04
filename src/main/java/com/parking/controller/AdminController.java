package com.parking.controller;

import com.parking.entity.Admin;
import com.parking.service.IAdminService;
import com.parking.util.MD5Util;
import com.parking.util.OperationLogHelper;
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

    @Autowired
    private OperationLogHelper operationLogHelper;

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
        operationLogHelper.log(session, "新增管理员", "新增管理员：" + username + "，角色：" + role);
        return "redirect:/admin/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!isSuperAdmin(session)) {
            return "redirect:/index";
        }
        adminService.deleteAdmin(id);
        operationLogHelper.log(session, "删除管理员", "删除管理员ID：" + id);
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
            operationLogHelper.log(session, "重置管理员密码", "重置管理员密码：" + admin.getUsername());
        }
        return "redirect:/admin/list";
    }
}
