package com.parking.mapper;

import com.parking.entity.OperationLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OperationLogMapper {
    @Select("SELECT * FROM operation_log WHERE id = #{id}")
    OperationLog selectById(Long id);

    @Insert("INSERT INTO operation_log (operator_id, operator_name, operation_type, operation_content, ip_address, create_time) VALUES (#{operatorId}, #{operatorName}, #{operationType}, #{operationContent}, #{ipAddress}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog log);

    @Select("SELECT * FROM operation_log ORDER BY create_time DESC")
    List<OperationLog> selectAll();
}