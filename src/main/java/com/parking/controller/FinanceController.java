package com.parking.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.parking.entity.FinancialRecord;
import com.parking.entity.ReconciliationReport;
import com.parking.service.IFinancialRecordService;
import com.parking.service.IReconciliationReportService;
import com.parking.util.OperationLogHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    @Autowired
    private IFinancialRecordService financialRecordService;

    @Autowired
    private IReconciliationReportService reconciliationReportService;

    @Autowired
    private OperationLogHelper operationLogHelper;

    private boolean isAdmin(HttpSession session) {
        return "admin".equals(session.getAttribute("userType"));
    }

    @GetMapping("/report")
    public String report(@RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer size,
                         Model model,
                         HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/index";
        }
        PageHelper.startPage(page, size);
        List<ReconciliationReport> list = reconciliationReportService.listAll();
        PageInfo<ReconciliationReport> pageInfo = new PageInfo<>(list);
        model.addAttribute("page", pageInfo);
        return "finance-report";
    }

    @GetMapping("/record")
    public String record(@RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer size,
                         Model model,
                         HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/index";
        }
        PageHelper.startPage(page, size);
        List<FinancialRecord> list = financialRecordService.listAll();
        PageInfo<FinancialRecord> pageInfo = new PageInfo<>(list);
        model.addAttribute("page", pageInfo);
        return "finance-record";
    }

    @PostMapping("/generate-report")
    public String generateReport(HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/index";
        }
        ReconciliationReport report = new ReconciliationReport();
        report.setReportDate(LocalDate.now());
        report.setTotalIncome(BigDecimal.ZERO);
        report.setCashIncome(BigDecimal.ZERO);
        report.setOnlineIncome(BigDecimal.ZERO);
        report.setDiscountAmount(BigDecimal.ZERO);
        report.setStatus("pending");
        reconciliationReportService.addReport(report);
        operationLogHelper.log(session, "生成对账报告", "生成对账报告，日期：" + LocalDate.now());
        return "redirect:/finance/report";
    }
}