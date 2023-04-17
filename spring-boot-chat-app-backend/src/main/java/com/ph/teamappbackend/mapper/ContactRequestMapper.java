package com.ph.teamappbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ph.teamappbackend.pojo.entity.Contact;
import com.ph.teamappbackend.pojo.entity.ContactRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author octopus
 * @since 2023/4/17 23:38
 */
@Mapper
public interface ContactRequestMapper extends BaseMapper<ContactRequest> {

}
