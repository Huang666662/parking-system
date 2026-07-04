package com.parking.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.parking.entity.Orders;
import com.parking.entity.User;
import com.parking.service.IOrdersService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private IOrdersService ordersService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(required = false) String plateNumber,
                       Model model, HttpSession session) {
        String userType = (String) session.getAttribute("userType");
        Long userId = null;
        if ("user".equals(userType)) {
            User user = (User) session.getAttribute("user");
            if (user != null) userId = user.getId();
        }
        PageHelper.startPage(page, size);
        List<Orders> list = ordersService.listOrders(plateNumber);
        if ("user".equals(userType) && userId != null) {
            final Long currentUserId = userId;
            list.removeIf(o -> !currentUserId.equals(o.getUserId()));
        }
        PageInfo<Orders> pageInfo = new PageInfo<>(list);
        model.addAttribute("page", pageInfo);
        model.addAttribute("plateNumber", plateNumber);
        model.addAttribute("current", "order");
        return "order-list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Orders order = ordersService.getOrder(id);
        model.addAttribute("order", order);
        return "order-detail";
    }
}
