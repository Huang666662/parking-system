package com.parking.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.parking.entity.*;
import com.parking.mapper.UserMapper;
import com.parking.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/order")
public class OrdersController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private IOrdersService ordersService;

    @Autowired
    private IPaymentRecordService paymentRecordService;

    @Autowired
    private IFinancialRecordService financialRecordService;

    @Autowired
    private IParkingRecordService parkingRecordService;

    @Autowired
    private IParkingSpaceService parkingSpaceService;

    @Autowired
    private IUserService userService;

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
        // 预格式化日期
        List<Map<String, Object>> orderList = new ArrayList<>();
        for (Orders o : list) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", o.getId());
            entry.put("orderNo", o.getOrderNo());
            entry.put("plateNumber", o.getPlateNumber());
            entry.put("totalAmount", o.getTotalAmount());
            entry.put("paidAmount", o.getPaidAmount());
            entry.put("status", o.getStatus());
            entry.put("statusLabel", getStatusLabel(o.getStatus()));
            entry.put("statusClass", getStatusClass(o.getStatus()));
            entry.put("createTimeStr", o.getCreateTime() != null ? o.getCreateTime().format(FMT) : "");
            orderList.add(entry);
        }
        PageInfo<Orders> pageInfo = new PageInfo<>(list);
        model.addAttribute("page", pageInfo);
        model.addAttribute("orderList", orderList);
        model.addAttribute("plateNumber", plateNumber);
        model.addAttribute("current", "order");
        return "order-list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Orders order = ordersService.getOrder(id);
        model.addAttribute("order", order);
        model.addAttribute("createTimeStr", order.getCreateTime() != null ? order.getCreateTime().format(FMT) : "");
        return "order-detail";
    }

    @GetMapping("/pay/{id}")
    public String payPage(@PathVariable Long id, Model model) {
        Orders order = ordersService.getOrder(id);
        if (order == null) {
            return "redirect:/order/list";
        }
        model.addAttribute("order", order);
        model.addAttribute("createTimeStr", order.getCreateTime() != null ? order.getCreateTime().format(FMT) : "");
        return "order-pay";
    }

    @PostMapping("/pay/{id}")
    public String doPay(@PathVariable Long id,
                        @RequestParam String paymentMethod,
                        HttpSession session) {
        Orders order = ordersService.getOrder(id);
        if (order == null || !"unpaid".equals(order.getStatus())) {
            return "redirect:/order/list";
        }

        // 更新订单
        order.setPaidAmount(order.getTotalAmount());
        order.setStatus("paid");
        ordersService.updateOrder(order);

        // 创建支付记录
        PaymentRecord payment = new PaymentRecord();
        payment.setPaymentNo("PAY" + System.currentTimeMillis());
        payment.setRecordId(null);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setDiscountPoints(0);
        payment.setStatus("success");
        paymentRecordService.addPayment(payment);

        // 创建财务记录
        FinancialRecord financial = new FinancialRecord();
        financial.setOrderId(order.getId());
        financial.setAmount(order.getTotalAmount());
        financial.setPaymentMethod(paymentMethod);
        financial.setRecordType("income");
        financialRecordService.addFinancialRecord(financial);

        // 积分奖励
        User user = (User) session.getAttribute("user");
        if (user != null) {
            int earnedPoints = order.getTotalAmount().intValue();
            if (earnedPoints > 0) {
                User dbUser = userService.getUser(user.getId());
                dbUser.setPoints((dbUser.getPoints() != null ? dbUser.getPoints() : 0) + earnedPoints);
                userService.updateUser(dbUser);
                session.setAttribute("user", dbUser);
            }
        }

        // 更新关联的停车记录为已离场
        try {
            List<ParkingRecord> records = parkingRecordService.listRecords(order.getPlateNumber());
            if (records != null) {
                for (ParkingRecord pr : records) {
                    if ("parking".equals(pr.getStatus())) {
                        pr.setExitTime(LocalDateTime.now());
                        pr.setDurationMinutes((int) java.time.Duration.between(pr.getEnterTime(), LocalDateTime.now()).toMinutes());
                        pr.setStatus("completed");
                        pr.setPaymentStatus("paid");
                        parkingRecordService.updateRecord(pr);
                        // 释放车位
                        if (pr.getSpaceId() != null) {
                            try {
                                ParkingSpace space = parkingSpaceService.getSpace(pr.getSpaceId());
                                if (space != null) {
                                    space.setStatus("free");
                                    parkingSpaceService.updateSpace(space);
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        return "redirect:/order/list";
    }

    private String getStatusLabel(String status) {
        if ("paid".equals(status)) return "已支付";
        if ("unpaid".equals(status)) return "待支付";
        if ("completed".equals(status)) return "已完成";
        return status;
    }

    private String getStatusClass(String status) {
        if ("paid".equals(status) || "completed".equals(status)) return "badge-success";
        if ("unpaid".equals(status)) return "badge-warning";
        return "badge-danger";
    }
}
