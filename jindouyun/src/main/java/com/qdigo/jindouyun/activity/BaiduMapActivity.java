package com.qdigo.jindouyun.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.qdigo.jindouyun.BaseActivity;
import com.qdigo.jindouyun.R;
import com.qdigo.jindouyun.utils.BroadcastUtils;
import com.qdigo.jindouyun.utils.ParseDataUtils;

import butterknife.Bind;
import butterknife.OnClick;

import static com.qdigo.jindouyun.MyApplication.app;

public class BaiduMapActivity extends BaseActivity {
    @Bind(R.id.bmapView)
    MapView mMapView;
    private BDLocation mLocationPosition;

    @Override
    protected void initView() {

    }
    @Bind(R.id.running_time)
    TextView mRunningTime;
    @Bind(R.id.total_mile)
    TextView mTotalMile;
    @Bind(R.id.current_speed)
    TextView mCurrentSpeed;

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private   int accuracyCircleFillColor = Color.argb(0x90, 83, 126, 220);
    private   int accuracyCircleStrokeColor = Color.argb(0, 250, 180, 70);

    BaiduMap mBaiduMap;

    // UI相关
    boolean isFirstLoc = true; // 是否首次定位
    float currentZoom = 17f;
    @OnClick(R.id.iv_increase)
    public void increase(){
        float maxZoomLevel = mBaiduMap.getMaxZoomLevel();
        currentZoom +=1f;
        if(currentZoom>=maxZoomLevel){
            currentZoom =maxZoomLevel;
        }
        MapStatus mMapStatus = new MapStatus.Builder()
                .zoom(currentZoom)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
    }

    @OnClick(R.id.iv_decrease)
    public void decrease(){
        float minZoomLevel = mBaiduMap.getMinZoomLevel();
        currentZoom -=1f;
        if(currentZoom<=minZoomLevel){
            currentZoom =minZoomLevel;
        }
        MapStatus mMapStatus = new MapStatus.Builder()
                .zoom(currentZoom)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.animateMapStatus(mMapStatusUpdate);

    }
    boolean changeCamera = false;
    @OnClick(R.id.iv_position)
    public void position(){
        if(changeCamera){
            //移动视角
            if(mLocationPosition ==null )return;
            LatLng ll = new LatLng(mLocationPosition.getLatitude(),
                    mLocationPosition.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(currentZoom);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            changeCamera = false;
            if(mCurrentMode == LocationMode.COMPASS){
                mBaiduMap
                        .setMyLocationConfigeration(new MyLocationConfiguration(
                                LocationMode.NORMAL, true, mCurrentMarker));
                mCurrentMode =LocationMode.NORMAL;
            }
        }else{
            if(mCurrentMode == LocationMode.NORMAL ){
                mBaiduMap
                        .setMyLocationConfigeration(new MyLocationConfiguration(
                                LocationMode.COMPASS, true, mCurrentMarker));
                mCurrentMode =LocationMode.COMPASS;
            }else if(mCurrentMode == LocationMode.COMPASS){
                mBaiduMap
                        .setMyLocationConfigeration(new MyLocationConfiguration(
                                LocationMode.NORMAL, true, mCurrentMarker));
                mCurrentMode =LocationMode.NORMAL;
            }

        }

    }
    BleStateReceiver bleStateReceiver;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_map);
        mCurrentMode = LocationMode.NORMAL;
        registerReceiver(bleStateReceiver = new BaiduMapActivity.BleStateReceiver(), new IntentFilter(BroadcastUtils.MILEAGE_ACTION));



        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();


        mMapView.showZoomControls(false);
        MapStatus mMapStatus = new MapStatus.Builder()
                .zoom(currentZoom)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        mCurrentMode = LocationMode.NORMAL;
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 修改为自定义marker
        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker);
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker,
                        accuracyCircleFillColor, accuracyCircleStrokeColor));

        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        mBaiduMap.setOnMapStatusChangeListener(listener);
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        mBaiduMap.setCompassPosition(new android.graphics.Point(70,380));
        uiSettings.setCompassEnabled(true);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
        mLocClient.start();
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
    BaiduMap.OnMapStatusChangeListener listener = new BaiduMap.OnMapStatusChangeListener() {
        /**
         * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
         * @param status 地图状态改变开始时的地图状态
         */
        public void onMapStatusChangeStart(MapStatus status){
            changeCamera = true;
        }
        /**
         * 地图状态变化中
         * @param status 当前地图状态
         */
        public void onMapStatusChange(MapStatus status){

        }
        /**
         * 地图状态改变结束
         * @param status 地图状态改变结束后的地图状态
         */
        public void onMapStatusChangeFinish(MapStatus status){
        }
    };

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mLocationPosition = location;
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(currentZoom);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
        unregisterReceiver(bleStateReceiver);
    }
}
