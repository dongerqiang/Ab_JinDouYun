package com.qdigo.jindouyun.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.qdigo.jindouyun.BaseActivity;
import com.qdigo.jindouyun.R;
import com.qdigo.jindouyun.utils.BroadcastUtils;
import com.qdigo.jindouyun.utils.DialogCallback;
import com.qdigo.jindouyun.utils.DialogUtils;
import com.qdigo.jindouyun.utils.GPSUtils;
import com.qdigo.jindouyun.utils.ParseDataUtils;

import butterknife.Bind;
import butterknife.OnClick;

import static com.qdigo.jindouyun.MyApplication.app;

@SuppressWarnings("deprecation")
@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
public class RideLineActivity extends BaseActivity {

    @Bind(R.id.mWebView)
    WebView mWebView;
    @Bind(R.id.running_time)
    TextView mRunningTime;
    @Bind(R.id.total_mile)
    TextView mTotalMile;
    @Bind(R.id.current_speed)
    TextView mCurrentSpeed;


    int currentMap = 1;
    CommonWebClient commonWebClient ;
    CommonWebChromeWebClient commonWebChromeWebClient;
    private String currentPosition;

    @OnClick(R.id.iv_changeMap)
    public void changeMap(){
        Dialog passDialog = DialogUtils.createChangeMapDialog(this, new DialogCallback() {
            @Override
            public void confirm() {
                super.confirm();
                //baidup
                if(currentMap !=1){
                    mWebView.loadUrl("file:///android_asset/"+"baiduh5.html");
                    currentMap = 1;
                }

                /*if(!TextUtils.isEmpty(currentPosition)){
                    final  String[] split = currentPosition.split(",");
                    new Handler().postDelayed(new Runnable(){
                        public void run() {
                            //execute the task

                            double[] doubles = GPSUtils.gcj02_To_Bd09(Double.parseDouble(split[1]), Double.parseDouble(split[0]));
                            String ttmessage = doubles[1]+","+doubles[0];
                            mWebView.loadUrl("javascript:moveLocation('"+ttmessage+"')");
                        }
                    },0);

                }*/
                position();
                currentMap =1;
            }

            @Override
            public void camareClick() {
                super.camareClick();
                if(currentMap !=2){
                    mWebView.loadUrl("file:///android_asset/"+"gaodeMap.html");
                }
                currentMap = 2;
            }
        });
        passDialog.show();
    }
    AMapLocationClient locationClientSingle;
    @OnClick(R.id.iv_position)
    public void position(){
        if(currentMap ==1){

            if(!TextUtils.isEmpty(currentPosition)){
                String[] split = currentPosition.split(",");
                double[] doubles = GPSUtils.gcj02_To_Bd09(Double.parseDouble(split[1]), Double.parseDouble(split[0]));
                String ttmessage = doubles[1]+","+doubles[0];
                mWebView.loadUrl("javascript:moveLocation('"+ttmessage+"')");
            }else{
                mWebView.loadUrl("javascript:moveCamera()");
            }
        }else{

                mWebView.loadUrl("javascript:moveCamera()");
        }
    }
    @OnClick(R.id.iv_decrease)
    public void decreaseMap(){
        if(currentMap == 1){
            mWebView.loadUrl("javascript:decreaseMap()");
        }else{
            mWebView.loadUrl("javascript:decreaseMap()");
        }
    }
    @OnClick(R.id.iv_increase)
    public void increaseMap(){
        if(currentMap == 1){
            mWebView.loadUrl("javascript:increaseMap()");
        }else{
            mWebView.loadUrl("javascript:increaseMap()");
        }
    }

