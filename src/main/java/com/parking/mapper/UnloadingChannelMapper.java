package com.parking.mapper;

import com.parking.entity.UnloadingChannel;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UnloadingChannelMapper {
    @Select("SELECT * FROM unloading_channel WHERE id = #{id}")
    UnloadingChannel selectById(Integer id);

    @Select("SELECT * FROM unloading_channel WHERE is_enabled = 1")
    List<UnloadingChannel> selectEnabled();
}