package com.parking.controller;

import com.parking.entity.CargoApplication;
import com.parking.service.ICargoApplicationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cargo-application")
public class CargoApplicationController {

    @Autowired
    private ICargoApplicationService cargoApplicationService;

    private boolean hasAccess(HttpSession session) {
        String role = (String) session.getAttribute("adminRole");
        return "super_admin".equals(role) || "space_admin".equals(role);
    }

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        List<CargoApplication> list = cargoApplicationService.listAll();
        model.addAttribute("list", list);
        model.addAttribute("current", "cargo");
        return "cargo-application-list";
    }

    @GetMapping("/approve/{id}")
    public String approve(@PathVariable Long id, @RequestParam String status, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        cargoApplicationService.approveApplication(id, status);
        return "redirect:/cargo-application/list";
    }
}
