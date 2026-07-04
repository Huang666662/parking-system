package com.parking.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.parking.entity.Reservation;
import com.parking.entity.User;
import com.parking.service.IReservationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private IReservationService reservationService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       Model model, HttpSession session) {
        String userType = (String) session.getAttribute("userType");
        Long userId = null;
        if ("user".equals(userType)) {
            User user = (User) session.getAttribute("user");
            if (user != null) userId = user.getId();
        }
        PageHelper.startPage(page, size);
        List<Reservation> list = reservationService.listAll();
        if ("user".equals(userType) && userId != null) {
            final Long currentUserId = userId;
            list.removeIf(r -> !currentUserId.equals(r.getUserId()));
        }
        PageInfo<Reservation> pageInfo = new PageInfo<>(list);
        model.addAttribute("page", pageInfo);
        model.addAttribute("current", "reservation");
        return "reservation-list";
    }

    @GetMapping("/add")
    public String addPage(HttpSession session) {
        if (!"user".equals(session.getAttribute("userType"))) {
            return "redirect:/login";
        }
        return "reservation-add";
    }

    @PostMapping("/add")
    public String add(@RequestParam String plateNumber,
                      @RequestParam Long spaceId,
                      @RequestParam String reserveTime,
                      @RequestParam BigDecimal depositAmount,
                      HttpSession session) {
        if (!"user".equals(session.getAttribute("userType"))) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        Reservation r = new Reservation();
        r.setReservationNo("RSV" + System.currentTimeMillis());
        r.setUserId(user.getId());
        r.setPlateNumber(plateNumber);
        r.setSpaceId(spaceId);
        r.setReserveTime(LocalDateTime.parse(reserveTime.replace(' ', 'T')));
        r.setDepositAmount(depositAmount);
        r.setStatus("pending");
        reservationService.addReservation(r);
        return "redirect:/reservation/list";
    }

    @GetMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return "redirect:/reservation/list";
    }
}
