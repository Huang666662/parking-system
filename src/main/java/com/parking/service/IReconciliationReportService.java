package com.parking.service;

import com.parking.entity.ReconciliationReport;
import java.util.List;

public interface IReconciliationReportService {
    int addReport(ReconciliationReport report);
    ReconciliationReport getReport(Long id);
    List<ReconciliationReport> listAll();
}