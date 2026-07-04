package com.parking.controller;

import com.parking.entity.MonthlyVehicle;
import com.parking.entity.SubscriptionPackage;
import com.parking.service.IMonthlyVehicleService;
import com.parking.service.ISubscriptionPackageService;
import com.parking.util.OperationLogHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/monthly-vehicle")
public class MonthlyVehicleController {

    @Autowired
    private IMonthlyVehicleService monthlyVehicleService;

    @Autowired
    private ISubscriptionPackageService subscriptionPackageService;

    @Autowired
    private OperationLogHelper operationLogHelper;

    private boolean hasAccess(HttpSession session) {
        String role = (String) session.getAttribute("adminRole");
        return "super_admin".equals(role) || "system_admin".equals(role);
    }

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        List<MonthlyVehicle> list = monthlyVehicleService.listExpired();
        List<SubscriptionPackage> packages = subscriptionPackageService.listActive();
        model.addAttribute("monthlyVehicles", list);
        model.addAttribute("packages", packages);
        model.addAttribute("current", "monthly");
        return "monthly-vehicle-list";
    }

    @PostMapping("/add")
    public String add(@RequestParam Long userId,
                      @RequestParam String plateNumber,
                      @RequestParam Integer packageId,
                      HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        SubscriptionPackage pkg = subscriptionPackageService.getPackage(packageId);
        MonthlyVehicle mv = new MonthlyVehicle();
        mv.setUserId(userId);
        mv.setPlateNumber(plateNumber);
        mv.setPackageId(packageId);
        mv.setStartDate(LocalDate.now());
        if (pkg != null) {
            mv.setEndDate(LocalDate.now().plusDays(pkg.getDurationDays()));
        }
        mv.setStatus("active");
        monthlyVehicleService.addVehicle(mv);
        operationLogHelper.log(session, "新增月租车辆", "新增月租车辆：" + plateNumber);
        return "redirect:/monthly-vehicle/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        monthlyVehicleService.updateStatus(id, "expired");
        operationLogHelper.log(session, "注销月租车辆", "注销月租车辆ID：" + id);
        return "redirect:/monthly-vehicle/list";
    }
}
