package com.ph.teamappbackend.mapper;

import com.ph.teamappbackend.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author octopus
 * @since 2023/4/15 20:38
 */
@Mapper
public interface UserMapper {

    @Select("select id, username, password from t_user where username = #{username}")
    User selectOneByUsername(String username);
}
