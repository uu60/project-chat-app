package com.ph.teamappbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author octopus
 * @since 2023/4/15 20:36
 */
@Data
@TableName("t_user")
public class UserEntity {
    @TableId(type = IdType.AUTO)
    Integer id;
    String username;
    String password;
    String nickname;
    String phone;
    String email;
    String address;
    @TableField("portrait_url")
    String portraitUrl;
    @TableField("register_time")
    Date registerTime;
}
