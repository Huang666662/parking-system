package com.parking.controller;

import com.parking.entity.BlackList;
import com.parking.service.IBlackListService;
import com.parking.util.OperationLogHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/black-list")
public class BlackListController {

    @Autowired
    private IBlackListService blackListService;

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
        List<BlackList> list = blackListService.listAll();
        model.addAttribute("blackList", list);
        model.addAttribute("current", "black");
        return "black-list-list";
    }

    @PostMapping("/add")
    public String add(@RequestParam String plateNumber,
                      @RequestParam String reason,
                      HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        BlackList bl = new BlackList();
        bl.setPlateNumber(plateNumber);
        bl.setReason(reason);
        Object adminObj = session.getAttribute("user");
        if (adminObj instanceof com.parking.entity.Admin) {
            bl.setOperatorId(((com.parking.entity.Admin) adminObj).getId());
        }
        blackListService.addBlackList(bl);
        operationLogHelper.log(session, "新增黑名单", "新增黑名单车辆：" + plateNumber + "，原因：" + reason);
        return "redirect:/black-list/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        BlackList bl = blackListService.getBlackList(id);
        if (bl != null) {
            blackListService.deleteByPlate(bl.getPlateNumber());
            operationLogHelper.log(session, "移除黑名单", "移除黑名单车辆：" + bl.getPlateNumber());
        }
        return "redirect:/black-list/list";
    }
}
