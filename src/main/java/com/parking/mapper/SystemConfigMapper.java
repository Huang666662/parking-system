package com.parking.mapper;

import com.parking.entity.SystemConfig;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SystemConfigMapper {
    @Select("SELECT * FROM system_config")
    List<SystemConfig> selectAll();

    @Select("SELECT * FROM system_config WHERE id = #{id}")
    SystemConfig selectById(Integer id);

    @Insert("INSERT INTO system_config (config_key, config_value, description, update_time) VALUES (#{configKey}, #{configValue}, #{description}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SystemConfig config);

    @Update("UPDATE system_config SET config_key=#{configKey}, config_value=#{configValue}, description=#{description}, update_time=NOW() WHERE id=#{id}")
    int update(SystemConfig config);

    @Select("SELECT * FROM system_config WHERE config_key = #{configKey}")
    SystemConfig selectByKey(String configKey);

    @Delete("DELETE FROM system_config WHERE id = #{id}")
    int deleteById(Integer id);
}
