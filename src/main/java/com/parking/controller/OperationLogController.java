package com.parking.controller;

import com.parking.entity.OperationLog;
import com.parking.service.IOperationLogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/operation-log")
public class OperationLogController {

    @Autowired
    private IOperationLogService operationLogService;

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        if (!"super_admin".equals(session.getAttribute("adminRole"))) {
            return "redirect:/index";
        }
        List<OperationLog> list = operationLogService.listAll();
        model.addAttribute("logs", list);
        model.addAttribute("current", "log");
        return "operation-log-list";
    }
}
