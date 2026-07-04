package com.parking.controller;

import com.parking.entity.ParkingSpace;
import com.parking.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private IParkingSpaceService parkingSpaceService;

    @Autowired
    private IParkingRecordService parkingRecordService;

    @Autowired
    private IOrdersService ordersService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IFinancialRecordService financialRecordService;

    private boolean hasAccess(HttpSession session) {
        if (!"admin".equals(session.getAttribute("userType"))) {
            return false;
        }
        String role = (String) session.getAttribute("adminRole");
        return "super_admin".equals(role)
                || "system_admin".equals(role)
                || "space_admin".equals(role)
                || "finance_admin".equals(role);
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        // 统一从 parking_space 表计算，只统计启用的车位
        List<ParkingSpace> allSpaces = parkingSpaceService.listAll();
        int totalSpaces = (int) allSpaces.stream().filter(s -> Integer.valueOf(1).equals(s.getIsEnabled())).count();
        int freeSpaces = (int) allSpaces.stream().filter(s -> "free".equals(s.getStatus()) && Integer.valueOf(1).equals(s.getIsEnabled())).count();
        int currentParked = (int) allSpaces.stream().filter(s -> "occupied".equals(s.getStatus()) && Integer.valueOf(1).equals(s.getIsEnabled())).count();

        model.addAttribute("totalSpaces", totalSpaces);
        model.addAttribute("freeSpaces", freeSpaces);
        model.addAttribute("currentParked", currentParked);
        model.addAttribute("current", "stat");
        return "statistics-dashboard";
    }
}
