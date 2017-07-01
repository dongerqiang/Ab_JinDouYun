package com.qdigo.jindouyun.fragment;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qdigo.jindouyun.R;
import com.qdigo.jindouyun.activity.BaiduMapActivity;
import com.qdigo.jindouyun.activity.MainActivity;
import com.qdigo.jindouyun.activity.MapActivity;
import com.qdigo.jindouyun.utils.DialogCallback;
import com.qdigo.jindouyun.utils.DialogUtils;
import com.qdigo.jindouyun.utils.ParseDataUtils;
import com.qdigo.jindouyun.view.CompassView;
import com.xw.repo.BubbleSeekBar;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import static com.qdigo.jindouyun.MyApplication.app;
import static com.qdigo.jindouyun.activity.MainActivity.TAG;

public class RideFragment extends Fragment implements View.OnClickListener,SensorEventListener {

    private OnFragmentInteractionListener mListener;
//    private SeekBar progress;
    private TextView problem;
    private TextView bikespeed;
    private TextView connectout;
    private ImageView map;
    private TextView mileTV;
    private TextView timeTV;
    private LinearLayout connetTips;
//    private Spinner spinner;
    private TextView dangwei3;
    private TextView dangwei2;
    private TextView dangwei1;
    private ImageView compass;
    private CheckBox screenOn;
    private BubbleSeekBar progress;
    private TextView danWei;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.qixing_fragment, container, false);
        bikespeed = (TextView) inflate.findViewById(R.id.bike_speed);
        connectout = (TextView) inflate.findViewById(R.id.connect_out);
        connetTips = (LinearLayout) inflate.findViewById(R.id.connet_tips);
        mCompassView = (CompassView) inflate.findViewById(R.id.compass);
        mileTV = (TextView) inflate.findViewById(R.id.mile);
        timeTV = (TextView) inflate.findViewById(R.id.time);
        progress = (BubbleSeekBar) inflate.findViewById(R.id.progress);
        danWei = (TextView) inflate.findViewById(R.id.tv_danwei);
        problem = (TextView) inflate.findViewById(R.id.problem);
//        spinner = (Spinner)inflate.findViewById(R.id.spinner);
        map = (ImageView) inflate.findViewById(R.id.map);
//        dangwei3 = (TextView) inflate.findViewById(R.id.tv_dangwei3);
//        dangwei2 = (TextView) inflate.findViewById(R.id.tv_dangwei2);
        dangwei1 = (TextView) inflate.findViewById(R.id.tv_dangwei1);
        screenOn = (CheckBox)inflate.findViewById(R.id.screenOn);
        MainActivity activity = (MainActivity) getActivity();

        processData(activity.mile,activity.time,activity.error,activity.speed,activity.dangwei);
        connectNotify(((MainActivity)(getActivity())).connect);

        initListener();
//        String sensitive = app.deviceNotes.opeMotorDang(false, "低");
//        int vibrationLevel = 1;
//        if("低".equals(sensitive)){
//            vibrationLevel = 1;
//        }else if("中".equals(sensitive)){
//            vibrationLevel = 2;
//        }else if("高".equals(sensitive)){
//            vibrationLevel = 3;
//        }
        dangwei1.setText("1档");
        return inflate;
    }
    boolean isOn =false;
    private void initListener() {
        initSensor();
        screenOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isOn = true;
                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }else{
                    isOn = false;
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        });
        map.setOnClickListener(this);
        dangwei1.setOnClickListener(this);
        /*dangwei2.setOnClickListener(this);
        dangwei3.setOnClickListener(this);*/
        progress.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {
                app.ble.setSensity(progress);
                dangwei1.setText(progress+"档");
            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });
       /* progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                app.ble.setSensity(seekBar.getProgress()+1);
                dangwei1.setText(seekBar.getProgress()+1+"档");
            }
        });*/
        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                String[] control = getResources().getStringArray(R.array.bike_control);
//              Toast.makeText(getActivity(), "你点击的是:"+control[pos], Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).controlBike(pos);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });*/



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.map:
//                startActivity(new Intent(getActivity(), RideLineActivity.class));
//                startUpApplication("com.autonavi.minimap");
//

