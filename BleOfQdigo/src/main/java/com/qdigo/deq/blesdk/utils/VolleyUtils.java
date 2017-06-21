package com.qdigo.deq.blesdk.utils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;


/**
 * Created by jpj on 2017/1/11.
 * 非常适合去进行数据量不大，但通信频繁的网络操作，而对于大数据量的网络操作，比如说下载文件等，Volley的表现就会非常糟糕。
 */

public class VolleyUtils {

    //请求队列（在每一个需要和网络交互的Activity中创建一个RequestQueue对象就足够了）
    private static RequestQueue mQueue;

    interface VolleyListener{
        void onSuccess(String s);
        void onError();
    }

    /**
     * 1、 使用第一部创建请求队列
     *
     * @param context
     */
    public static void init(Context context) {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(context);
        }
    }

    /**
     * get 方式
     *
     * @param url
     * @param mVolleyListener
     */
    public static void doGet(String url, final VolleyListener mVolleyListener) {
            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    mVolleyListener.onSuccess(s);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mVolleyListener.onError();
                }
            });
            mQueue.add(request);


    }

    public static void doPost(String url, final Map<String,String> params, final VolleyListener mVolleyListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                mVolleyListener.onSuccess(s);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mVolleyListener.onError();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if(params == null){
                    return null;
                }
                return params;
            }
        };
        mQueue.add(stringRequest);
    }

}
