package com.qdigo.jindouyun.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.qdigo.jindouyun.BaseActivity;
import com.qdigo.jindouyun.R;
import com.qdigo.jindouyun.utils.BroadcastUtils;
import com.qdigo.jindouyun.utils.ParseDataUtils;

import butterknife.Bind;
import butterknife.OnClick;

import static com.amap.api.maps.AMap.LOCATION_TYPE_LOCATE;
import static com.amap.api.maps.AMap.LOCATION_TYPE_MAP_ROTATE;
import static com.qdigo.jindouyun.MyApplication.app;

public class MapActivity extends BaseActivity implements LocationSource, AMapLocationListener {
    //view
    @Bind(R.id.mMapView)
    MapView mapView;
    @Bind(R.id.running_time)
    TextView mRunningTime;
    @Bind(R.id.total_mile)
    TextView mTotalMile;
    @Bind(R.id.current_speed)
    TextView mCurrentSpeed;
    private int currentMode;

    @OnClick(R.id.iv_increase)
    public void increase(){
        zoomLv = aMap.getCameraPosition().zoom;
        zoomLv += 1.2f;

        if (zoomLv > aMap.getMaxZoomLevel()) {
            zoomLv = aMap.getMaxZoomLevel();
        }
        Log.w(TAG,"MAX LEVEL == "+aMap.getMaxZoomLevel());
        aMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLv),300,null);
    }

    @OnClick(R.id.iv_decrease)
    public void decrease(){
        zoomLv = aMap.getCameraPosition().zoom;
        zoomLv -= 1.2f;
        if (zoomLv < aMap.getMinZoomLevel()) {
            zoomLv = aMap.getMinZoomLevel();
        }
        aMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLv),300,null);
    }
    boolean changeCamera = false;
    boolean typeLocal = false;
    @OnClick(R.id.iv_position)
    public void position(){
//        startPostion();
        if(changeCamera){
            //移动视角
            if(currentPosition == null) return;
            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentPosition, 18, 0, 0)),500,null);
            aMap.setMyLocationType(LOCATION_TYPE_LOCATE);
            currentMode =LOCATION_TYPE_LOCATE;
            changeCamera = false;
        }else{
            //没移动视角
            if(currentMode == LOCATION_TYPE_LOCATE){
                //设置旋转
                aMap.setMyLocationType(LOCATION_TYPE_MAP_ROTATE);
                currentMode =LOCATION_TYPE_MAP_ROTATE;
            }else if(currentMode == AMap.LOCATION_TYPE_MAP_ROTATE){
                aMap.setMyLocationType(LOCATION_TYPE_LOCATE);
                currentMode =LOCATION_TYPE_LOCATE;
            }

        }

    }
    private AMap aMap;
    private float zoomLv = 16;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    private OnLocationChangedListener mListener;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private static final String TAG = "MapActivity";
    private LatLng currentPosition;
    private boolean isFirst = false;


    //    private DiffuseView diffuseView;
    BleStateReceiver bleStateReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_map,"车辆位置");
        registerReceiver(bleStateReceiver = new BleStateReceiver(), new IntentFilter(BroadcastUtils.MILEAGE_ACTION));



        mapView.onCreate(savedInstanceState);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    }

    class BleStateReceiver extends BroadcastReceiver {
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
    private void init() {

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.argb(0, 250, 180, 70));// 设置圆形的边框颜色
        myLocationStyle.strokeWidth(5f);
        myLocationStyle.radiusFillColor(Color.argb(0x90, 83, 126, 220));// 设置圆形的填充颜色
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setScaleControlsEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setCompassEnabled(false);
        aMap.getUiSettings().setCompassEnabled(true);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(LOCATION_TYPE_LOCATE);
        currentMode = LOCATION_TYPE_LOCATE;
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener(){
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                changeCamera =true;
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        if(null != mLocationClient){
            mLocationClient.onDestroy();
        }
        unregisterReceiver(bleStateReceiver);
    }

    @Override
    protected void initView() {
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
//        if(diffuseView !=null){
//            diffuseView.start();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
//        if(diffuseView !=null){
//            diffuseView.stop();
//        }
        deactivate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 在activity执行onSaveInstanceState时执行mapView.onSaveInstanceState
        // (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }

    public void startPostion() {
        Log.w(TAG,"startPostion");
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);

            //获取一次定位结果：
            //该方法默认为false。
            mLocationOption.setOnceLocation(false);


			mLocationOption.setInterval(3000);

            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }


    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

        Log.w(TAG,"onLocationChanged  mListener "+mListener);
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                currentPosition = new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude());
                Projection projection = aMap.getProjection();
                //将地图的中心点，转换为屏幕上的点
                Point center = projection.toScreenLocation(currentPosition);
                if(!isFirst){
                    isFirst = true;
                    aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentPosition, 18, 0, 0)),500,null);
                }

            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        Log.w(TAG,"activate");
        mListener = listener;
        startPostion();
    }


    @Override
    public void deactivate() {
        Log.w(TAG,"deactivate");
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

}