//                startActivity(new Intent((getActivity()), MulMapActivity.class));

                Dialog passDialog = DialogUtils.createChangeMapDialog(getActivity(), new DialogCallback() {
                    @Override
                    public void confirm() {
                        super.confirm();
                        //baidu
//                        startUpApplication("com.baidu.BaiduMap","没有安装百度地图");
                        startActivity(new Intent(getActivity(), BaiduMapActivity.class));
                    }
                    @Override
                    public void camareClick() {
                        super.camareClick();
                        //gaode
                        startActivity(new Intent(getActivity(), MapActivity.class));
                    }
                });

                passDialog.show();

                break;
            default:
                break;
        }
    }

    /**
     * <功能描述> 启动应用程序
     *
     * @return void [返回类型说明]
     */
    @SuppressWarnings("deprecation")
    private void startUpApplication(String pkg,String error) {
        PackageManager packageManager = getActivity().getPackageManager();
        PackageInfo packageInfo = null;
        try {
            // 获取指定包名的应用程序的PackageInfo实例
            packageInfo = packageManager.getPackageInfo(pkg, 0);
        } catch (PackageManager.NameNotFoundException e) {
            // 未找到指定包名的应用程序
            e.printStackTrace();
            // 提示没有GPS Test Plus应用
            Toast.makeText(getActivity(),
                    error,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (packageInfo != null) {
            // 已安装应用
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(packageInfo.packageName);
            List<ResolveInfo> apps = packageManager.queryIntentActivities(
                    resolveIntent, 0);
            ResolveInfo ri = null;
            try {
                ri = apps.iterator().next();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (ri != null) {
                // 获取应用程序对应的启动Activity类名
                String className = ri.activityInfo.name;
                // 启动应用程序对应的Activity
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName componentName = new ComponentName(pkg, className);
                intent.setComponent(componentName);
                getActivity().startActivity(intent);

               /* Intent intent = null;
                try {
                    //intent = Intent.getIntent("androidamap://navi?sourceApplication=筋斗云&poiname=我的目的地&lat="+31.235301+"&lon="+121.481139+"&dev=0");
                    intent = Intent.getIntent("androidamap://route?sourceApplication=筋斗云&sname=我的位置&dlat="+31.235301+"&dlon="+121.481139+"&dname="+"故障车辆位置"+"&dev=0&m=0&t=1");

                    getActivity().startActivity(intent);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }*/


            }
        }
    }
    /*@SuppressWarnings("deprecation")
    public void setDangwei(int dangwei){
        dangwei1.setBackgroundColor(getResources().getColor(R.color.white));
        dangwei2.setBackgroundColor(getResources().getColor(R.color.white));
        dangwei3.setBackgroundColor(getResources().getColor(R.color.white));
        switch (dangwei){
            case 1:
                dangwei1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                break;
            case 2:
                dangwei2.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                break;
            case 3:
                dangwei3.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                break;
            default:
                break;
        }
    }*/
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

    public void processData(String mile,String time,String error,String speed,int dangwei){
        if(TextUtils.isEmpty(mile)){
            mile = 0+"";
        }
        if(TextUtils.isEmpty(time)){
            time ="00:00:00";
        }
        if(TextUtils.isEmpty(speed)){
            speed = "00.00";
        }

        if(TextUtils.isEmpty(error)){
            error =  "error";
        }
        problem.setText(error);
//        timeTV.setText(time);
        String km = app.deviceNotes.speedDanWei(false, "km");
        if(km.equalsIgnoreCase("mi")){
           mile = ParseDataUtils.kmToMi(mile);
           speed=ParseDataUtils.kmToMi(speed);
        }else{

        }
        mileTV.setText(mile+" "+km );
        bikespeed.setText(speed);
        danWei.setText(km+"/h");
        timeTV.setText(time);
    }

    @SuppressWarnings("deprecation")
    public void connectNotify(boolean connect){
        if(connect){
//            connetTips.setBackgroundResource(R.color.colorAccent);
            connectout.setText("连接成功！");
//            spinner.setVisibility(View.VISIBLE);
            /*timeTV.setBase(SystemClock.elapsedRealtime());//计时器清零
            int hour = (int) ((SystemClock.elapsedRealtime() - timeTV.getBase()) / 1000 / 60);
            timeTV.setFormat("0"+String.valueOf(hour)+":%s");*/

        }else{
//            connetTips.setBackgroundResource(R.color.colorPrimary);
            connectout.setText("连接失败");
//            spinner.setVisibility(View.GONE);
        }
    }

    protected CompassView mCompassView;

    private SensorManager mSensorManager;
    private Sensor mMagneticSensor;
    private Sensor mAccelerateSensor;

    private boolean mHasNeededSensors = false;

    float[] mAccelerometerValues = new float[3];
    float[] mMagneticFieldValues = new float[3];

    public void initSensor(){
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null &&
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mAccelerateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mHasNeededSensors = true;
        } else {
            Toast.makeText(getActivity(), "没有磁力计或加速度计", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mMagneticFieldValues = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometerValues = event.values;
        }
        Log.w(TAG,"onSensorChanged  mAccelerometerValues == "+mAccelerometerValues+"\n"
                +"mMagneticFieldValues == "+mMagneticFieldValues);
        calculateOrientation();
    }

    @Override
    public void onResume() {
        super.onResume();
        String dan = app.deviceNotes.speedDanWei(false, "km");
        String dangwei = danWei.getText().toString().trim();
        String speed = bikespeed.getText().toString().trim();
        String[] split = mileTV.getText().toString().trim().split(" ");

        if(dangwei.equalsIgnoreCase(dan)){

        }else{
            if(dan.equalsIgnoreCase("mi")&&dangwei.equalsIgnoreCase("km")){
                bikespeed.setText(" "+ParseDataUtils.kmToMi(speed));
            }
            if(dan.equalsIgnoreCase("km")&& dangwei.equalsIgnoreCase("mi")){
                bikespeed.setText(" "+ParseDataUtils.miToKm(speed));
            }
        }

        if(dan.equalsIgnoreCase(split[1])){

        }else{
            if(dan.equalsIgnoreCase("mi")&&split[1].equalsIgnoreCase("km")){
                mileTV.setText(ParseDataUtils.kmToMi(split[0])+" "+dan);
            }
            if(dan.equalsIgnoreCase("km")&& split[1].equalsIgnoreCase("mi")){
                mileTV.setText(ParseDataUtils.miToKm(split[0])+" "+dan);
            }
        }

        danWei.setText(dan+"/h");
        if (mHasNeededSensors) {
            mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mAccelerateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mHasNeededSensors) {
            mSensorManager.unregisterListener(this);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.w(TAG,"onAccuracyChanged  accuracy == "+accuracy);
    }

    float currentSeta = 0;
    // 计算指南针的南向和手机x轴的角度，以弧度表示（-PI, PI]
    private void calculateOrientation() {
        float[] results = new float[3];
        float[] rotates = new float[9];

        SensorManager.getRotationMatrix(rotates, null, mAccelerometerValues, mMagneticFieldValues);
        SensorManager.getOrientation(rotates, results);

        // alpha是南向和手机Y轴的夹角
        float alpha = results[0];
        float seta;

        // 将alpha转换成南向和手机X轴的夹角，便于使用极坐标系绘制指南针的圆盘
        if ((alpha - (-Math.PI)) < 0.000000001) {
            seta = (float) (Math.PI / 2);
        } else {
            seta = (float) (alpha - Math.PI / 2);
        }

        Log.w(TAG,"compass:"+Math.toDegrees(seta)+" \nchange seta ="+Math.abs(seta-currentSeta));
        if(Math.abs(seta-currentSeta)>0.08) {
            mCompassView.setSouth(seta);
            currentSeta = seta;
            mCompassView.invalidate();
        }
    }

    @SuppressWarnings("deprecation")
    void setUpBaiduAPPByMine(){
        try {
            Intent intent = Intent.getIntent("intent://map/direction?origin=我的位置&destination=latlng:"+31.235301+","+121.481139+"|name:故障车辆位置&mode=driving&src=com.qdigo.jindouyun|筋斗云#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            if(isInstallByread("com.baidu.BaiduMap")){
                startActivity(intent);
                Log.e(TAG, "百度地图客户端已经安装") ;
            }else {
                Log.e(TAG, "没有安装百度地图客户端") ;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否安装目标应用
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }
}
