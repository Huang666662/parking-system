package com.parking.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.parking.entity.*;
import com.parking.service.*;
import com.parking.util.OperationLogHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/parking-record")
public class ParkingRecordController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private IParkingRecordService parkingRecordService;

    @Autowired
    private IChargeRuleService chargeRuleService;

    @Autowired
    private ISystemConfigService systemConfigService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IMemberLevelService memberLevelService;

    @Autowired
    private OperationLogHelper operationLogHelper;

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

        // 预格式化日期
        List<Map<String, Object>> recordList = new ArrayList<>();
        for (ParkingRecord r : list) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", r.getId());
            entry.put("recordNo", r.getRecordNo());
            entry.put("plateNumber", r.getPlateNumber());
            entry.put("enterTimeStr", r.getEnterTime() != null ? r.getEnterTime().format(FMT) : "");
            entry.put("exitTimeStr", r.getExitTime() != null ? r.getExitTime().format(FMT) : "");
            entry.put("durationMinutes", r.getDurationMinutes());
            entry.put("actualFee", r.getActualFee());
            entry.put("status", r.getStatus());
            entry.put("statusLabel", "parking".equals(r.getStatus()) ? "停放中" : "已完成");
            entry.put("statusClass", "parking".equals(r.getStatus()) ? "badge-warning" : "badge-success");
            entry.put("paymentStatus", r.getPaymentStatus());
            recordList.add(entry);
        }

        PageInfo<ParkingRecord> pageInfo = new PageInfo<>(list);
        model.addAttribute("page", pageInfo);
        model.addAttribute("recordList", recordList);
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
                        @RequestParam(required = false) Long spaceId,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        // 普通用户自动关联当前用户ID
        if ("user".equals(session.getAttribute("userType")) && userId == null) {
            User u = (User) session.getAttribute("user");
            if (u != null) userId = u.getId();
        }
        try {
            parkingRecordService.enter(plateNumber, userId, spaceId);
            operationLogHelper.log(session, "车辆入场", "车辆入场：" + plateNumber);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                msg = e.getCause().getMessage();
            }
            redirectAttributes.addFlashAttribute("enterError", msg != null ? msg : "入场失败，请稍后重试");
            return "redirect:/parking-record/enter";
        }
        return "redirect:/parking-record/list";
    }

    @GetMapping("/exit")
    public String exitPage() {
        return "parking-record-exit";
    }

    @PostMapping("/exit/lookup")
    public String exitLookup(@RequestParam Long recordId, Model model) {
        ParkingRecord record = parkingRecordService.getRecord(recordId);
        if (record == null || !"parking".equals(record.getStatus())) {
            model.addAttribute("lookupError", "记录不存在或已离场");
            return "parking-record-exit";
        }
        addFeePreviewToModel(record, model);
        return "parking-record-exit";
    }

    @PostMapping("/exit")
    public String exit(@RequestParam Long recordId,
                       @RequestParam String paymentMethod,
                       HttpSession session) {
        ParkingRecord record = parkingRecordService.getRecord(recordId);
        parkingRecordService.exit(recordId, paymentMethod);
        operationLogHelper.log(session, "车辆出场", "车辆出场，记录ID：" + recordId + (record != null ? "，车牌：" + record.getPlateNumber() : ""));
        return "redirect:/parking-record/list";
    }

    // ===== 用户端出场缴费 =====

    @GetMapping("/user-exit/{id}")
    public String userExitPage(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        ParkingRecord record = parkingRecordService.getRecord(id);
        if (record == null || !"parking".equals(record.getStatus())
                || !user.getId().equals(record.getUserId())) {
            return "redirect:/parking-record/list";
        }
        addFeePreviewToModel(record, model);
        return "parking-record-user-exit";
    }

    @PostMapping("/user-exit/{id}")
    public String userExit(@PathVariable Long id,
                           @RequestParam String paymentMethod,
                           HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        ParkingRecord record = parkingRecordService.getRecord(id);
        if (record == null || !"parking".equals(record.getStatus())
                || !user.getId().equals(record.getUserId())) {
            return "redirect:/parking-record/list";
        }
        parkingRecordService.exit(id, paymentMethod);
        return "redirect:/order/list";
    }

    // ===== 费用预览 =====

    private void addFeePreviewToModel(ParkingRecord record, Model model) {
        model.addAttribute("record", record);
        model.addAttribute("enterTimeStr", record.getEnterTime().format(FMT));
        model.addAttribute("durationMinutes",
                Duration.between(record.getEnterTime(), LocalDateTime.now()).toMinutes());

        FeePreview fee = calculateFeePreview(record);
        model.addAttribute("originalFee", fee.originalFee);
        model.addAttribute("discountFee", fee.discountFee);
        model.addAttribute("actualFee", fee.actualFee);
        model.addAttribute("memberLevelName", fee.memberLevelName);
    }

    private FeePreview calculateFeePreview(ParkingRecord record) {
        FeePreview result = new FeePreview();
        long minutes = Duration.between(record.getEnterTime(), LocalDateTime.now()).toMinutes();

        List<ChargeRule> rules = chargeRuleService.listActive();
        ChargeRule rule = (rules != null && !rules.isEmpty()) ? rules.get(0) : null;

        Integer freeMinutes = getConfigInt("free_parking_minutes", rule != null ? rule.getFreeMinutes() : 15);
        BigDecimal unitPrice = getConfigDecimal("unit_price", rule != null ? rule.getUnitPrice() : new BigDecimal("5"));
        BigDecimal capPrice = getConfigDecimal("cap_amount", rule != null ? rule.getCapPrice() : null);

        int chargeableMinutes = Math.max(0, (int) minutes - freeMinutes);
        int hours = (int) Math.ceil(chargeableMinutes / 60.0);
        BigDecimal originalFee = unitPrice.multiply(BigDecimal.valueOf(hours));
        if (capPrice != null && originalFee.compareTo(capPrice) > 0) {
            originalFee = capPrice;
        }
        result.originalFee = originalFee.setScale(2, RoundingMode.HALF_UP);

        BigDecimal discountFee = BigDecimal.ZERO;
        Long userId = record.getUserId();
        if (userId != null) {
            User user = userService.getUser(userId);
            if (user != null && user.getMemberLevelId() != null) {
                MemberLevel level = memberLevelService.getLevel(user.getMemberLevelId());
                if (level != null && level.getDiscountRate() != null) {
                    BigDecimal rate = level.getDiscountRate();
                    discountFee = originalFee.multiply(BigDecimal.ONE.subtract(rate)).setScale(2, RoundingMode.HALF_UP);
                    result.memberLevelName = level.getLevelName();
                }
            }
        }
        result.discountFee = discountFee;
        result.actualFee = originalFee.subtract(discountFee).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        return result;
    }

    private Integer getConfigInt(String key, Integer defaultValue) {
        try {
            SystemConfig config = systemConfigService.getConfigByKey(key);
            if (config != null && config.getConfigValue() != null && !config.getConfigValue().isEmpty()) {
                try { return Integer.parseInt(config.getConfigValue()); } catch (NumberFormatException ignored) {}
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    private BigDecimal getConfigDecimal(String key, BigDecimal defaultValue) {
        try {
            SystemConfig config = systemConfigService.getConfigByKey(key);
            if (config != null && config.getConfigValue() != null && !config.getConfigValue().isEmpty()) {
                try { return new BigDecimal(config.getConfigValue()); } catch (NumberFormatException ignored) {}
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    private static class FeePreview {
        BigDecimal originalFee = BigDecimal.ZERO;
        BigDecimal discountFee = BigDecimal.ZERO;
        BigDecimal actualFee = BigDecimal.ZERO;
        String memberLevelName;
    }
}
