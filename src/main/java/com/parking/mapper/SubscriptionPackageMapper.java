package com.parking.mapper;

import com.parking.entity.SubscriptionPackage;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SubscriptionPackageMapper {
    @Select("SELECT * FROM subscription_package WHERE id = #{id}")
    SubscriptionPackage selectById(Integer id);

    @Select("SELECT * FROM subscription_package WHERE is_active = 1")
    List<SubscriptionPackage> selectActive();
}