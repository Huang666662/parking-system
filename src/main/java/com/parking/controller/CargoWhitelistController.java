package com.parking.controller;

import com.parking.entity.CargoWhitelist;
import com.parking.service.ICargoWhitelistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cargo-whitelist")
public class CargoWhitelistController {

    @Autowired
    private ICargoWhitelistService cargoWhitelistService;

    private boolean hasAccess(HttpSession session) {
        String role = (String) session.getAttribute("adminRole");
        return "super_admin".equals(role) || "space_admin".equals(role);
    }

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        List<CargoWhitelist> list = cargoWhitelistService.listAll();
        model.addAttribute("list", list);
        model.addAttribute("current", "cargo");
        return "cargo-whitelist-list";
    }

    @PostMapping("/add")
    public String add(@RequestParam String plateNumber,
                      @RequestParam Long merchantId,
                      @RequestParam String driverName,
                      @RequestParam String driverPhone,
                      @RequestParam String allowStartTime,
                      @RequestParam String allowEndTime,
                      @RequestParam String effectiveStartDate,
                      @RequestParam String effectiveEndDate,
                      HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        CargoWhitelist w = new CargoWhitelist();
        w.setPlateNumber(plateNumber);
        w.setMerchantId(merchantId);
        w.setDriverName(driverName);
        w.setDriverPhone(driverPhone);
        w.setAllowStartTime(java.time.LocalTime.parse(allowStartTime));
        w.setAllowEndTime(java.time.LocalTime.parse(allowEndTime));
        w.setEffectiveStartDate(java.time.LocalDate.parse(effectiveStartDate));
        w.setEffectiveEndDate(java.time.LocalDate.parse(effectiveEndDate));
        w.setStatus("active");
        cargoWhitelistService.addWhitelist(w);
        return "redirect:/cargo-whitelist/list";
    }

    @GetMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id, @RequestParam String status, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        cargoWhitelistService.updateStatus(id, status);
        return "redirect:/cargo-whitelist/list";
    }
}
