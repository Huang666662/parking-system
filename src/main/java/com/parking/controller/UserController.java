package com.parking.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.parking.entity.User;
import com.parking.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    private boolean isAdmin(HttpSession session) {
        return "admin".equals(session.getAttribute("userType"));
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(required = false) String username,
                       Model model,
                       HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/index";
        }
        PageHelper.startPage(page, size);
        List<User> list = userService.listUsers(username);
        PageInfo<User> pageInfo = new PageInfo<>(list);
        model.addAttribute("page", pageInfo);
        model.addAttribute("username", username);
        return "user-list";
    }

    @GetMapping("/audit/{id}")
    public String audit(@PathVariable Long id, @RequestParam Integer status, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/index";
        }
        User user = userService.getUser(id);
        if (user != null) {
            user.setStatus(status);
            userService.updateUser(user);
        }
        return "redirect:/user/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/index";
        }
        userService.deleteUser(id);
        return "redirect:/user/list";
    }
}