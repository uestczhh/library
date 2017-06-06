package com.uestczhh.commlib.http;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 网络请求回调.
 * Created by zhanghao on 2017/6/6.
 */

public abstract class HttpJsonCallback<T> {

    private Type mType;


    public HttpJsonCallback(Type type) {
        this.mType = type;
    }

    public HttpJsonCallback() {
    }

    /**
     * 请求失败
     *
     * @param tag
     * @param errorMsg
     */
    public abstract void onFail(String tag, ErrorMsg errorMsg);

    /**
     * 请求成功
     *
     * @param tag
     * @param object
     * @throws IOException
     */
    public abstract void onSucc(String tag, T object);

    /**
     * 获取type
     *
     * @return
     */
    public Type getType() {
        if (mType != null) {
            return mType;
        }
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return type;
    }

}
