package com.uestczhh.commlib.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.uestczhh.commlib.utils.JsonUtil;
import com.uestczhh.commlib.utils.LogUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络请求类，参数处理和请求错误处理.
 * Created by zhanghao on 2017/6/6.
 */

public class OkHttpUtil {
    private final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int TIME_OUT = 30;//网络请求超时默认是30秒

    private static final String KEY_CLIENT = "x-client";
    private static final String KEY_VERSON = "x-version";
    private static final String KEY_TOKEN = "x-token";

    private OkHttpClient okHttpClient;
    private OkHttpClient.Builder mBuilder;
    private int mTimeOut = TIME_OUT;


    /**
     * 初始化httpClient
     */
    private void initOkHttpClient() {
        if (okHttpClient != null) {
            return;
        }
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        okHttpClient = new OkHttpClient();
        mBuilder = okHttpClient.newBuilder();
        mBuilder.hostnameVerifier(DO_NOT_VERIFY);
        initTimeOut();
    }

    /**
     * 初始化超时时间
     */
    private void initTimeOut() {
        mBuilder.readTimeout(mTimeOut, TimeUnit.SECONDS);
        mBuilder.connectTimeout(mTimeOut, TimeUnit.SECONDS);
        mBuilder.writeTimeout(mTimeOut, TimeUnit.SECONDS);
    }

    /**
     * 设置链接超时时间
     *
     * @param timeOut
     */
    public void setTimeOut(int timeOut) {
        mTimeOut = timeOut;
        initOkHttpClient();
    }

    /**
     * 异步get请求  x-client
     */
    public void doGetRequest(String url, Map<String, Object> map, final String tag, String xClient, String vVison, String xToken, final HttpJsonCallback cb) {
        initOkHttpClient();
        Request request = buildGetRequest(url, map, tag, xClient, vVison, xToken);
        request(request, tag, cb);
    }


    /**
     * 异步post请求
     */
    public void doPostRequest(String url, Map<String, Object> map, final String tag, String xClient, String vVison, String xToken, final HttpJsonCallback cb) {
        initOkHttpClient();
        Request request = buildPostRequest(url, map, tag, xClient, vVison, xToken);
        request(request, tag, cb);
    }

    /**
     * 异步post请求
     */
    public void doPostJsonRequest(String url, final String json, final String tag, String xClient, String vVison, String xToken, final HttpJsonCallback cb) {
        initOkHttpClient();
        Request request = buildJsonPostRequest(url, json, tag, xClient, vVison, xToken);
        request(request, tag, cb);
    }

    /**
     * 设置证书
     *
     * @param factory
     */
    public void setSslSocketFactory(SSLSocketFactory factory) {
        initOkHttpClient();
        mBuilder.sslSocketFactory(factory);
    }

