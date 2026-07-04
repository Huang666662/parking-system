package com.parking.service.impl;

import com.parking.entity.ReconciliationReport;
import com.parking.mapper.ReconciliationReportMapper;
import com.parking.service.IReconciliationReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReconciliationReportServiceImpl implements IReconciliationReportService {

    @Autowired
    private ReconciliationReportMapper reconciliationReportMapper;

    @Override
    public int addReport(ReconciliationReport report) {
        return reconciliationReportMapper.insert(report);
    }

    @Override
    public ReconciliationReport getReport(Long id) {
        return reconciliationReportMapper.selectById(id);
    }

    @Override
    public List<ReconciliationReport> listAll() {
        return reconciliationReportMapper.selectAll();
    }
}