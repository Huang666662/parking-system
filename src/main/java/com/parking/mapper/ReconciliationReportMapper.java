package com.parking.mapper;

import com.parking.entity.ReconciliationReport;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ReconciliationReportMapper {
    @Select("SELECT * FROM reconciliation_report WHERE id = #{id}")
    ReconciliationReport selectById(Long id);

    @Insert("INSERT INTO reconciliation_report (report_date, total_income, cash_income, online_income, discount_amount, status, create_time) VALUES (#{reportDate}, #{totalIncome}, #{cashIncome}, #{onlineIncome}, #{discountAmount}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ReconciliationReport report);

    @Select("SELECT * FROM reconciliation_report ORDER BY report_date DESC")
    List<ReconciliationReport> selectAll();
}