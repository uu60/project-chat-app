package com.ph.teamappbackend.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ph.teamappbackend.pojo.entity.User;
import lombok.Data;

import java.util.Date;

/**
 * @author octopus
 * @since 2023/4/17 23:47
 */
@Data
public class RequestUserTo {
    Integer id;
    String nickname;
    String portraitUrl;
    Date requestTime;
}
