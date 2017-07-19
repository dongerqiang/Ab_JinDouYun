package com.qdigo.jindouyun.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.qdigo.jindouyun.BaseActivity;
import com.qdigo.jindouyun.R;
import com.qdigo.jindouyun.utils.DialogCallback;
import com.qdigo.jindouyun.utils.DialogUtils;
import com.qdigo.jindouyun.utils.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;

@SuppressWarnings("deprecation")
public class MulMapActivity extends BaseActivity {
    @Bind(R.id.mWebView)
    WebView mWebView;
    @Bind(R.id.running_time)
    TextView mRunningTime;
    @Bind(R.id.total_mile)
    TextView mTotalMile;
    @Bind(R.id.current_speed)
    TextView mCurrentSpeed;
    private int currentMap = 1;//1 高德 2 百度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_line);
        currentMap = 2;
    }

    @OnClick(R.id.iv_changeMap)
    public void changeMap(){
        Dialog passDialog = DialogUtils.createChangeMapDialog(this, new DialogCallback() {
            @Override
            public void confirm() {
                super.confirm();
                //baidu
                    mWebView.loadUrl("file:///android_asset/"+"baiduh5.html");
                currentMap = 2;

            }
            @Override
            public void camareClick() {
                super.camareClick();
                //gaode
                mWebView.loadUrl("file:///android_asset/"+"gaodeMap.html");
                currentMap =1;
            }
        });

        passDialog.show();

    }
    @OnClick(R.id.iv_position)
    public void position(){
        mWebView.loadUrl("javascript:moveCamera()");
    }
    @OnClick(R.id.iv_decrease)
    public void decreaseMap(){
            mWebView.loadUrl("javascript:decreaseMap()");
    }
    @OnClick(R.id.iv_increase)
    public void increaseMap(){
            mWebView.loadUrl("javascript:increaseMap()");
    }



    @Override
    protected void initView() {
        initSetting();
        startAssistLocation();
        mWebView.loadUrl("file:///android_asset/"+"gaodeMap.html");
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //Toast.makeText(mContext,"onPageStarted",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //Toast.makeText(mContext,"onPageFinished",Toast.LENGTH_SHORT).show();
                if(currentMap ==2){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            position();
                        }
                    },500);
                }else{
                    position();
                }
            }
        });
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient(){

            //配置权限（同样在WebChromeClient中实现）
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

        });
//        mWebView.setWebChromeClient(commonWebChromeWebClient,"test");
        mWebView.addJavascriptInterface(new JSBridge(), "android");

    }



    public class JSBridge {
        @JavascriptInterface
        public void toastMessage(String message) {
            ToastUtil.showToast(mContext,message);
        }


    }

    /**
     * 启动H5辅助定位
     */
    AMapLocationClient locationClientSingle;
    void startAssistLocation(){
        if(null == locationClientSingle){
            locationClientSingle = new AMapLocationClient(this.getApplicationContext());
        }
        locationClientSingle.startAssistantLocation();

//        Toast.makeText(MulMapActivity.this, "正在定位...", Toast.LENGTH_LONG).show();
    }

    private void initSetting() {
        WebSettings webSettings = mWebView.getSettings();
        //允许webview执行javaScript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置是否允许定位，这里为了使用H5辅助定位，设置为false。
        //设置为true不一定会进行H5辅助定位，设置为true时只有H5定位失败后才会进行辅助定位
        webSettings.setGeolocationEnabled(true);
        //设置UserAgent
        if(Build.VERSION.SDK_INT >= 19) {
            /*对系统API在19以上的版本作了兼容。因为4.4以上系统在onPageFinished时再恢复图片加载时,
            如果存在多张图片引用的是相同的src时，会只有一个image标签得到加载，
            因而对于这样的系统我们就先直接加载。
            */
            mWebView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mWebView.getSettings().setLoadsImagesAutomatically(false);
        }
        String userAgent = webSettings.getUserAgentString();
        mWebView.getSettings().setUserAgentString(userAgent);
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        //设置定位的数据库路径
        mWebView.getSettings().setGeolocationDatabasePath(dir);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setHorizontalScrollbarOverlay(true);
    }
}
