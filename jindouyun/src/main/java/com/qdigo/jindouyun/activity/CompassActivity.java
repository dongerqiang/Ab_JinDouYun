package com.qdigo.jindouyun.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.qdigo.jindouyun.BaseActivity;
import com.qdigo.jindouyun.R;
import com.qdigo.jindouyun.view.CompassView;

import butterknife.Bind;

public class CompassActivity extends BaseActivity implements SensorEventListener {
    @Bind(R.id.compas_view)
    protected CompassView mCompassView;

    private SensorManager mSensorManager;
    private Sensor mMagneticSensor;
    private Sensor mAccelerateSensor;

    private boolean mHasNeededSensors = false;

    float[] mAccelerometerValues = new float[3];
    float[] mMagneticFieldValues = new float[3];

    public static final String TAG = "CompassActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentViewWithDefaultTitle(R.layout.activity_compass,"指南针");

    }

    @Override
    protected void initView() {
        initSensor();
    }

    public void initSensor(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null &&
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mAccelerateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mHasNeededSensors = true;
        } else {
            Toast.makeText(this, "没有磁力计或加速度计", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mHasNeededSensors) {
            mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mAccelerateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mHasNeededSensors) {
            mSensorManager.unregisterListener(this);
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
}
