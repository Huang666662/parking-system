package com.parking.controller;

import com.parking.entity.Vehicle;
import com.parking.service.IVehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    private IVehicleService vehicleService;

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        Object userObj = session.getAttribute("user");
        if (userObj == null || !"user".equals(session.getAttribute("userType"))) {
            return "redirect:/login";
        }
        Long userId = ((com.parking.entity.User) userObj).getId();
        List<Vehicle> list = vehicleService.listByUserId(userId);
        model.addAttribute("vehicles", list);
        model.addAttribute("current", "vehicle");
        return "vehicle-list";
    }

    @PostMapping("/add")
    public String add(@RequestParam String plateNumber, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null || !"user".equals(session.getAttribute("userType"))) {
            return "redirect:/login";
        }
        Long userId = ((com.parking.entity.User) userObj).getId();
        Vehicle vehicle = new Vehicle();
        vehicle.setUserId(userId);
        vehicle.setPlateNumber(plateNumber);
        vehicle.setIsDefault(0);
        vehicle.setStatus(1);
        vehicleService.addVehicle(vehicle);
        return "redirect:/vehicle/list";
    }

    @GetMapping("/set-default/{id}")
    public String setDefault(@PathVariable Long id, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null || !"user".equals(session.getAttribute("userType"))) {
            return "redirect:/login";
        }
        Long userId = ((com.parking.entity.User) userObj).getId();
        List<Vehicle> vehicles = vehicleService.listByUserId(userId);
        for (Vehicle v : vehicles) {
            if (v.getId().equals(id)) {
                v.setIsDefault(1);
            } else {
                v.setIsDefault(0);
            }
            vehicleService.updateVehicle(v);
        }
        return "redirect:/vehicle/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null || !"user".equals(session.getAttribute("userType"))) {
            return "redirect:/login";
        }
        vehicleService.deleteVehicle(id);
        return "redirect:/vehicle/list";
    }
}
