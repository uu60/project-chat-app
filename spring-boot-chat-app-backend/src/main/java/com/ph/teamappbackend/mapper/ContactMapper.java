package com.ph.teamappbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ph.teamappbackend.pojo.entity.Contact;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author octopus
 * @since 2023/4/17 01:02
 */
@Mapper
public interface ContactMapper extends BaseMapper<Contact> {
}