    BleStateReceiver bleStateReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏透明
//        initState();
        setContentView(R.layout.activity_ride_line);
        registerReceiver(bleStateReceiver = new BleStateReceiver(), new IntentFilter(BroadcastUtils.MILEAGE_ACTION));
       // registerReceiver(bleConectReceiver = new BleConnectReceiver(), new IntentFilter(BroadcastUtils.BLE_CONNECT_STATE));

    }

    class BleStateReceiver extends BroadcastReceiver {
        float currentmile = 0f;
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if(intent.getAction().equals(BroadcastUtils.BLE_CONNECT_STATE)){
                int state = intent.getIntExtra(BroadcastUtils.KEY_BLE_STATE, 0);
                if(state ==1){
                    app.showToast("已连接");
                }else{
                    app.showToast("连接失败");
                }
            }else if(intent.getAction().equals(BroadcastUtils.MILEAGE_ACTION)){
                if(intent.hasExtra(BroadcastUtils.MILEAGE_VALUE_KEY)){
                    //总里程
                    String km = intent.getStringExtra(BroadcastUtils.MILEAGE_VALUE_KEY);
                    String kmDanwei = app.deviceNotes.speedDanWei(false, "km");
                    if("km".equalsIgnoreCase(kmDanwei)){
                        mTotalMile.setText(km);
                    }else{
                        String miToKm = ParseDataUtils.kmToMi(km);
                        mTotalMile.setText(miToKm);
                    }
                } if(intent.hasExtra(BroadcastUtils.SPEED_VALUE_KEY)){
                    //速度
                    String speeddKm = intent.getStringExtra(BroadcastUtils.SPEED_VALUE_KEY);
                    String kmDanwei = app.deviceNotes.speedDanWei(false, "km");
                    if("km".equalsIgnoreCase(kmDanwei)){
                        mCurrentSpeed.setText(speeddKm);
                    }else{
                        String miToKm = ParseDataUtils.kmToMi(speeddKm);
                        mCurrentSpeed.setText(miToKm);
                    }
                }else if(intent.hasExtra(BroadcastUtils.RUNNING_TIME_KEY)){
                    //runningtime
                    String runningtime = intent.getStringExtra(BroadcastUtils.RUNNING_TIME_KEY);
                    mRunningTime.setText(runningtime);
                }
            }
        }

    }
    private void initState() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);//calculateStatusColor(Color.WHITE, (int) alphaValue)
        }
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void initView() {
        initSetting();
        /*if(webClient == null){
            webClient = new WebClient();
        }
        if(javaScriptInterface == null){
            javaScriptInterface = new JavaScriptInterface();
        }
        mWebView.setWebViewClient(webClient);
        mWebView.addJavascriptInterface(javaScriptInterface, "load");*/
        startAssistLocation();
        if(commonWebClient == null){
            commonWebClient = new CommonWebClient();
        }
        if(commonWebChromeWebClient == null){
            commonWebChromeWebClient = new CommonWebChromeWebClient();
        }
        mWebView.loadUrl("file:///android_asset/"+"baiduh5.html");
        mWebView.setWebViewClient(commonWebClient);
//        mWebView.setWebChromeClient(commonWebChromeWebClient,"test");
        mWebView.addJavascriptInterface(commonWebChromeWebClient, "test");
        currentMap =2;

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



    class CommonWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (!mWebView.getSettings().getLoadsImagesAutomatically()) {
                mWebView.getSettings().setLoadsImagesAutomatically(true);
            }

        }

    }
    class CommonWebChromeWebClient extends WebChromeClient {
        // 处理javascript中的alert
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {
            return true;
        };

        // 处理javascript中的confirm
        public boolean onJsConfirm(WebView view, String url,
                                   String message, final JsResult result) {
            return true;
        };

        // 处理定位权限请求
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, true);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
        @Override
        // 设置网页加载的进度条
        public void onProgressChanged(WebView view, int newProgress) {
            RideLineActivity.this.getWindow().setFeatureInt(
                    Window.FEATURE_PROGRESS, newProgress * 100);
            super.onProgressChanged(view, newProgress);
        }

        // 设置应用程序的标题title
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        @JavascriptInterface
        public void locationFinish(String message){
//            Toast.makeText(RideLineActivity.this,"locationFinish-----"+message,Toast.LENGTH_SHORT).show();
            currentPosition = message;
        }

        @JavascriptInterface
        public void locationError(String message){
//            Toast.makeText(RideLineActivity.this,"locationError-----"+message,Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void Toast(String latitude,String longtitude){
            Toast.makeText(RideLineActivity.this,"latitude-----"+latitude+"\nlongtitude------",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 启动H5辅助定位
     */
    void startAssistLocation(){
        if(null == locationClientSingle){
            locationClientSingle = new AMapLocationClient(this.getApplicationContext());
        }
        locationClientSingle.startAssistantLocation();

        Toast.makeText(RideLineActivity.this, "正在定位...", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bleStateReceiver);

        if (null != locationClientSingle) {
            locationClientSingle.stopAssistantLocation();
            locationClientSingle.onDestroy();
        }
        if(null != mWebView){
            mWebView.destroy();
        }
    }



}
