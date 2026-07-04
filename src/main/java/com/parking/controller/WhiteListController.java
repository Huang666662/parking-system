package com.parking.controller;

import com.parking.entity.WhiteList;
import com.parking.service.IWhiteListService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/white-list")
public class WhiteListController {

    @Autowired
    private IWhiteListService whiteListService;

    private boolean hasAccess(HttpSession session) {
        String role = (String) session.getAttribute("adminRole");
        return "super_admin".equals(role) || "space_admin".equals(role);
    }

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        List<WhiteList> list = whiteListService.listAll();
        model.addAttribute("whiteList", list);
        model.addAttribute("current", "white");
        return "white-list-list";
    }

    @PostMapping("/add")
    public String add(@RequestParam String plateNumber,
                      @RequestParam(required = false) java.time.LocalDate expireDate,
                      HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        WhiteList wl = new WhiteList();
        wl.setPlateNumber(plateNumber);
        wl.setExpireDate(expireDate);
        wl.setStatus("active");
        whiteListService.addWhiteList(wl);
        return "redirect:/white-list/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        WhiteList wl = whiteListService.getWhiteList(id);
        if (wl != null) {
            whiteListService.updateStatusByPlate(wl.getPlateNumber(), "inactive");
        }
        return "redirect:/white-list/list";
    }
}
