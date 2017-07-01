package com.qdigo.jindouyun.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.qdigo.jindouyun.BaseActivity;
import com.qdigo.jindouyun.R;
import com.qdigo.jindouyun.blesdkhelp.BleInterface;
import com.qdigo.jindouyun.blesdkhelp.BleSdkUtils;
import com.qdigo.jindouyun.blesdkhelp.DeviceAdapter;
import com.qdigo.jindouyun.blesdkhelp.DeviceDB;
import com.qdigo.jindouyun.fragment.MineFragment;
import com.qdigo.jindouyun.fragment.MoreFragment;
import com.qdigo.jindouyun.fragment.RideFragment;
import com.qdigo.jindouyun.utils.BroadcastUtils;
import com.qdigo.jindouyun.utils.DialogUtils;
import com.qdigo.jindouyun.utils.ParseDataUtils;
import com.qdigo.jindouyun.view.CustomDialog;
import com.xiaofu_yan.blux.blue_guard.BlueGuard;
import com.xiaofu_yan.blux.smart_bike.SmartBike;
import com.xiaofu_yan.blux.smart_bike.SmartBikeManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;

import static com.qdigo.jindouyun.MyApplication.app;
import static com.qdigo.jindouyun.R.id.problem;

public class MainActivity extends BaseActivity implements View.OnClickListener,MineFragment.OnFragmentInteractionListener,MoreFragment.OnFragmentInteractionListener,RideFragment.OnFragmentInteractionListener{

    //view
    @Bind(R.id.ride)
    AppCompatRadioButton mRide;
    @Bind(R.id.mine)
    AppCompatRadioButton mMine;
    @Bind(R.id.more)
    AppCompatRadioButton mMore;

    private RideFragment mRideFragment;
    private MineFragment mMineFragment;
    private MoreFragment mMoreFragment;

    public DeviceDB.Record mCurrentDevice = null;
    public static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    private Dialog directLoading;
    private boolean isKeyDirect;
    private DeviceAdapter adapter;
    private Timer timer;
    private List<DeviceDB.Record> mDevices = new ArrayList<>();
    private SwipeMenuListView mListView;
    private Dialog pairLoading;
    private CustomDialog cusdialog;
    private String pairResult;
    private Dialog dialog;
    private Timer pairTimer;
    public boolean connect = false;
    public BleSdkUtils mBleSdkUtils ;
    public String mile;
    public String time;
    public String error;
    public String speed;
    public int dangwei;
    private Timer directTimer;
    private boolean keyDirect;
    private boolean isScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentViewWithDefaultTitle(R.layout.activity_main,"筋斗云");
        FragmentManager fManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            mRideFragment = (RideFragment) fManager.findFragmentByTag("tab1");
            mMineFragment = (MineFragment) fManager.findFragmentByTag("tab2");
            mMoreFragment = (MoreFragment) fManager.findFragmentByTag("tab3");
        }
//        back = (ImageView)findViewById(R.id.backimg);
//        title = (TextView)findViewById(R.id.titleTv);

        mRide = (AppCompatRadioButton)findViewById(R.id.ride);
        mMine = (AppCompatRadioButton)findViewById(R.id.mine);
        mMore = (AppCompatRadioButton)findViewById(R.id.more);

        initListener();

        //初始显示ride
        setBottom(1);
        Log.w(TAG,"ACTIVITY ONCREATE");
