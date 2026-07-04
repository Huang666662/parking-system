package com.parking.mapper;

import com.parking.entity.MerchantUser;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MerchantUserMapper {
    @Select("SELECT * FROM merchant_user WHERE id = #{id}")
    MerchantUser selectById(Long id);

    @Insert("INSERT INTO merchant_user (merchant_id, username, password, role, status) VALUES (#{merchantId}, #{username}, #{password}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MerchantUser user);

    @Select("SELECT * FROM merchant_user WHERE merchant_id = #{merchantId}")
    List<MerchantUser> selectByMerchantId(@Param("merchantId") Long merchantId);
}