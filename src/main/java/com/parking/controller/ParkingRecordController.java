package com.parking.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.parking.entity.ParkingRecord;
import com.parking.entity.User;
import com.parking.service.IParkingRecordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/parking-record")
public class ParkingRecordController {

    @Autowired
    private IParkingRecordService parkingRecordService;

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
        List<ParkingRecord> list = parkingRecordService.listRecords(plateNumber);
        if ("user".equals(userType) && userId != null) {
            final Long currentUserId = userId;
            list.removeIf(r -> !currentUserId.equals(r.getUserId()));
        }
        PageInfo<ParkingRecord> pageInfo = new PageInfo<>(list);
        model.addAttribute("page", pageInfo);
        model.addAttribute("plateNumber", plateNumber);
        model.addAttribute("current", "record");
        return "parking-record-list";
    }

    @GetMapping("/enter")
    public String enterPage() {
        return "parking-record-enter";
    }

    @PostMapping("/enter")
    public String enter(@RequestParam String plateNumber,
                        @RequestParam(required = false) Long userId,
                        @RequestParam(required = false) Long spaceId) {
        parkingRecordService.enter(plateNumber, userId, spaceId);
        return "redirect:/parking-record/list";
    }

    @GetMapping("/exit")
    public String exitPage() {
        return "parking-record-exit";
    }

    @PostMapping("/exit")
    public String exit(@RequestParam Long recordId,
                       @RequestParam String paymentMethod) {
        parkingRecordService.exit(recordId, paymentMethod);
        return "redirect:/parking-record/list";
    }
}