    /**
     * 发送请求
     *
     * @param request
     */
    private void request(Request request, final String tag, final HttpJsonCallback cb) {

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                dealHttpFail(cb, tag, call, e);
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                dealHttpSucc(cb, tag, call, response);
            }
        });
    }

    /**
     * http 错误处理
     *
     * @param cb
     * @param tag
     * @param call
     * @param e
     */
    private void dealHttpFail(final HttpJsonCallback cb, final String tag, okhttp3.Call call, Exception e) {
        if (call.isCanceled()) {
            return;
        }
        LogUtil.e("请求出问题====>" + e.getMessage());
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                cb.onFail(tag, new ErrorMsg());
            }
        });

    }

    /**
     * http成功请求处理
     *
     * @param callback 回调
     * @param tag      tag唯一标识
     * @param call     请求的call
     * @param response
     */
    private void dealHttpSucc(final HttpJsonCallback callback, final String tag, final okhttp3.Call call, final Response response) {
        if (call.isCanceled()) {
            return;
        }
        if (!response.isSuccessful()) {
            dealHttpFail(callback, tag, call, new IOException("网络异常"));
            return;
        }
        try {
            String body = response.body().string();
            LogUtil.e("返回数据====>" + body);

            if (TextUtils.isEmpty(body)) {
                dealHttpFail(callback, tag, call, new IOException("返回数据为空"));
                return;
            }

            //json解析
            Type type = callback.getType();
            final BaseHttpResult result = JsonUtil.fromJson(body, type);

            if (result == null) {
                dealHttpFail(callback, tag, call, new IOException("解析实体为空"));
                return;
            }

            //从response获取token保存到BaseHttpResult里面，方面外面获取
            String token = response.header(KEY_TOKEN);
            if (!TextUtils.isEmpty(token)) {
                result.token = token;
            }
            //执行回调
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    callback.onSucc(tag, result);
                }
            });
        } catch (Exception e) {
            dealHttpFail(callback, tag, call, e);
        }
    }

    /**
     * post请求构建reqeust
     */
    private Request buildPostRequest(String url, Map<String, Object> map, String tag, String xClient, String vVison, String xToken) {
        FormBody.Builder bilder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            bilder.add(entry.getKey(), entry.getValue() + "");
        }
        Request request = buildRequest(url, bilder.build(), tag, xClient, vVison, xToken);
        return request;
    }

    /**
     * post请求构建json reqeust
     */
    private Request buildJsonPostRequest(String url, String json, String tag, String xClient, String vVison, String xToken) {
        LogUtil.e("请求json====>" + json);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request request = buildRequest(url, requestBody, tag, xClient, vVison, xToken);
        return request;
    }

    /**
     * 构建请求对象
     *
     * @param url
     * @param body
     * @param tag
     * @return
     */
    private Request buildRequest(String url, FormBody body, String tag, String xClient, String vVison, String xToken) {
        Request.Builder builder = createRequestBuilder(url, tag, xClient, vVison, xToken);
        builder.post(body);
        return builder.build();
    }

    /**
     * 构建请求对象
     *
     * @param url
     * @param body
     * @param tag
     * @return
     */
    private Request buildRequest(String url, RequestBody body, String tag, String xClient, String vVison, String xToken) {
        Request.Builder builder = createRequestBuilder(url, tag, xClient, vVison, xToken);
        builder.post(body);
        return builder.build();
    }

    /**
     * get请求构建request
     */
    private Request buildGetRequest(String url, Map<String, Object> params, String tag, String xClient, String vVison, String xToken) {
        if (params != null && !params.isEmpty()) {
            url += "?";
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                url += entry.getKey() + "=" + entry.getValue() + "&";
            }
        }
        if (url.endsWith("&")) {
            url = url.substring(0, url.length() - 1);
        }
        Request.Builder builder = createRequestBuilder(url, tag, xClient, vVison, xToken);
        return builder.build();
    }

    /**
     * 创建request 的builder
     *
     * @param url     请求地址
     * @param tag     tag唯一标识
     * @param xClient 客户端标识
     * @param verson  版本
     * @param xToken  用户的tooken
     * @return
     */
    private Request.Builder createRequestBuilder(String url, String tag, String xClient, String verson, String xToken) {
        Request.Builder builder = new Request.Builder().url(url);
        if (!TextUtils.isEmpty(xClient)) {
            builder.addHeader(KEY_CLIENT, xClient);
        }

        if (!TextUtils.isEmpty(verson)) {
            builder.addHeader(KEY_VERSON, verson);
        }

        if (!TextUtils.isEmpty(xToken)) {
            builder.addHeader(KEY_TOKEN, xToken);
            LogUtil.e("x-token====>" + xToken);
        }
        if (tag != null) {
            builder.tag(tag);
        }
        LogUtil.e("请求url====>" + url);
        return builder;
    }

    public OkHttpClient getOkHttpClient() {
        if(okHttpClient == null){
            initOkHttpClient();
        }
        return okHttpClient;
    }
}
