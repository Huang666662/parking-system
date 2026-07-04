package com.parking.controller;

import com.parking.entity.MemberLevel;
import com.parking.service.IMemberLevelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/member-level")
public class MemberLevelController {

    @Autowired
    private IMemberLevelService memberLevelService;

    private boolean hasAccess(HttpSession session) {
        String role = (String) session.getAttribute("adminRole");
        return "super_admin".equals(role) || "system_admin".equals(role);
    }

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        List<MemberLevel> list = memberLevelService.listAll();
        model.addAttribute("levels", list);
        model.addAttribute("current", "member");
        return "member-level-list";
    }

    @PostMapping("/add")
    public String add(@RequestParam String levelName,
                      @RequestParam String levelCode,
                      @RequestParam Integer minPoints,
                      @RequestParam java.math.BigDecimal discountRate,
                      @RequestParam Integer monthlyFreeMinutes,
                      HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        MemberLevel level = new MemberLevel();
        level.setLevelName(levelName);
        level.setLevelCode(levelCode);
        level.setMinPoints(minPoints);
        level.setDiscountRate(discountRate);
        level.setMonthlyFreeMinutes(monthlyFreeMinutes);
        memberLevelService.addLevel(level);
        return "redirect:/member-level/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        memberLevelService.deleteLevel(id);
        return "redirect:/member-level/list";
    }
}
