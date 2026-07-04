package com.parking.controller;

import com.parking.entity.User;
import com.parking.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private IParkingSpaceService parkingSpaceService;

    @Autowired
    private IParkingRecordService parkingRecordService;

    @Autowired
    private IOrdersService ordersService;

    @Autowired
    private IReservationService reservationService;

    @Autowired
    private IVehicleService vehicleService;

    @GetMapping({"/", "/index"})
    public String index(Model model, HttpSession session) {
        model.addAttribute("totalSpaces", 0);
        model.addAttribute("freeSpaces", 0);
        model.addAttribute("currentParked", 0);
        model.addAttribute("todayIncome", BigDecimal.ZERO);
        model.addAttribute("vehicleCount", 0);
        model.addAttribute("reservationCount", 0);
        model.addAttribute("unpaidOrders", 0);

        List<Map<String, String>> dashboardErrorDetails = new ArrayList<>();
        model.addAttribute("dashboardErrorDetails", dashboardErrorDetails);

        String userType = (String) session.getAttribute("userType");
        if ("admin".equals(userType)) {
            try {
                model.addAttribute("totalSpaces", parkingSpaceService.listAll().size());
            } catch (Exception ex) {
                addDashboardError(dashboardErrorDetails, "parking_space", ex);
            }
            try {
                model.addAttribute("freeSpaces", parkingSpaceService.listFreeSpaces().size());
            } catch (Exception ex) {
                addDashboardError(dashboardErrorDetails, "parking_space", ex);
            }
            try {
                long currentParked = parkingSpaceService.listAll().stream()
                        .filter(s -> "occupied".equals(s.getStatus()))
                        .count();
                model.addAttribute("currentParked", (int) currentParked);
            } catch (Exception ex) {
                addDashboardError(dashboardErrorDetails, "parking_space", ex);
            }
            try {
                BigDecimal todayIncome = BigDecimal.ZERO;
                for (Object o : ordersService.listOrders(null)) {
                    if (o instanceof com.parking.entity.Orders order) {
                        if (order.getCreateTime() != null && order.getCreateTime().toLocalDate().equals(LocalDate.now())) {
                            todayIncome = todayIncome.add(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
                        }
                    }
                }
                model.addAttribute("todayIncome", todayIncome);
            } catch (Exception ex) {
                addDashboardError(dashboardErrorDetails, "order", ex);
            }
        } else if ("user".equals(userType)) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                try {
                    model.addAttribute("vehicleCount", vehicleService.listByUserId(user.getId()).size());
                } catch (Exception ex) {
                    addDashboardError(dashboardErrorDetails, "vehicle", ex);
                }
                try {
                    int reservationCount = 0;
                    for (Object r : reservationService.listAll()) {
                        if (r instanceof com.parking.entity.Reservation reservation && user.getId().equals(reservation.getUserId())) {
                            String status = reservation.getStatus();
                            if ("pending".equals(status) || "confirmed".equals(status)) {
                                reservationCount++;
                            }
                        }
                    }
                    model.addAttribute("reservationCount", reservationCount);
                } catch (Exception ex) {
                    addDashboardError(dashboardErrorDetails, "reservation", ex);
                }
                try {
                    int unpaidOrders = 0;
                    for (Object o : ordersService.listOrders(null)) {
                        if (o instanceof com.parking.entity.Orders order && user.getId().equals(order.getUserId())) {
                            if ("unpaid".equals(order.getStatus())) {
                                unpaidOrders++;
                            }
                        }
                    }
                    model.addAttribute("unpaidOrders", unpaidOrders);
                } catch (Exception ex) {
                    addDashboardError(dashboardErrorDetails, "order", ex);
                }
            }
        }

        if (!dashboardErrorDetails.isEmpty()) {
            model.addAttribute("dashboardError", "首页部分数据加载失败，点击查看详情");
        }
        return "index";
    }

    private void addDashboardError(List<Map<String, String>> dashboardErrorDetails, String tableName, Exception ex) {
        Map<String, String> detail = new HashMap<>();
        detail.put("tableName", tableName);
        String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
        if (ex.getCause() != null && ex.getCause().getMessage() != null && !ex.getCause().getMessage().isEmpty()) {
            message = ex.getCause().getMessage();
        }
        detail.put("message", message);
        dashboardErrorDetails.add(detail);
    }
}
