package com.ph.teamappbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author octopus
 * @since 2023/4/15 20:36
 */
@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.AUTO)
    Integer id;
    String username;
    String password;
}
