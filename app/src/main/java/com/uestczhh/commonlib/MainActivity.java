package com.uestczhh.commonlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.uestczhh.commlib.http.ErrorMsg;
import com.uestczhh.commlib.http.HttpJsonCallback;
import com.uestczhh.commlib.http.OkHttpManager;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        http://113.108.139.178:11805/front/demandList?busiDataType=0&pageNO=1&keyWord=&cityCode=0&pageSize=10&listType=0&busiTypeCode=0
        Map<String, Object> map = new HashMap<>();
        map.put("busiDataType", 0);
        map.put("pageNO", 1);
        map.put("cityCode", 0);
        map.put("pageSize", 10);
        map.put("listType", 0);
        map.put("busiTypeCode", 0);
        OkHttpManager.getInstance().doGetRequest(" http://113.108.139.178:11805/front/demandList", map, new HttpJsonCallback<Object>() {
            @Override
            public void onFail(String tag, ErrorMsg errorMsg) {

            }

            @Override
            public void onSucc(String tag, Object object) {

            }
        });
    }
}
