package com.ph.teamappbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author octopus
 * @since 2023/4/19 23:58
 */
@Data
@TableName("t_message")
public class Message {
    @TableId
    Integer id;
    String content;
    @TableField("sender_id")
    Integer senderId;
    @TableField("receiver_id")
    Integer receiverId;
    @TableField("send_time")
    Date sendTime;
}
