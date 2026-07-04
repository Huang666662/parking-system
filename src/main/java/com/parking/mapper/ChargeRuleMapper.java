package com.parking.mapper;

import com.parking.entity.ChargeRule;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ChargeRuleMapper {
    @Select("SELECT * FROM charge_rule WHERE id = #{id}")
    ChargeRule selectById(Integer id);

    @Insert("INSERT INTO charge_rule (rule_name, rule_type, unit_price, free_minutes, cap_price, is_active, effective_date, expire_date) VALUES (#{ruleName}, #{ruleType}, #{unitPrice}, #{freeMinutes}, #{capPrice}, #{isActive}, #{effectiveDate}, #{expireDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChargeRule rule);

    @Update("UPDATE charge_rule SET rule_name=#{ruleName}, rule_type=#{ruleType}, unit_price=#{unitPrice}, free_minutes=#{freeMinutes}, cap_price=#{capPrice}, is_active=#{isActive}, effective_date=#{effectiveDate}, expire_date=#{expireDate} WHERE id=#{id}")
    int update(ChargeRule rule);

    @Delete("DELETE FROM charge_rule WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT * FROM charge_rule WHERE is_active = 1")
    List<ChargeRule> selectActive();
}