package com.qdigo.deq.blesdk.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.AnimationSet;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.qdigo.deq.blesdk.R;
import com.qdigo.deq.blesdk.utils.LogUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Num1Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Num1Fragment extends SupportMapFragment implements AMapLocationListener, AMap.CancelableCallback, View.OnClickListener, AMap.OnMapLoadedListener, RouteSearch.OnRouteSearchListener {

    private final static String TAG = "Num1Fragment";
    private OnFragmentInteractionListener mListener;
    private MapView mapView;
    private AMap aMap;
    private Boolean firsttouch = true;
    private LatLng currentLatLng;
    private ImageButton location;
    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;
    private RouteSearch routeSearch;
    private WalkRouteResult mWalkRouteResultresult;

    public Num1Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_num1, container,false);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        location = (ImageButton) view.findViewById(R.id.btn_location_refresh);
        initMap();


        location.setOnClickListener(this);
        return view;
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
//            aMap.setMapType(AMap.MAP_TYPE_NAVI);
        }
        aMap.setOnMapLoadedListener(this);
        aMap.setOnMarkerClickListener(markerListener);
        routeSearch = new RouteSearch(getActivity());
        routeSearch.setRouteSearchListener(this);
    }

    AMap.OnMarkerClickListener markerListener = new AMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {

            marker.showInfoWindow();
            //路线导航
            LatLonPoint from = new LatLonPoint(currentLatLng.latitude, currentLatLng.longitude);
            LatLonPoint to = new LatLonPoint(marker.getPosition().latitude, marker.getPosition().longitude);
            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(from,to);
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
            routeSearch.calculateWalkRouteAsyn(query);
            return true;
        }
    };

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.search_center_ic));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
    }

    /**
     * 一次定位
     */
    private void initLocation() {
        LogUtils.i(TAG, "initLocation --- start");
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        //初始化定位参数
        mlocationClient = new AMapLocationClient(getActivity());
        mlocationClient.setLocationListener(this);
        //声明mLocationOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
//        mLocationOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setNeedAddress(true);//是否需要返回地址信息
        mLocationOption.setOnceLocation(true);//是否为一次定位
//        mLocationOption.setGpsFirst(true);//是否优先使用GPS进行定位
        mLocationOption.setHttpTimeOut(10000);//设置连接超时时间
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                amapLocation.getAoiName();//获取当前定位点的AOI信息
                //获取定位时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);
                currentLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                Log.i(TAG, "location ------------ end :");
                changeCamera();
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e(TAG, "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
            }
        }
    }

    private void changeCamera() {
        if (currentLatLng == null) return;
        Log.i(TAG,"currentLatLng latitude: "+currentLatLng.latitude+" longitude :"+currentLatLng.longitude);
        changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentLatLng, 18, 0, 0)), this);
        aMap.clear();
        //create
        LatLng xiecheng = new LatLng( 31.220798,121.351046);
        MarkerOptions xmarkerOptions = new MarkerOptions().position(xiecheng).icon(BitmapDescriptorFactory.fromResource(R.drawable.search_center_ic));
        Marker markerx = aMap.addMarker(xmarkerOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.search_center_ic)).snippet("my position").draggable(true);
        Marker marker = aMap.addMarker(markerOptions);
        AnimationSet animationSet = new AnimationSet(true);
        //scale
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1);
        long duration = 500L;
        scaleAnimation.setDuration(duration);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        animationSet.addAnimation(scaleAnimation);
        //alpha
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setDuration(duration);
        animationSet.addAnimation(alphaAnimation);

        marker.setAnimation(animationSet);
        marker.startAnimation();


    }

    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
        if(firsttouch){
            firsttouch = false;
            aMap.moveCamera(update);
        }else{
            aMap.animateCamera(update, 500, callback);
        }
//        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocalLatlng, 18));
    }

    /**
     * 地图动画效果完成回调方法
     */
    @Override
    public void onFinish() {
        Log.i(TAG,"地图动画效果结束");
    }

    /**
     * 地图动画效果终止回调方法
     */
    @Override
    public void onCancel() {
        Log.i(TAG,"地图动画效果终止回调方法");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_location_refresh:
                //启动定位
                mlocationClient.startLocation();
                break;
        }
    }

    @Override
    public void onMapLoaded() {
        initLocation();


    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
        Log.w(TAG,"onWalkRouteSearched rCode ="+rCode+",result = "+result.toString());
        if (rCode == 1000) {
            if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
                WalkPath walkPath = result.getPaths().get(0);
                //mile
                float distance = walkPath.getDistance();
                DecimalFormat df = new DecimalFormat("0.00");//格式化小数
                String dis = df.format(distance/1000);
                //muni
                long duration = walkPath.getDuration();
                String time = df.format(duration / 60);
                //
                List<WalkStep> steps = walkPath.getSteps();
                for (int i = 0; i < steps.size(); i++) {
                }

                Log.w(TAG,"distance = "+dis+" 公里,duration = "+time+" 分钟,steps ="+steps.size());
                mWalkRouteResultresult = result;
//                aMap.clear();// 清理地图上的所有覆盖物
                MyRouteStyle walkRouteOverlay = new MyRouteStyle(getActivity(), aMap, walkPath, result.getStartPos(), result.getTargetPos());
                walkRouteOverlay.setNodeIconVisibility(false);
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay.addToMap();
                walkRouteOverlay.zoomToSpan();
            } else {
                Log.i(TAG,"对不起，没有搜索到相关数据！");
            }
        } else if (rCode == 27) {
            Log.i(TAG,"搜索失败,请检查网络连接！");
        } else if (rCode == 32) {
            Log.i(TAG,"key验证无效！");
        } else {
            Log.i(TAG,"未知错误，请稍后重试!错误码为" + rCode);
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        changeCamera();
        mapView.onResume();
        changeCamera();
        Log.i(TAG,"onResume");
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        Log.i(TAG,"onPause");

    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        Log.i(TAG,"onSaveInstanceState");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG,"onDestroyView");
        mapView.onDestroy();
        if(mlocationClient!=null){
            mlocationClient.onDestroy();
        }
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }

}
