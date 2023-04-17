package com.ph.teamappbackend.utils;

import com.ph.teamappbackend.constant.ErrorCodeConst;
import lombok.Data;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author octopus
 * @date 2023/4/15 20:14
 */
@Data
public class Resp {
    private Integer code = ErrorCodeConst.SUCCESS;
    private String msg;
    private Object data;

    public static Resp ok() {
        Resp resp = new Resp();
        resp.msg = "success";
        return resp;
    }

    public static Resp ok(String msg) {
        Resp resp = new Resp();
        resp.msg = msg;
        return resp;
    }

    public static Resp error(int code) {
        Resp resp = new Resp();
        resp.code = code;
        return resp;
    }

    public static Resp error(int code, String msg) {
        Resp resp = new Resp();
        resp.code = code;
        resp.msg = msg;
        return resp;
    }

    public Resp setData(Object data) {
        this.data = data;
        return this;
    }
}
