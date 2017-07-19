package com.qdigo.jindouyun;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.baidu.mapapi.SDKInitializer;
import com.qdigo.jindouyun.blesdkhelp.BleInterface;
import com.qdigo.jindouyun.utils.BroadcastUtils;
import com.qdigo.jindouyun.utils.DeviceNotes;
import com.qdigo.jindouyun.utils.ToastUtil;

import java.lang.reflect.Method;

/**
 * Created by jpj on 2017-06-08.
 */

public class MyApplication extends Application {
    public static MyApplication app;
    public DeviceNotes deviceNotes;
    public int heightPixels ;
    public int widthPixels ;
    public BleInterface ble;
    public BroadcastUtils broadUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        MyApplication.app.ble = BleInterface.getInstance();
        MyApplication.app.ble.startSmartBikeService(this);
        broadUtils = BroadcastUtils.getInstance();
        broadUtils.init(this);

        deviceNotes = DeviceNotes.getInstance();
        deviceNotes.init(this);
        int[] displayScreenResolution = getDisplayScreenResolution(app);
        heightPixels = displayScreenResolution[1];
        widthPixels = displayScreenResolution[0];
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
    }

    public  int[] getDisplayScreenResolution(Context context) {
        int[] screenSizeArray = new int[2];

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);

        screenSizeArray[0] = dm.widthPixels;
        screenSizeArray[1] = dm.heightPixels;
        int ver = Build.VERSION.SDK_INT;
        if (ver < 13) {
            screenSizeArray[1] = dm.heightPixels;
        } else if (ver == 13) {
            try {
                Method mt = display.getClass().getMethod("getRealHeight");
                screenSizeArray[1] = (Integer) mt.invoke(display);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ver > 13) {
            try {
                Method mt = display.getClass().getMethod("getRawHeight");
                screenSizeArray[1] = (Integer) mt.invoke(display);

            } catch (Exception e) {
                screenSizeArray[1] = dm.heightPixels;
            }
        }

        return screenSizeArray;
    }
    public void showToast(String str){
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        ToastUtil.showToast(this,str);
    }
    public static void logBug(String msg) {
        Log.d("jindouyun", msg);
    }
}
