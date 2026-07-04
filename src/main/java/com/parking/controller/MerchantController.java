package com.parking.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.parking.entity.Merchant;
import com.parking.service.IMerchantService;
import com.parking.util.OperationLogHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/merchant")
public class MerchantController {

    @Autowired
    private IMerchantService merchantService;

    @Autowired
    private OperationLogHelper operationLogHelper;

    private boolean isAdmin(HttpSession session) {
        return "admin".equals(session.getAttribute("userType"));
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       Model model,
                       HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/index";
        }
        PageHelper.startPage(page, size);
        List<Merchant> list = merchantService.listAll();
        PageInfo<Merchant> pageInfo = new PageInfo<>(list);
        model.addAttribute("page", pageInfo);
        return "merchant-list";
    }

    @GetMapping("/add")
    public String addPage(HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/index";
        }
        return "merchant-add";
    }

    @PostMapping("/add")
    public String add(Merchant merchant, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/index";
        }
        merchantService.addMerchant(merchant);
        operationLogHelper.log(session, "新增商户", "新增商户：" + merchant.getMerchantName());
        return "redirect:/merchant/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/index";
        }
        merchantService.deleteMerchant(id);
        operationLogHelper.log(session, "删除商户", "删除商户ID：" + id);
        return "redirect:/merchant/list";
    }
}