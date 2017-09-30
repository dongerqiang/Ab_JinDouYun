package com.qdigo.blediscovery;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mScanBtn;
    private RecyclerView mRecyclerView;
    private BluetoothAdapter mBluetoothAdapter;
    public static final int REQUEST_ENABLE_BT =99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initBle();
    }

    private void initBle() {
        //是否支持ble蓝牙
        isSupportBle();
    }

    private void isSupportBle() {
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "您的手机不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
            return ;
        }

        BluetoothManager bluetoothManager = (BluetoothManager) this
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "您的手机不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
            return ;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            // 如果Android6.0以上版本，要求打开GPS，还要访问位置的权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 打开GPS定位
                initGPS();
                // 提示用户是否授予位置访问权限
                requireLocationAccess();
            }
        }
    }

    private void initGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.open_gps));
            dialog.setMessage(getString(R.string.msg));
            dialog.setPositiveButton(getString(R.string.setting), new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // 转到手机设置界面，用户设置GPS
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                }
            });
            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        }
    }

    public static final int PERMISSION_REQUEST_COARSE_LOCATION=100;
    private void requireLocationAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //用户拒绝授予位置访问权限
                    Toast.makeText(MainActivity.this,"用户拒绝授予位置访问权限",Toast.LENGTH_SHORT).show();
                }else {
                    //用户授予位置访问权限
                }
                break;
        }
    }
    private void initView() {
        mScanBtn = (Button) findViewById(R.id.btn_scan);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyvleView);
        mScanBtn.setOnClickListener(this);
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_scan:
                // TODO: 2017-08-25  
                break;
        }
    }
}
