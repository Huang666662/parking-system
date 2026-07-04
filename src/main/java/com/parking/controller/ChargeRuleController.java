package com.parking.controller;

import com.parking.entity.ChargeRule;
import com.parking.service.IChargeRuleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/charge-rule")
public class ChargeRuleController {

    @Autowired
    private IChargeRuleService chargeRuleService;

    private boolean hasAccess(HttpSession session) {
        String role = (String) session.getAttribute("adminRole");
        return "super_admin".equals(role) || "system_admin".equals(role);
    }

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        List<ChargeRule> list = chargeRuleService.listActive();
        model.addAttribute("rules", list);
        model.addAttribute("current", "charge");
        return "charge-rule-list";
    }

    @PostMapping("/add")
    public String add(@RequestParam String ruleName,
                      @RequestParam String ruleType,
                      @RequestParam java.math.BigDecimal unitPrice,
                      @RequestParam Integer freeMinutes,
                      @RequestParam java.math.BigDecimal capPrice,
                      HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        ChargeRule rule = new ChargeRule();
        rule.setRuleName(ruleName);
        rule.setRuleType(ruleType);
        rule.setUnitPrice(unitPrice);
        rule.setFreeMinutes(freeMinutes);
        rule.setCapPrice(capPrice);
        rule.setIsActive(1);
        chargeRuleService.addRule(rule);
        return "redirect:/charge-rule/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        chargeRuleService.deleteRule(id);
        return "redirect:/charge-rule/list";
    }
}
