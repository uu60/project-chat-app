package com.ph.teamappbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @author octopus
 * @since 2023/4/17 23:37
 */
@Data
@TableName("t_contact_request")
public class ContactRequest {

    @TableId(type = IdType.AUTO)
    Integer id;
    @TableField("user_id")
    Integer userId;
    @TableField("contact_id")
    Integer contactId;
    @TableField("request_time")
    Date requestTime;
    @TableLogic
    @TableField(value = "delete_status")
    Integer deleteStatus = 0;
}
