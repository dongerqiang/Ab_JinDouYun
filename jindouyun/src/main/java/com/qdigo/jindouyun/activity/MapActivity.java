package com.qdigo.jindouyun.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.View;

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
import com.qdigo.jindouyun.view.WaveView;

import butterknife.Bind;

public class MapActivity extends BaseActivity implements LocationSource, AMapLocationListener {
    //view
    @Bind(R.id.waveView)
    WaveView mWaveView;
    @Bind(R.id.mMapView)
    MapView mapView;

    private AMap aMap;
    private float zoomLv = 17;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    private OnLocationChangedListener mListener;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private static final String TAG = "MapActivity";
    private LatLng currentPosition;
    private boolean isFirst = false;


    //    private DiffuseView diffuseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map);
        setContentViewWithDefaultTitle(R.layout.activity_map,"车辆位置");
//        diffuseView = (DiffuseView) findViewById(R.id.diffuseView);
//        diffuseView.start();



        mapView.onCreate(savedInstanceState);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPosition == null) return;
                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentPosition, 18, 0, 0)),1500,null);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    private void init() {
        //init waveview
        mWaveView = (WaveView) findViewById(R.id.waveView);
        mWaveView.setDuration(5000);
        mWaveView.setStyle(Paint.Style.FILL);
        mWaveView.setColor(Color.parseColor("#FF0000"));
        mWaveView.setInterpolator(new LinearOutSlowInInterpolator());
        mWaveView.start();

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.stable_cluster_marker_one_select));// 设置小蓝点的图标
        aMap.setMyLocationStyle(myLocationStyle);

        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setScaleControlsEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setCompassEnabled(false);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        if(null != mLocationClient){
            mLocationClient.onDestroy();
        }
//        if(diffuseView !=null){
//            diffuseView.stop();
//            diffuseView =null;
//        }
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
                    aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentPosition, 16, 0, 0)),1500,null);
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
