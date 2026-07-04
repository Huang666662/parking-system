package com.parking.controller;

import com.parking.entity.ParkingArea;
import com.parking.service.IParkingAreaService;
import com.parking.util.OperationLogHelper;
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

    @Autowired
    private OperationLogHelper operationLogHelper;

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
        operationLogHelper.log(session, "新增停车区域", "新增停车区域：" + areaName);
        return "redirect:/parking-area/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        parkingAreaService.deleteArea(id);
        operationLogHelper.log(session, "删除停车区域", "删除停车区域ID：" + id);
        return "redirect:/parking-area/list";
    }
}
