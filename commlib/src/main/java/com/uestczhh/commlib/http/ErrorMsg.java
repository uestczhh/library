package com.uestczhh.commlib.http;

/**
 * 请求错误信息
 * Created by zhanghao on 2017/6/6.
 */

public class ErrorMsg {
    /**
     * 超时，或者网络出错
     */
    public static final int ERR_CODE_TIMEOUT = -1;
    /**
     * 登录失败3次 需要验证码 code
     */
    public static final int ERR_CODE_1101 = 1101;
    /**
     * url不正确
     */
    public static final int ERR_CODE_404 = 404;
    /**
     * 服务器内部错误
     */
    public static final int ERR_CODE_500 = 500;

    /**
     * 异常CODE
     */
    public static final int ERR_CODE_EXCEPTION = 0;


    //请求返回code
    private int code = ERR_CODE_TIMEOUT;
    //失败信息
    private String errMsg = "网络异常";

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public ErrorMsg(int code, String errMsg) {
        this.code = code;
        this.errMsg = errMsg;
    }

    public ErrorMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public ErrorMsg() {

    }
}
