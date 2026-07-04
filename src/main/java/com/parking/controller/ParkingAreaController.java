package com.parking.controller;

import com.parking.entity.ParkingArea;
import com.parking.service.IParkingAreaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/parking-area")
public class ParkingAreaController {

    @Autowired
    private IParkingAreaService parkingAreaService;

    private boolean hasAccess(HttpSession session) {
        String role = (String) session.getAttribute("adminRole");
        return "super_admin".equals(role) || "space_admin".equals(role);
    }

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        List<ParkingArea> list = parkingAreaService.listAll();
        model.addAttribute("areas", list);
        model.addAttribute("current", "area");
        return "parking-area-list";
    }

    @PostMapping("/add")
    public String add(@RequestParam String areaName,
                      @RequestParam Integer floor,
                      @RequestParam Integer totalSpaces,
                      @RequestParam(required = false) String description,
                      HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        ParkingArea area = new ParkingArea();
        area.setAreaName(areaName);
        area.setFloor(floor);
        area.setTotalSpaces(totalSpaces);
        area.setDescription(description);
        parkingAreaService.addArea(area);
        return "redirect:/parking-area/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        parkingAreaService.deleteArea(id);
        return "redirect:/parking-area/list";
    }
}
