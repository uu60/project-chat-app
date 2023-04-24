package com.ph.teamappbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ph.teamappbackend.pojo.entity.MessageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author octopus
 * @since 2023/4/20 00:00
 */
@Mapper
public interface MessageMapper extends BaseMapper<MessageEntity> {

    @Select("select max(id) from t_message where (sender_id = #{currentUserId} and receiver_id = #{userId}) or " +
            "(sender_id = #{userId} and receiver_id = #{currentUserId})")
    Integer selectMaxMessageId(Integer currentUserId, Integer userId);
}
