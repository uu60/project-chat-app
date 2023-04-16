package com.ph.teamappbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author octopus
 * @since 2023/4/17 01:01
 */
@Data
@TableName("t_contact")
public class Contact {
    @TableId(type = IdType.AUTO)
    Integer id;
    @TableField("user_id")
    Integer userId;
    @TableField("contact_id")
    Integer contactId;
    @TableField("add_time")
    Date addTime;
}
