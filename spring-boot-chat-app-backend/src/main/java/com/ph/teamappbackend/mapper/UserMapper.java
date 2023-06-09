package com.ph.teamappbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ph.teamappbackend.pojo.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author octopus
 * @since 2023/4/15 20:38
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    @Select("select id, username, password from t_user where username = #{username}")
    UserEntity selectOneByUsername(String username);
}
