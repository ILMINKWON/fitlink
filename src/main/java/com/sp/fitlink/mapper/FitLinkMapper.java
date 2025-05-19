package com.sp.fitlink.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FitLinkMapper {

    public String findByAdminCode();

}
