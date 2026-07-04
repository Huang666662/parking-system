package com.parking.mapper;

import com.parking.entity.Merchant;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MerchantMapper {
    @Select("SELECT * FROM merchant WHERE id = #{id}")
    Merchant selectById(Long id);

    @Insert("INSERT INTO merchant (merchant_name, merchant_code, contact_person, contact_phone, settlement_rate, status, create_time) VALUES (#{merchantName}, #{merchantCode}, #{contactPerson}, #{contactPhone}, #{settlementRate}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Merchant merchant);

    @Update("UPDATE merchant SET merchant_name=#{merchantName}, merchant_code=#{merchantCode}, contact_person=#{contactPerson}, contact_phone=#{contactPhone}, settlement_rate=#{settlementRate}, status=#{status} WHERE id=#{id}")
    int update(Merchant merchant);

    @Delete("DELETE FROM merchant WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM merchant")
    List<Merchant> selectAll();
}