package com.parking.controller;

import com.parking.entity.User;
import com.parking.entity.Vehicle;
import com.parking.service.IUserService;
import com.parking.service.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IVehicleService vehicleService;

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String phone,
                           @RequestParam(required = false) String plateNumber) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);
        user.setPoints(0);
        user.setMemberLevelId(1);
        user.setStatus(1);

        int result = userService.addUser(user);
        if (result > 0 && plateNumber != null && !plateNumber.isEmpty()) {
            Vehicle vehicle = new Vehicle();
            vehicle.setUserId(user.getId());
            vehicle.setPlateNumber(plateNumber);
            vehicle.setIsDefault(1);
            vehicle.setStatus(1);
            vehicleService.addVehicle(vehicle);
        }
        if (result > 0) {
            return "redirect:/login?registered";
        }
        return "redirect:/register?error";
    }
}
