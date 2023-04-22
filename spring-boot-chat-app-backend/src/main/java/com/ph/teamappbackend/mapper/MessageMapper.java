package com.ph.teamappbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ph.teamappbackend.pojo.entity.MessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author octopus
 * @since 2023/4/20 00:00
 */
@Mapper
public interface MessageMapper extends BaseMapper<MessageEntity> {
}