//        mBleSdkUtils = BleSdkUtils.getInstance();
//        mBleSdkUtils.initBleSdk(this);
//        initBluetooth();
        mRide.setChecked(true);
        app.ble.initBle(this);
        openBluetooth();

    }
    BleStateReceiver bleStateReceiver;
    BleConnectReceiver bleConectReceiver;
    private void initListener() {
//        back.setOnClickListener(this);
        mRide.setOnClickListener(this);
        mMine.setOnClickListener(this);
        mMore.setOnClickListener(this);
        registerReceiver(bleStateReceiver = new BleStateReceiver(), new IntentFilter(BroadcastUtils.MILEAGE_ACTION));
        registerReceiver(bleConectReceiver = new BleConnectReceiver(), new IntentFilter(BroadcastUtils.BLE_CONNECT_STATE));

    }
    class BleStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if(intent.getAction().equals(BroadcastUtils.BLE_CONNECT_STATE)){
                int state = intent.getIntExtra(BroadcastUtils.KEY_BLE_STATE, 0);
                if(state ==1){
                    app.showToast("已连接");
                    broadFragment(true);
                }else{
                    app.showToast("连接失败");
                    broadFragment(false);
                }
            }else if(intent.getAction().equals(BroadcastUtils.MILEAGE_ACTION)){
                /*if(intent.hasExtra(BroadcastUtils.MILEAGE_VALUE_KEY)){
                    String km = intent.getStringExtra(BroadcastUtils.MILEAGE_VALUE_KEY);
                    mile = km;
                }*/ if(intent.hasExtra(BroadcastUtils.SPEED_VALUE_KEY)){
                    String speeddKm = intent.getStringExtra(BroadcastUtils.SPEED_VALUE_KEY);
                    speed=speeddKm;

                }else if(intent.hasExtra(BroadcastUtils.DANGWEI_KEY)){
                    int dangWei = intent.getIntExtra(BroadcastUtils.DANGWEI_KEY,1);
                    dangwei =dangWei;
                }else if(intent.hasExtra(BroadcastUtils.ARS_CODE_KEY)){
                    String ars = intent.getStringExtra(BroadcastUtils.ARS_CODE_KEY);
                    error = ars;
                }else if(intent.hasExtra(BroadcastUtils.MILEAGE_VALUE_INCREASE_KEY)){
                    String km = intent.getStringExtra(BroadcastUtils.MILEAGE_VALUE_INCREASE_KEY);
                    mile =km;
                }else if(intent.hasExtra(BroadcastUtils.RUNNING_TIME_KEY)){
                    //运行时间
                    String runningtime = intent.getStringExtra(BroadcastUtils.RUNNING_TIME_KEY);
                    time = runningtime;
                }
                broadFragment(true);
            }else if(intent.getAction().equals(BroadcastUtils.BLE_CONNECTED)){

            }else if(intent.getAction().equals(BroadcastUtils.BLE_DISCONNECT)){

            }
        }

    }

    class BleConnectReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context ctx, Intent intent) {
            if(intent.getAction().equals(BroadcastUtils.BLE_CONNECT_STATE)){
                int state = intent.getIntExtra(BroadcastUtils.KEY_BLE_STATE, 0);
                if(state ==1){
                    app.showToast("已连接");
                    broadFragment(true);
                }else{
                    app.showToast("连接失败");
                    broadFragment(false);
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
           /* case R.id.backimg:
                finish();
                break;*/
            case R.id.ride:
                setBottom(1);
                break;
            case R.id.mine:
                setBottom(2);
                break;
            case R.id.more:
                setBottom(3);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * 设置标题
     * @param mes
     */
    public void setTitle(String mes) {
//        title.setText(mes);
        mTvTitleName.setText(mes);
    }

    /**
     * 设置地表的显示
     * @param dex
     */
    public void setBottom(int dex){

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mRideFragment = (RideFragment)fm.findFragmentByTag("tab1");
        mMineFragment = (MineFragment)fm.findFragmentByTag("tab2");
        mMoreFragment  = (MoreFragment)fm.findFragmentByTag("tab3");
        if(mRideFragment !=null){
            ft.hide(mRideFragment);
        }
        if(mMineFragment !=null){
            ft.hide(mMineFragment);
        }
        if(mMoreFragment !=null){
            ft.hide(mMoreFragment);
        }


        switch (dex){
            case 1:
                setTitle("筋斗云");
                if(mRideFragment == null){
                    mRideFragment = new RideFragment();
                    ft.add(R.id.fragment_container,mRideFragment,"tab1").show(mRideFragment).commit();
                }else{
                    ft.show(mRideFragment).commit();
                }

                break;
            case 2:
                setTitle("车辆状态");
                if(mMineFragment ==null){
                    mMineFragment = new MineFragment();
                    ft.add(R.id.fragment_container,mMineFragment,"tab2").show(mMineFragment).commit();
                }else{
                    ft.show(mMineFragment).commit();
                }

                break;
            case 3:
                setTitle("更多");
                if(mMoreFragment ==null){
                    mMoreFragment = new MoreFragment();
                    ft.add(R.id.fragment_container,mMoreFragment,"tab3").show(mMoreFragment).commit();
                }else{
                    ft.show(mMoreFragment).commit();
                }
                break;
            default:
                break;

        }
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void initBluetooth(){

        keyDirect = getCurrentDeviceKey();

        SmartBikeManager smartBikeManager = mBleSdkUtils.getSmartBikeManager();
        Log.w(TAG,"smartManager = "+mBleSdkUtils.getSmartBikeManager());
        if(smartBikeManager == null){
            boolean isServerConnect = mBleSdkUtils.bindService(MainActivity.this, new BleSdkUtils.ServerConnectListerner() {
                @Override
                public void serverConnectted(SmartBikeManager smartBikeManager) {
                    Log.w(TAG,"serverConnectted ----");
                    openBluetooth();
                }

                @Override
                public void serverDisConnectted() {
                    Log.w(TAG,"serverDisConnectted ----");
                    broadFragment(false);
                }
            });

            if (!isServerConnect) {
                Toast.makeText(mContext, "服务绑定失败，请重试", Toast.LENGTH_SHORT).show();
                broadFragment(false);
            }
        }else{
            openBluetooth();
        }


    }

    @SuppressLint("NewApi")
    public void openBluetooth() {
        //判断设备是否支持ble
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "不支持BLE蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        // 初始化蓝牙适配器
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(mContext.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        // 确保蓝牙在手机上可以开启
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
//            scanBluetooth();
        }

    }

    @Override
    protected void onResume() {
        BleInterface.getInstance().binder(this);
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "开启蓝牙成功!", Toast.LENGTH_SHORT).show();
                // mBluetoothAdapter.enable(); 此方法不需要询问用户，直接开启 但需要admin的权限
//                scanBluetooth();
            } else {
                Toast.makeText(this, "开启蓝牙失败,请重试!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void scanBluetooth() {
        Log.w(TAG,"scanBluetooth");
        Log.w(TAG,"scanBluetooth mBleSdkUtils = "+mBleSdkUtils);
        Log.w(TAG,"scanBluetooth getConnection = "+mBleSdkUtils.getConnection());
        Log.w(TAG,"scanBluetooth getSmartBikeManager = "+mBleSdkUtils.getSmartBikeManager());
        Log.w(TAG,"scanBluetooth getSmartBike = "+mBleSdkUtils.getSmartBike());

        keyDirect = getCurrentDeviceKey();
        Log.w(TAG,"scanBluetooth mCurrentDevice = "+mCurrentDevice);
        //直连
        if (keyDirect) {
            //已在扫描 暂停
            if (mBleSdkUtils.isScanning()) {
                mBleSdkUtils.stopScanBleDevice();
            }
            if(directTimer == null){
                directTimer = new Timer();
            }

            directTimer.schedule(new DirectTask(), 10 * 1000);

            directLoading = DialogUtils.createLoadingDialog(MainActivity.this, "连接车辆中...");
            directLoading.show();
            isKeyDirect = true;
            Log.w(TAG,"scanBluetooth isKeyDirect = "+isKeyDirect +" \nkey = "+mCurrentDevice.key +
                    "\nidentifier == "+mCurrentDevice.identifier);
            SmartBike smartBike = mBleSdkUtils.getSmartBike();
            SmartBikeManager smartBikeManager = mBleSdkUtils.getSmartBikeManager();
            if(smartBike == null){

                mBleSdkUtils.connectDeviceByIdentifier(mCurrentDevice.identifier, new MyScanListener() {

                    @Override
                    public void getSmartBike(SmartBike smartBike) {
                        mBleSdkUtils.connectDeviceBykey(mCurrentDevice.key, new MySmartBikeListerner() {

                            @Override
                            public void smartBikeState(BlueGuard.State state) {
                                Log.w(TAG, "直连 state ：" + state);

                                if (state == BlueGuard.State.ARMED) {
                                    //布防
                                    // TODO: 2017-03-02
                                } else if (state == BlueGuard.State.RUNNING) {
                                    //行驶
                                    // TODO: 2017-03-02
                                } else if (state == BlueGuard.State.STARTED) {
                                    //上电
                                    // TODO: 2017-03-02
                                } else if (state == BlueGuard.State.STOPPED) {
                                    //撤防
                                    // TODO: 2017-03-02
                                }
                            }

                            @Override
                            public void smartBikeConnected() {
                                if(directTimer !=null){
                                    directTimer.cancel();
                                    directTimer = null;
                                }
                                if(directLoading !=null &&directLoading.isShowing()){
                                    directLoading.dismiss();
                                }
                                broadFragment(true);
                                Toast.makeText(mContext,"直连成功",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void smartBikeDisConnectted(BlueGuard.DisconnectReason reason) {
                                Log.w(TAG,"reason = "+reason);
                                if(directTimer !=null){
                                    directTimer.cancel();
                                    directTimer = null;
                                }
                                if(directLoading !=null &&directLoading.isShowing()){
                                    directLoading.dismiss();
                                }
                                broadFragment(false);
                                if(reason != BlueGuard.DisconnectReason.CLOSED){
                                    DeviceDB.deleteKey(mContext);
                                }
                                Toast.makeText(mContext,"直连失败",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }else{
                mBleSdkUtils.connectDeviceBykey(mCurrentDevice.key, new MySmartBikeListerner() {

                    @Override
                    public void smartBikeState(BlueGuard.State state) {
                        // TODO Auto-generated method stub
                        Log.w(TAG, "直连 state ：" + state);

                        if (state == BlueGuard.State.ARMED) {
                            //布防
                        } else if (state == BlueGuard.State.RUNNING) {
                            //行驶
                        } else if (state == BlueGuard.State.STARTED) {
                            //上电
                        } else if (state == BlueGuard.State.STOPPED) {
                            //撤防
                        }
                    }

                    @Override
                    public void smartBikeConnected() {
                        if(directTimer !=null){
                            directTimer.cancel();
                            directTimer = null;
                        }
                        if(directLoading !=null &&directLoading.isShowing()){
                            directLoading.dismiss();
                        }
                        broadFragment(true);
                        Toast.makeText(mContext,"直连成功",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void smartBikeDisConnectted(BlueGuard.DisconnectReason reason) {
                        Log.w(TAG,"reason = "+reason);
                        if(directTimer !=null){
                            directTimer.cancel();
                            directTimer = null;
                        }
                        if(directLoading !=null &&directLoading.isShowing()){
                            directLoading.dismiss();
                        }
                        broadFragment(false);
                        if(reason != BlueGuard.DisconnectReason.CLOSED){
                            DeviceDB.deleteKey(mContext);
                            Log.w(TAG," 直连 失败 DELETE KEY ");
                        }
                        Toast.makeText(mContext,"直连失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } else {
            //首次连接
            isKeyDirect = false;
            //扫描 配对连接
            showDeviceDialog();

            if (timer == null) {
                timer = new Timer();
            }
            timer.schedule(new ScanTask(), 30 * 1000);
            Log.w(TAG,"scanBluetooth isKeyDirect = "+isKeyDirect);
            mBleSdkUtils.scanBleDevice(new MyScanListener() {

                @Override
                public void foundSmartBike(String identifier, String name) {
                    Log.w(TAG,"device identifier:" + identifier + "  device name:" + name);

                    if (!TextUtils.isEmpty(identifier) && !TextUtils.isEmpty(name)) {
                        Log.w(TAG, "device identifier:" + identifier + "  device name:" + name);
//					            mBleSdkUtils.stopScanBleDevice();
//					            mBleSdkUtils.connectDeviceByIdentifier(identifier);
                        DeviceDB.Record rec = new DeviceDB.Record(name, identifier, null);
                        DeviceDB.saveScan(mContext,rec);
                        if (dialog != null && dialog.isShowing()) {
//			                Toast.makeText(mContext,"find new devices",Toast.LENGTH_SHORT).show();
                            dialog.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                        }
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        DeviceDB.saveScan(mContext ,rec);
                        if (adapter != null) {
                            adapter.addDevice(rec);
                        }

                    }
                }
            });

        }

    }

    protected void showDeviceDialog() {
        if (dialog != null) {
            if (mDevices.size() != 0) {
                dialog.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            } else {
                if(adapter.getCount() == 0){
                    dialog.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                }else{
                    dialog.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                }
            }
            dialog.show();
            return;
        }
        Log.w(TAG,"showDeviceDialog ---- dialog = "+dialog);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.device_list2, null);
        mListView =  (SwipeMenuListView) view.findViewById(R.id.paired_devices);
        initMenu();
        DeviceDB.Record localScanDevice = getCurrentDeviceScan();
        if (localScanDevice != null){
            boolean isHas = false;
            for (int i = 0 ;i<mDevices.size();i++){
                if(mDevices.get(i).identifier == localScanDevice.identifier ){
                    isHas = true;
                }
            }
            if(!isHas){
                mDevices.add(localScanDevice);
            }
        }
        adapter = new DeviceAdapter(mContext, mDevices);
        mListView.setAdapter(adapter);
        dialog =  new Dialog(mContext, R.style.dialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        dialog.show();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                mBleSdkUtils.stopScanBleDevice();//停止扫描
                mCurrentDevice = mDevices.get(position);
                DeviceDB.saveScan(mContext,mCurrentDevice);
                mBleSdkUtils.connectDeviceByIdentifier(mCurrentDevice.identifier, new MyScanListener() {

                    @Override
                    public void getSmartBike(SmartBike smartBike) {

                        if (!TextUtils.isEmpty(mCurrentDevice.key)) {
                            mBleSdkUtils.connectDeviceBykey(mCurrentDevice.key, new MySmartBikeListerner() {
                                @Override
                                public void smartBikeState(BlueGuard.State state) {
                                    Log.w(TAG, "Piar 连接 , state :" + state);
                                    if (state == BlueGuard.State.ARMED) {
                                        //布防
                                    } else if (state == BlueGuard.State.RUNNING) {
                                        //行驶
                                    } else if (state == BlueGuard.State.STARTED) {
                                        //上电
                                    } else if (state == BlueGuard.State.STOPPED) {
                                        //撤防
                                    }
                                }

                                @Override
                                public void smartBikeConnected() {
                                    Log.w(TAG, "showdialog -- MySmartBikeListerner, smartBikeConnected ");
                                }

                                @Override
                                public void smartBikeDisConnectted(BlueGuard.DisconnectReason reason) {
                                    Log.w(TAG, "showdialog -- MySmartBikeListerner, smartBikeDisConnectted , reason = " + reason);
                                    if(reason !=BlueGuard.DisconnectReason.CLOSED ){
                                        DeviceDB.deleteKey(mContext);
                                    }
                                }
                            });
                        } else {
                            Log.w(TAG, "pairDialog");
                            pairDialog();
                        }
                    }

                });
                dialog.dismiss();


            }
        });
    }

        private void initMenu() {
            SwipeMenuCreator creator = new SwipeMenuCreator() {

                @Override
                public void create(SwipeMenu menu) {

                    // create "delete" item
                    SwipeMenuItem deleteItem = new SwipeMenuItem(
                            mContext);
                    // set item background
                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                            0x3F, 0x25)));
                    // set item width
                    deleteItem.setWidth(dp2px(90));
                    // set a icon
                    deleteItem.setIcon(R.drawable.ic_delete);
                    // add to menu
                    menu.addMenuItem(deleteItem);
                }
            };
            // set creator
            mListView.setMenuCreator(creator);

            // step 2. listener item click event
            mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                    switch (index) {
                        case 0:
                            // delete
                            mDevices.remove(mDevices.get(position));
                            DeviceDB.deleteScan(mContext);
                            adapter.notifyDataSetChanged();
                            if (mDevices.size() == 0) {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                            break;

                    }
                    return false;
                }
            });
        }

    private void pairDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        cusdialog = builder.create();
        cusdialog.setClickListener(new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                pairLoading = DialogUtils.createLoadingDialog(MainActivity.this, "车辆配对中...");
                pairLoading.show();
                if (pairTimer == null) {
                    pairTimer = new Timer();
                }
                pairTimer.schedule(new PairTask(), 10*1000);
                String carCode = ((CustomDialog) dialog).getEdittext();
                mBleSdkUtils.stopScanBleDevice();
                if(TextUtils.isEmpty(carCode)){
                    Toast.makeText(MainActivity.this, "配对码为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (carCode.length() == 6) {
                    dialog.dismiss();
                    SmartBike smartBike = mBleSdkUtils.getSmartBike();
                    if(smartBike != null){
                        Log.w(TAG,"pairDialog ="+smartBike);
                    mBleSdkUtils.connectDeviceByPair(carCode, new MySmartBikeListerner() {


                        @Override
                        public void smartBikeState(BlueGuard.State state) {
                            // TODO Auto-generated method stub
                            Log.w(TAG, "pair 连接 , state = " + state);
                            if (state == BlueGuard.State.ARMED) {
                                //布防
                            } else if (state == BlueGuard.State.RUNNING) {
                                //行驶
                            } else if (state == BlueGuard.State.STARTED) {
                                //上电
                            } else if (state == BlueGuard.State.STOPPED) {
                                //撤防
                            }
                        }

                        @Override
                        public void smartBikePairResult(BlueGuard blueGuard, BlueGuard.PairResult result, String key) {
                            Log.w(TAG, "smartBikePairResult result:" + result + "  key:" + result);
                            pairLoading.dismiss();
                            if (pairTimer != null) {
                                pairTimer.cancel();
                                pairTimer = null;
                            }
                            if (result == BlueGuard.PairResult.SUCCESS) {
                                //save db
                                if (!TextUtils.isEmpty(key)) {
                                    DeviceDB.Record rec = new DeviceDB.Record(blueGuard.name(), blueGuard.identifier(), key);
                                    DeviceDB.saveKey(mContext, rec);
                                    Log.w(TAG, "小主连接车辆成功！");
                                } else {
                                    Log.w(TAG, "小主连接车辆成功,但是key == null");
                                    Toast.makeText(mContext, "pair 成功，但是key == null", Toast.LENGTH_SHORT).show();
                                }
                            } else if (result == BlueGuard.PairResult.ERROR_KEY) {
                                Log.w(TAG, "小主连接车辆失败，原因：ERROR_KEY ,delete data");
                                DeviceDB.deleteKey(mContext);
                                mDevices.clear();
                            } else if (result == BlueGuard.PairResult.ERROR_PERMISSION) {
                                Log.w(TAG, "小主连接车辆失败，原因：ERROR_PERMISSION , delete data");
                                DeviceDB.deleteKey(mContext);
                                mDevices.clear();
                                //pair show
                            } else if (result == BlueGuard.PairResult.ERROR) {
                                Log.w(TAG, "小主连接车辆失败，原因：ERROR");
//                                DeviceDB.deleteKey(mContext);
                            }

                        }

                        @Override
                        public void smartBikeConnected() {
                            Log.w(TAG,"配对成功 ： reason = ");
                            //pair success
                            broadFragment(true);
                            if (cusdialog != null && cusdialog.isShowing()) {
                                cusdialog.dismiss();
                            }
                            Toast.makeText(mContext,"配对成功",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void smartBikeDisConnectted(BlueGuard.DisconnectReason reason) {
                            //pair failed
                            Log.w(TAG,"配对失败 ： reason = "+reason);
                            broadFragment(false);
                            if (cusdialog != null && cusdialog.isShowing()) {
                                cusdialog.dismiss();
                            }

//                            if(reason != BlueGuard.DisconnectReason.CLOSED){
//                                DeviceDB.delete(mContext);
//                            }
                            Toast.makeText(mContext,"配对失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                    }
                } else {
                    pairLoading.dismiss();
                   /* if (cusdialog != null && cusdialog.isShowing()) {
                        cusdialog.dismiss();
                    }
                    if(pairTimer !=null){
                        pairTimer.cancel();
                        pairTimer = null;
                    }*/
                    Toast.makeText(MainActivity.this, "配对码错误", Toast.LENGTH_SHORT).show();
                    //close
                }


            }
        });
        cusdialog.setCanceledOnTouchOutside(false);
        cusdialog.setCancelable(false);
        cusdialog.show();

    }

    /**
     * 获取有没有直连成功的设备
     * @return
     */
    public boolean getCurrentDeviceKey() {
        boolean isEnable = false;
        DeviceDB.Record currentDevice = DeviceDB.loadKey(this);
        if (currentDevice != null) {
            if (!TextUtils.isEmpty(currentDevice.identifier) && !TextUtils.isEmpty(currentDevice.name) && !TextUtils.isEmpty(currentDevice.key)) {
//                mDevices.add(currentDevice);
                isEnable = true;
                mCurrentDevice = currentDevice;
            }
        }
        Log.w(TAG,"getCurrentDeviceKey = "+mCurrentDevice+", local has = "+isEnable);
        return isEnable;
    }

    public DeviceDB.Record getCurrentDeviceScan() {
        boolean isEnable = false;
        DeviceDB.Record scanDevice = DeviceDB.loadScan(this);
        if (scanDevice != null) {
            if (!TextUtils.isEmpty(scanDevice.identifier) && !TextUtils.isEmpty(scanDevice.name)) {
//                mDevices.add(currentDevice);
                isEnable = true;
            }else{
                isEnable = false;
                scanDevice = null;
            }
        }
        Log.w(TAG,"getCurrentDeviceScan = "+mCurrentDevice+", local has = "+isEnable);
        return scanDevice;
    }

    class MyScanListener implements BleSdkUtils.ScanSmartListener {

        @Override
        public void foundSmartBike(String identifier, String name) {
            Log.w(TAG," foundSmartBike ==== ");

        }

        @Override
        public void getSmartBike(SmartBike smartBike) {
            Log.w(TAG,"getSmartBike");

        }

    }

    class MySmartBikeListerner implements BleSdkUtils.SmartBikeListerner {

        @Override
        public void smartBikeConnected() {
            Log.w(TAG, "MySmartBikeListerner, smartBikeConnected "+", smartbike = "+mBleSdkUtils.getSmartBike());
            if (pairLoading != null && pairLoading.isShowing()) {
                pairLoading.dismiss();
            }
            if (cusdialog != null && cusdialog.isShowing()) {
                cusdialog.dismiss();
            }

            if (isKeyDirect) {
                if (directLoading != null && directLoading.isShowing()) {
                    directLoading.dismiss();
                }
                pairResult = "小主，车辆连接直连成功！";
                isKeyDirect = false;
            } else {
                pairResult = "小主，车辆连接成功！";
            }
            broadFragment(true);
            Toast.makeText(mContext, pairResult, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void smartBikeDisConnectted(BlueGuard.DisconnectReason reason) {
            Log.w(TAG, "MySmartBikeListerner, smartBikeDisConnectted , reason = " + reason+", smartbike = "+mBleSdkUtils.getSmartBike());

            if (pairLoading != null && pairLoading.isShowing()) {
                pairLoading.dismiss();
            }
            if (directLoading != null && directLoading.isShowing()) {
                directLoading.dismiss();
            }
            if (cusdialog != null && cusdialog.isShowing()) {
                cusdialog.dismiss();
            }

            if (isKeyDirect) {
                pairResult = "小主，车辆连接直连失败！";
                isKeyDirect = false;
            } else {
                pairResult = "小主，车辆连接失败";
            }
            Toast.makeText(mContext, pairResult, Toast.LENGTH_SHORT).show();
            broadFragment(false);
            if(reason != BlueGuard.DisconnectReason.CLOSED ){
                DeviceDB.deleteKey(mContext);

            }
            mDevices.clear();
        }

        @Override
        public void smartBikeName(String name) {
            Log.w(TAG,"smartBikeName ---- "+name);
        }

        @Override
        public void smartBikeState(BlueGuard.State state) {
            Log.w(TAG, "smartBikeState state = " + state);

        }

        @Override
        public void smartBikePairResult(BlueGuard blueGuard, BlueGuard.PairResult result,
                                        String key) {
            Log.w(TAG, "smartBikePairResult result:" + result + "  key:" + result);
            /*pairLoading.dismiss();
            if (pairTimer != null) {
                pairTimer.cancel();
                pairTimer = null;
            }
            if (result == BlueGuard.PairResult.SUCCESS) {
                //save db
                if (!TextUtils.isEmpty(key)) {
                    DeviceDB.Record rec = new DeviceDB.Record(blueGuard.name(), blueGuard.identifier(), key);
                    DeviceDB.save(mContext, rec);
                    Log.w(TAG, "小主连接车辆成功！");
                } else {
                    Log.w(TAG, "小主连接车辆成功,但是key == null");
                    Toast.makeText(mContext, "pair 成功，但是key == null", Toast.LENGTH_SHORT).show();
                }
            } else if (result == BlueGuard.PairResult.ERROR_KEY) {
                Log.w(TAG, "小主连接车辆失败，原因：ERROR_KEY ,delete data");
                DeviceDB.delete(mContext);
                mDevices.clear();
            } else if (result == BlueGuard.PairResult.ERROR_PERMISSION) {
                Log.w(TAG, "小主连接车辆失败，原因：ERROR_PERMISSION , delete data");
                DeviceDB.delete(mContext);
                mDevices.clear();
                //pair show
            } else if (result == BlueGuard.PairResult.ERROR) {
                Log.w(TAG, "小主连接车辆失败，原因：ERROR");
            }*/

        }


        @Override
        public void smartBikeUpdateData(SmartBike smartBike, byte[] data) {
            Log.w(TAG, "smartBikeUpdateData data = " + data);
            dealBluetoothData(data);
        }


    }

    //处理硬件返回的数据
    private void dealBluetoothData(byte[] data) {
        // 从这里可以解析出 里程 速度 电量 故障码  等信息，如何解析需要和硬件沟通
        Log.w(TAG,"dealBluetoothData ----\n"+ Arrays.toString(data));
        // 模拟
        String mile = ParseDataUtils.parseMile(data); //km
        String time = "00:00:00";
        String error =ParseDataUtils.parseARS(data);
        String speed = ParseDataUtils.parseSpeed(this,data);//  km/h
        int dangwei = ParseDataUtils.parseDangWei(data);
        Fragment tab1 = getSupportFragmentManager().findFragmentByTag("tab1");
        if(tab1!=null){
            if(tab1 instanceof RideFragment){
                ((RideFragment)tab1).processData(mile,time,error,speed,dangwei);
            }
        }


    }

    /**
     * 扫描 不到设备 十秒内自动关闭
     *
     * @author jpj
     */
    class ScanTask extends TimerTask {

        @Override
        public void run() {
            Log.w(TAG,"ScanTask ----");
            if (mBleSdkUtils.isScanning()) {
                mBleSdkUtils.stopScanBleDevice();
            }
            if(adapter.getCount() == 0){
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if(adapter.getCount() == 0){
                        Toast.makeText(mContext, "10秒，未搜索到车辆请重试！", Toast.LENGTH_SHORT).show();
                    }
                    broadFragment(false);
                }

            });
        }

    }
    public void broadFragment(boolean connect){
        Log.w(TAG,"broadFragment ---- connect = "+connect);
        this.connect =connect;
        /*com.qdigo.jindouyun.utils.DeviceDB.Record rec = new com.qdigo.jindouyun.utils.DeviceDB.Record("", "", "");
        if(!connect){
            com.qdigo.jindouyun.utils.DeviceDB.save(this,rec);
        }*/
        /*
        mile = new Random().nextInt(100)+" km";
        time = "33:33:33";
        error= "未知错误";
        speed = new Random().nextInt(100)+"";
        dangwei = new Random().nextInt(25);*/
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment1 = fm.findFragmentByTag("tab1");
        Fragment fragment2 = fm.findFragmentByTag("tab2");
        Fragment fragment3 = fm.findFragmentByTag("tab3");
        if(mMineFragment !=null){
                ((MineFragment)fragment2).connectNotify(connect);
        }
        if(fragment1 !=null){
            if(fragment1 instanceof RideFragment){
                ((RideFragment)fragment1).connectNotify(connect);
                ((RideFragment)fragment1).processData(mile, time, error, speed, dangwei);
            }

        }
    }

    class PairTask extends TimerTask {

        @Override
        public void run() {
            Log.w(TAG,"PairTask ----");
            if (pairLoading != null && pairLoading.isShowing()) {
                pairLoading.dismiss();
            }
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    broadFragment(false);
                    Toast.makeText(mContext, "10秒，未配对到车辆！", Toast.LENGTH_SHORT).show();
                }

            });
        }

    }

    class DirectTask extends TimerTask {

        @Override
        public void run() {
            Log.w(TAG,"DirectTask ----");
            if (directLoading != null && directLoading.isShowing()) {
                directLoading.dismiss();
            }
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    broadFragment(false);
                    Toast.makeText(mContext, "10秒，没有直连到车辆！", Toast.LENGTH_SHORT).show();
                }

            });
        }

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBleSdkUtils.unBindService();
        unregisterReceiver(bleStateReceiver);
        unregisterReceiver(bleConectReceiver);
        app.ble.mConnection.disconnect();
    }

    @Override
    protected void initView() {

    }

    public void controlBike(int code) {
        switch (code){
            case 0:
                app.ble.lock();
                //上锁
                break;
            case 1:
                app.ble.unlock();
                //解锁
                break;
            case 2:
                app.ble.findCarAlarm();
                //寻车
                break;
            case 3:
                app.ble.start();
                //上电
                break;
            case 4:
                app.ble.stop();
                //断电
                break;
            case 5:
                app.ble.openSeat();
                //open trunk
                break;
            default:
                break;

        }
    }

    public void setDangwei(int dangwei){
        app.ble.setSensity(dangwei);
    }

    private boolean isFirst;
    @Override
    public void onBackPressed() {
        if(!isFirst){
            isFirst = true;
            Toast.makeText(mContext, "再点一次退出应用", Toast.LENGTH_SHORT).show();
        }else{

            super.onBackPressed();
//            mBleSdkUtils.disConnectDevice();
            app.ble.disConnectDevice();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }
}
