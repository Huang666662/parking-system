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
import java.time.LocalDateTime;
import java.util.List;

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
        String userType = (String) session.getAttribute("userType");
        if ("admin".equals(userType)) {
            int totalSpaces = parkingSpaceService.listAll().size();
            int freeSpaces = parkingSpaceService.listFreeSpaces().size();
            int currentParked = 0;
            List<? extends Object> records = parkingRecordService.listRecords(null);
            for (Object r : records) {
                if (r instanceof com.parking.entity.ParkingRecord && "parking".equals(((com.parking.entity.ParkingRecord) r).getStatus())) {
                    currentParked++;
                }
            }
            BigDecimal todayIncome = BigDecimal.ZERO;
            for (Object o : ordersService.listOrders(null)) {
                if (o instanceof com.parking.entity.Orders) {
                    com.parking.entity.Orders order = (com.parking.entity.Orders) o;
                    if (order.getCreateTime() != null && order.getCreateTime().toLocalDate().equals(LocalDate.now())) {
                        todayIncome = todayIncome.add(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
                    }
                }
            }
            model.addAttribute("totalSpaces", totalSpaces);
            model.addAttribute("freeSpaces", freeSpaces);
            model.addAttribute("currentParked", currentParked);
            model.addAttribute("todayIncome", todayIncome);
        } else if ("user".equals(userType)) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                int vehicleCount = vehicleService.listByUserId(user.getId()).size();
                int reservationCount = 0;
                for (Object r : reservationService.listAll()) {
                    if (r instanceof com.parking.entity.Reservation && user.getId().equals(((com.parking.entity.Reservation) r).getUserId())) {
                        String status = ((com.parking.entity.Reservation) r).getStatus();
                        if ("pending".equals(status) || "confirmed".equals(status)) {
                            reservationCount++;
                        }
                    }
                }
                int unpaidOrders = 0;
                for (Object o : ordersService.listOrders(null)) {
                    if (o instanceof com.parking.entity.Orders && user.getId().equals(((com.parking.entity.Orders) o).getUserId())) {
                        if ("unpaid".equals(((com.parking.entity.Orders) o).getStatus())) {
                            unpaidOrders++;
                        }
                    }
                }
                model.addAttribute("vehicleCount", vehicleCount);
                model.addAttribute("reservationCount", reservationCount);
                model.addAttribute("unpaidOrders", unpaidOrders);
            }
        }
        return "index";
    }
}
