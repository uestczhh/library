package com.uestczhh.commlib.http;

import android.text.TextUtils;

import com.uestczhh.commlib.utils.JsonUtil;
import com.uestczhh.commlib.utils.LogUtil;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;

/**
 * 网络请求管理类
 * Created by zhanghao on 2017/6/6.
 */

public class OkHttpManager {
    private static OkHttpManager mInstance;

    private OkHttpUtil mHttpUtil;

    private String mToken;//用户token
    private String mVerson; //版本号
    private String mClient;//客户端类型


    private OkHttpManager() {
        mHttpUtil = new OkHttpUtil();
    }

    public static OkHttpManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpManager();
                }
            }
        }
        return mInstance;
    }


    /**
     * http get请求
     *
     * @param url      请求地址
     * @param map      请求参数
     * @param callback 回调
     */
    public void doGetRequest(String url, Map<String, Object> map, HttpJsonCallback callback) {
        doGetRequest("", url, map, callback);
    }

    /**
     * http get请求
     *
     * @param tag      唯一标识标签
     * @param url      请求地址
     * @param map      请求参数
     * @param callback 回调
     */
    public void doGetRequest(String tag, String url, Map<String, Object> map, final HttpJsonCallback callback) {
        mHttpUtil.doGetRequest(url, map, tag, mClient, mVerson, mToken, new HttpJsonCallback(callback.getType()) {
            @Override
            public void onFail(String tag, ErrorMsg errorMsg) {
                dealHttpFail(tag, errorMsg, callback);
            }

            @Override
            public void onSucc(String tag, Object object) {
                dealHttpSucc(tag, object, callback);
            }
        });
    }

    /**
     * post请求
     *
     * @param tag      唯一标识
     * @param url      url
     * @param json     json
     * @param callback 回调
     */
    public void doPostRequest(String tag, String url, String json, final HttpJsonCallback callback) {
        mHttpUtil.doPostJsonRequest(url, json, tag, mClient, mVerson, mToken, new HttpJsonCallback(callback.getType()) {
            @Override
            public void onFail(String tag, ErrorMsg errorMsg) {
                dealHttpFail(tag, errorMsg, callback);
            }

            @Override
            public void onSucc(String tag, Object object) {
                dealHttpSucc(tag, object, callback);
            }
        });
    }

    /**
     * post请求
     *
     * @param tag      唯一标识
     * @param url      url
     * @param parms    json
     * @param callback 回调
     */
    public void doPostRequest(String tag, String url, Map<String, Object> parms, final HttpJsonCallback callback) {
        String json = JsonUtil.toJson(parms);
        mHttpUtil.doPostJsonRequest(url, json, tag, mClient, mVerson, mToken, new HttpJsonCallback(callback.getType()) {
            @Override
            public void onFail(String tag, ErrorMsg errorMsg) {
                dealHttpFail(tag, errorMsg, callback);
            }

            @Override
            public void onSucc(String tag, Object object) {
                dealHttpSucc(tag, object, callback);
            }
        });
    }

    /**
     * post请求
     *
     * @param url      url
     * @param parms    json
     * @param callback 回调
     */
    public void doPostRequest(String url, Map<String, Object> parms, final HttpJsonCallback callback) {
        doPostRequest("", url, parms, callback);
    }

    /**
     * post请求
     *
     * @param url      请求url
     * @param json     json
     * @param callback 回调
     */
    public void doPostRequest(String url, String json, final HttpJsonCallback callback) {
        doPostRequest("", url, json, callback);
    }

    /**
     * 成功处理
     *
     * @param tag
     * @param object
     * @param callback
     */
    private synchronized void dealHttpSucc(String tag, Object object, HttpJsonCallback callback) {
        try {
            if (!(object instanceof BaseHttpResult)) {
                dealHttpFail(tag, new ErrorMsg(), callback);
                return;
            }
            BaseHttpResult result = (BaseHttpResult) object;
            if (result.isSucc()) {//请求成功，回调出去
                callback.onSucc(tag, result);
            } else if (result.isLoginOut()) {//登录异常，清除用户信息，退出到登录页面，发送eventbus通知主页登录异常
//                LjBaseApplication.context().cleanUserInfo();
//                LjBaseApplication.context().toLoginActivity(LjBaseApplication.context());
//
//                LoginStatusEvent loginStatusEvent = new LoginStatusEvent();
//                loginStatusEvent.setLogin(false);
//                EventBus.getDefault().post(loginStatusEvent);
//                SpSettingUtil.getInstance().putString(ConstantConfig.TOKEN, "");
            } else {
                dealHttpFail(tag, new ErrorMsg(result.message), callback);
            }
            //保存token
            mToken = result.token;
            if (!TextUtils.isEmpty(mToken)) {
                //需要的话  存储token
//                SpSettingUtil.getInstance().putString(ConstantConfig.TOKEN, mToken);
            }
        } catch (Exception e) {
            dealHttpFail(tag, new ErrorMsg(), callback);
            LogUtil.e(e.getMessage());
        }
    }

    /**
     * 处理失败
     *
     * @param tag
     * @param errorMsg
     * @param callback
     */
    private synchronized void dealHttpFail(String tag, ErrorMsg errorMsg, HttpJsonCallback callback) {
        try {
            callback.onFail(tag, errorMsg);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }

    /**
     * 初始化头部参数
     * 在Application中调用
     */
    private void initManager(String token, String client, String version) {
        //暂时每次都要获取新的tooken，因为有些请求没有迁移到新的请求上面来
        mToken = token;

        if (TextUtils.isEmpty(mClient)) {
            mClient = client;
        }
        if (TextUtils.isEmpty(mVerson)) {
//            mVerson = InfoUtil.getVersionName(LjBaseApplication.context());
            mVerson = version;
        }
    }

    /**
     * 设置https证书  debug环境时如果需要证书，调用此方法
     * 在Application中HttpManager.getInstance().setCertificates(new Buffer().writeUtf8(ConstantConfig.CA).inputStream());
     *
     * @param certificates
     */
    public void setCertificates(InputStream certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("0", certificateFactory.generateCertificate(certificates));
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            mHttpUtil.setSslSocketFactory(sslContext.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OkHttpClient getOkHttpClient() {
        return mHttpUtil.getOkHttpClient();
    }
}
