package com.uestczhh.commlib.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * json解析工具类
 * Created by zhanghao on 2017/6/6.
 */

public class JsonUtil {
    private static Gson mGson = new Gson();

    /**
     * 对象转成json
     *
     * @param o
     * @return
     */
    public static String toJson(Object o) {
        if (o == null) {
            return null;
        }
        String json = "";
        try {
            json = mGson.toJson(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 字符串json根据实体类型转成实体
     *
     * @param json
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        T t = null;
        try {
            t = mGson.fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 字符串json根据type转成实体
     *
     * @param json
     * @param typeOfT
     * @param <T>
     * @return
     * @throws JsonSyntaxException
     */
    public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        T t = null;
        try {
            t = mGson.fromJson(json, typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;


    }
}
