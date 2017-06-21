package com.qdigo.deq.blesdk.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.qdigo.deq.blesdk.R;
import com.qdigo.deq.blesdk.blesdkhelp.BleSdkUtils;
import com.qdigo.deq.blesdk.blesdkhelp.DeviceAdapter;
import com.qdigo.deq.blesdk.blesdkhelp.DeviceDB;
import com.xiaofu_yan.blux.blue_guard.BlueGuard;
import com.xiaofu_yan.blux.smart_bike.SmartBike;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BleSdkUtils.ScanSmartListener {

    private static final String TAG = "deq";
    private BleSdkUtils mBleSdkUtils;

    private static final int REQUEST_ENABLE_BT = 10;
    private Button mBtnArm;
    private Button mBtnTrunk;
    private Button mBtnDisarm;
    private Button mBtnFind;
    private Button mBtnStart;
    private Button mBtnStop;
    private Button mBtnSocket;

    private Dialog dialog;
    private DeviceAdapter adapter;
    // devices
    private List<DeviceDB.Record> mDevices = new ArrayList<>();
    private DeviceDB.Record mCurrentDevice;
    private boolean isShowingDeviceListDialog = false;
    private EditText mPassEditView;
    private boolean keyDirect;
    private AlertDialog pairDialog;
    private Button mBtnShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        mBleSdkUtils = new BleSdkUtils();
        mBleSdkUtils.setScanSmartListener(this);
        mBleSdkUtils.initBleSdk(this);
        //提示打开蓝牙
        openBluetooth();

    }

    public boolean getCurrentDevice() {
        boolean isEnable = false;
        DeviceDB.Record currentDevice = DeviceDB.load(this);
        if(currentDevice !=null){
            if(!TextUtils.isEmpty(currentDevice.identifier)&&!TextUtils.isEmpty(currentDevice.name)&& !TextUtils.isEmpty(currentDevice.key)){
//                mDevices.add(currentDevice);
                isEnable = true;
                mCurrentDevice = currentDevice;
            }
        }
        return isEnable;
    }

    private void initData() {
        if(getCurrentDevice()){
            mDevices.add(mCurrentDevice);
        }else{
            Log.w(TAG,"initData() -- null");
        }

    }

    /**
     * 打开蓝牙
     */
    private void openBluetooth() {
        //判断设备是否支持ble
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        // 初始化蓝牙适配器
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        // 确保蓝牙在手机上可以开启
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // open bluetooth back
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "开启蓝牙成功", Toast.LENGTH_SHORT).show();
                mBleSdkUtils.scanBleDevice();
                // mBluetoothAdapter.enable(); 此方法不需要询问用户，直接开启 但需要admin的权限
            } else {
                Toast.makeText(this, "开启蓝牙失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBleSdkUtils.bindService(this);
        getCurrentDevice();
        if(mCurrentDevice !=null && !TextUtils.isEmpty(mCurrentDevice.identifier)){
            mBleSdkUtils.connectDeviceByIdentifier(mCurrentDevice.identifier);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBleSdkUtils.unBindService();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBleSdkUtils.unBindService();
    }

    private void initView() {
        mBtnArm = (Button) findViewById(R.id.btn_arm);
        mBtnTrunk = (Button) findViewById(R.id.btn_trunk);
        mBtnDisarm = (Button) findViewById(R.id.btn_disarm);
        mBtnFind = (Button) findViewById(R.id.btn_find);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStop = (Button) findViewById(R.id.btn_stop);
        mBtnShare = (Button) findViewById(R.id.btn_share);
        mBtnSocket = (Button)findViewById(R.id.btn_socket);
        Button mBtnScan = (Button) findViewById(R.id.btn_scan);

        mBtnArm.setOnClickListener(this);
        mBtnTrunk.setOnClickListener(this);
        mBtnDisarm.setOnClickListener(this);
        mBtnFind.setOnClickListener(this);
        mBtnStart.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        mBtnShare.setOnClickListener(this);
        mBtnScan.setOnClickListener(this);
        mBtnSocket.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_arm:
                mBleSdkUtils.lock();
                break;
            case R.id.btn_trunk:
                mBleSdkUtils.openSeat();
                break;
            case R.id.btn_disarm:
                mBleSdkUtils.unlock();
                break;
            case R.id.btn_find:
                mBleSdkUtils.findBikeAlarm();
                break;
            case R.id.btn_start:
                mBleSdkUtils.start();
                break;
            case R.id.btn_stop:
                mBleSdkUtils.stop();
                break;
            case R.id.btn_scan:
                    mBleSdkUtils.scanBleDevice();
                    isShowingDeviceListDialog = true;
                    showDeviceDialog(mDevices);
                break;
            case R.id.btn_share:
                shareQdigo();
                break;
            case R.id.btn_socket:
                //socket
                doSocket();
                break;

        }
    }

    /**
     * 进入高德地图
     */
    private void doSocket() {
        Intent intent = new Intent(this,MapActivity.class);
        startActivity(intent);
    }

    private void shareQdigo() {
        Intent share_intent = new Intent();
        String dialog ="分享一点，绿色一点",title = "电滴出行分享",content = "租电动车，找电滴出行！你的一小步，环保的一大步！http://www.qdigo.com";
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("text/plain");//设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_SUBJECT, title);//添加分享内容标题
        share_intent.putExtra(Intent.EXTRA_TEXT, content);//添加分享内容
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, dialog);
        startActivity(share_intent);
    }

    /**
     * 显示搜索到的设备
     */

    protected void showDeviceDialog(final List<DeviceDB.Record> devices){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view =inflater.inflate(R.layout.device_list2, null);
        ListView listView = (ListView) view.findViewById(R.id.paired_devices);
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        if(devices == null || devices.size() ==0 ){
            progressBar.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
        }

        adapter = new DeviceAdapter(this, devices);
        listView.setAdapter(adapter);
        dialog = new Dialog(this, R.style.dialog);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();
        if(!isShowingDeviceListDialog ){
            dialog.dismiss();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (devices == null) return;
                mBleSdkUtils.stopScanBleDevice();
                DeviceDB.Record currentDeviceInfo = devices.get(position);
                DeviceDB.save(MainActivity.this,currentDeviceInfo);
                if(!TextUtils.isEmpty(currentDeviceInfo.identifier)){
                    mBleSdkUtils.connectDeviceByIdentifier(currentDeviceInfo.identifier);
                }
                mCurrentDevice = currentDeviceInfo;
                dialog.dismiss();

            }
        });
    }

    private void pairDialog() {
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.pair_pass_input, (ViewGroup)findViewById(R.id.pair_pass_word_layout));
        mPassEditView = (EditText) layout.findViewById(R.id.pair_pass_key_input);
        mPassEditView.setText("000000");

        pairDialog = new AlertDialog.Builder(MainActivity.this).
                setTitle("Pair").
                setMessage("Pair key:").
                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBleSdkUtils.stopScanBleDevice();
                        String password = MainActivity.this.mPassEditView.getText().toString();
                        if("000000".equals(password)){
                            mBleSdkUtils.connectDeviceByPair(password);
                        }else {
                            Toast.makeText(MainActivity.this, R.string.toast_pair_error,Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }).setView(layout).create();
        pairDialog.show();
    }

    private void updateButton(BlueGuard.State state) {
        SmartBike smartBike = mBleSdkUtils.getSmartBike();
        boolean sEnable = (smartBike != null)&&(smartBike.connected());
        mBtnArm.setEnabled(sEnable);
        mBtnTrunk.setEnabled(sEnable);
        mBtnDisarm.setEnabled(sEnable);
        mBtnFind.setEnabled(sEnable);
        mBtnStart.setEnabled(sEnable);
        mBtnStop.setEnabled(sEnable);
        if(sEnable){
            if(BlueGuard.State.ARMED == state) {
                mBtnArm.setEnabled(false);
            }
            if(BlueGuard.State.STOPPED == state) {
                mBtnStop.setEnabled(false);
            }
            if(BlueGuard.State.STARTED == state) {
                mBtnStart.setEnabled(false);
            }
            if(BlueGuard.State.RUNNING == state) {
                mBtnArm.setEnabled(false);
                mBtnStop.setEnabled(false);
                mBtnStart.setEnabled(false);
            }
        }



    }

    /**
     * 服务连接回调
     * @param connect
     */
    @Override
    public void serviceConnectResult(boolean connect) {
        if(connect){
            getCurrentDevice();
            if(mCurrentDevice != null){
                if(!TextUtils.isEmpty(mCurrentDevice.identifier)){
                    mBleSdkUtils.connectDeviceByIdentifier(mCurrentDevice.identifier);
                }
            }else{
                mBleSdkUtils.scanBleDevice();
            }
        }else{
            Log.w(TAG,"Server connect failed and bind again");
            mBleSdkUtils.bindService(this);
        }
    }

    /**
     * 成功扫描到设备的回调
     * @param identifier
     * @param name
     */
    @Override
    public void scanSuccess(String identifier, String name) {

        if(!TextUtils.isEmpty(identifier) && !TextUtils.isEmpty(name)){
            Log.w(TAG,"device identifier:"+identifier+"  device name:"+name);
            mBleSdkUtils.stopScanBleDevice();
            mBleSdkUtils.connectDeviceByIdentifier(identifier);
            DeviceDB.Record rec = new DeviceDB.Record(name, identifier, null);
            if(dialog!=null&&dialog.isShowing()&& mDevices.size() == 0){
                Toast.makeText(this,"find new devices",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
            if(adapter !=null){
                adapter.addDevice(rec);
            }
            if(isShowingDeviceListDialog){
                isShowingDeviceListDialog =false;
            }

        }
    }

    /**
     * connectDeviceByIdentifier(identifier);的回调 得到smartbike实例 即可对车 发送指令
     */
    @Override
    public void getSmartBikeInstance() {
        if(mCurrentDevice!= null){
            // from dialog
                if(!TextUtils.isEmpty(mCurrentDevice.key)){
                    mBleSdkUtils.connectDeviceBykey(mCurrentDevice.key);
                }else{
                    if(pairDialog ==null){
                        pairDialog();
                    }
                }
        }
    }

    @Override
    public void smartBikePairResult(BlueGuard blueGuard,BlueGuard.PairResult result, String key) {
        Log.w(TAG,"smartBikePairResult result:"+result+"  key:"+result);
        if(result == BlueGuard.PairResult.SUCCESS){
            //save db
            if(!TextUtils.isEmpty(key)){
                DeviceDB.Record rec = new DeviceDB.Record(blueGuard.name(), blueGuard.identifier(), key);
                DeviceDB.save(this, rec);
            }else{
                Toast.makeText(this,"pair failed __ key is not ok",Toast.LENGTH_SHORT).show();
            }
        }else if(result == BlueGuard.PairResult.ERROR_KEY){
            DeviceDB.delete(this);
        }else if (result == BlueGuard.PairResult.ERROR_PERMISSION ){
            Toast.makeText(this,"重启硬件",Toast.LENGTH_SHORT).show();
            DeviceDB.delete(this);
            //pair show
        }else{
            Toast.makeText(this,"pair failed",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateState(BlueGuard.State state) {
        updateButton(state);
    }

}
