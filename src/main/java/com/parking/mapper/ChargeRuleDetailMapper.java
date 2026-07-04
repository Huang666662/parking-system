package com.parking.mapper;

import com.parking.entity.ChargeRuleDetail;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ChargeRuleDetailMapper {
    @Select("SELECT * FROM charge_rule_detail WHERE rule_id = #{ruleId}")
    List<ChargeRuleDetail> selectByRuleId(@Param("ruleId") Integer ruleId);

    @Insert("INSERT INTO charge_rule_detail (rule_id, day_of_week, start_time, end_time, unit_price) VALUES (#{ruleId}, #{dayOfWeek}, #{startTime}, #{endTime}, #{unitPrice})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChargeRuleDetail detail);
}