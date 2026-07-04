package com.parking.controller;

import com.parking.entity.SystemConfig;
import com.parking.service.ISystemConfigService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/system-config")
public class SystemConfigController {

    @Autowired
    private ISystemConfigService systemConfigService;

    private boolean hasAccess(HttpSession session) {
        String role = (String) session.getAttribute("adminRole");
        return "super_admin".equals(role) || "system_admin".equals(role);
    }

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        List<SystemConfig> list = systemConfigService.listAll();
        model.addAttribute("configs", list);
        model.addAttribute("current", "config");
        return "system-config-list";
    }

    @PostMapping("/add")
    public String add(@RequestParam String configKey,
                      @RequestParam String configValue,
                      @RequestParam(required = false) String description,
                      HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        SystemConfig config = new SystemConfig();
        config.setConfigKey(configKey);
        config.setConfigValue(configValue);
        config.setDescription(description);
        systemConfigService.addConfig(config);
        return "redirect:/system-config/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        systemConfigService.deleteConfig(id);
        return "redirect:/system-config/list";
    }
}
