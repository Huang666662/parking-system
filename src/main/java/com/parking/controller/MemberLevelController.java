package com.parking.controller;

import com.parking.entity.MemberLevel;
import com.parking.service.IMemberLevelService;
import com.parking.util.OperationLogHelper;
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
        operationLogHelper.log(session, "新增会员等级", "新增会员等级：" + levelName);
        return "redirect:/member-level/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        memberLevelService.deleteLevel(id);
        operationLogHelper.log(session, "删除会员等级", "删除会员等级ID：" + id);
        return "redirect:/member-level/list";
    }
}
