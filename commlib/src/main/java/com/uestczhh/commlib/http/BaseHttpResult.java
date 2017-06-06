package com.uestczhh.commlib.http;

/**
 * 基础数据结构类
 * Created by zhanghao on 2017/6/6.
 */

public class BaseHttpResult<T> {
    private static final int CODE_SUCC = 1000;
    private static final int CODE_LOGINOUT = 210018;
    //返回状态码
    public int code;
    //返回描述
    public String message;

    public T data;

    public String token;

    /**
     * 数据请求是否成功
     *
     * @return
     */
    public boolean isSucc() {
        if (CODE_SUCC == code) {
            return true;
        }
        return false;
    }

    /**
     * 是否登录异常
     *
     * @return
     */
    public boolean isLoginOut() {
        if (CODE_LOGINOUT == code) {
            return true;
        }
        return false;
    }
}
