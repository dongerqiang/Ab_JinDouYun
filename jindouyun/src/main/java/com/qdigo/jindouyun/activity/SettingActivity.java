package com.qdigo.jindouyun.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qdigo.jindouyun.BaseActivity;
import com.qdigo.jindouyun.R;
import com.qdigo.jindouyun.utils.BroadcastUtils;
import com.qdigo.jindouyun.utils.DialogCallback;
import com.qdigo.jindouyun.utils.DialogUtils;
import com.qdigo.jindouyun.utils.ParseDataUtils;

import butterknife.Bind;

import static com.qdigo.jindouyun.MyApplication.app;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ljTv)
    TextView  ljTv;
    @Bind(R.id.jdsTv)
    TextView  jdsTv;
    @Bind(R.id.volTv)
    TextView  volTv;
   /* @Bind(R.id.dangTv)
    TextView  dangTv;*/
    @Bind(R.id.danWeiTv)
    TextView danWei;
    @Bind(R.id.tv_total)
    TextView totalmile;

   @Bind(R.id.tv_carluli)
    TextView mCarLuLi;

    /*@Bind(R.id.lockCb)
    CheckBox lockCb;
    @Bind(R.id.unlockCb)
    CheckBox unlockCb;
    @Bind(R.id.findCb)
    CheckBox findCb;
    @Bind(R.id.anzhuoCb)
    CheckBox anzhuoCb;
    @Bind(R.id.autolockCb)
    CheckBox autolockCb;
    @Bind(R.id.mutelockCb)
    CheckBox mutelockCb;*/
    @Bind(R.id.ljLayout)
    LinearLayout ljLayout;
    @Bind(R.id.jdsLayout)
    LinearLayout jdsLayout;
   /* @Bind(R.id.volLayout)
    LinearLayout volLayout;*/
    /*@Bind(R.id.speedLayout)
    LinearLayout speedLayout;*/
    @Bind(R.id.danweiLayout)
    LinearLayout danweiLayout;
    @Bind(R.id.caluli)
    LinearLayout kaluli;
    private String speedStr="0";

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: 2017-06-24
        String km1 = app.deviceNotes.speedDanWei(false, "km");
        String[] split = totalmile.getText().toString().trim().split(" ");
        if(!km1.equalsIgnoreCase(split[1])){
                if("km".equalsIgnoreCase(split[1])&&km1.equalsIgnoreCase("mi")){
                    totalmile.setText(ParseDataUtils.kmToMi(split[0])+" "+km1);
                }else if("mi".equalsIgnoreCase(split[1])&&km1.equalsIgnoreCase("km")){
                    totalmile.setText(ParseDataUtils.miToKm(split[0])+" "+km1);
                }
        }

    }
    BleStateReceiver bleStateReceiver;
    public String timeSe = "00:00:00";
    public float carluli = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_setting,"设置");
        registerReceiver(bleStateReceiver = new BleStateReceiver(), new IntentFilter(BroadcastUtils.MILEAGE_ACTION));

    }
    class BleStateReceiver extends BroadcastReceiver {
        float speed =0;
        int running = 0;
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if (intent.getAction().equals(BroadcastUtils.MILEAGE_ACTION)) {
                if (intent.hasExtra(BroadcastUtils.MILEAGE_VALUE_KEY)) {
                   /* String km = intent.getStringExtra(BroadcastUtils.MILEAGE_VALUE_KEY);
                    String km1 = app.deviceNotes.speedDanWei(false, "km");
                    if(!"km".equalsIgnoreCase(km1)){
                        km = ParseDataUtils.kmToMi(km);
                    }
                    totalmile.setText(km+" "+app.deviceNotes.speedDanWei(false,"km"));*/
                }else if(intent.hasExtra(BroadcastUtils.BATTERY_VALUE_KEY)){
                    String battery = intent.getStringExtra(BroadcastUtils.BATTERY_VALUE_KEY);
                    volTv.setText(battery+"V");
                }else if(intent.hasExtra(BroadcastUtils.MILEAGE_VALUE_INCREASE_KEY)){
                    //本次骑行里程
                    String km = intent.getStringExtra(BroadcastUtils.MILEAGE_VALUE_INCREASE_KEY);
                    String km1 = app.deviceNotes.speedDanWei(false, "km");
                    if(!"km".equalsIgnoreCase(km1)){
                        km = ParseDataUtils.kmToMi(km);
                    }
                    totalmile.setText(km+" "+app.deviceNotes.speedDanWei(false,"km"));
                }else if (intent.hasExtra(BroadcastUtils.RUNNING_TIME_KEY)){
                    //时间
                    String time = intent.getStringExtra(BroadcastUtils.RUNNING_TIME_KEY);
                    timeSe = time;
                    try {
                        String[] split = time.split(":");
                        //s
                        int runningtime = Integer.parseInt(split[0])*3600+Integer.parseInt(split[1])*60+Integer.parseInt(split[2]);
                        running =runningtime;
                    }catch (Exception e){
                        running =0;
                    }

                }else if(intent.hasExtra(BroadcastUtils.SPEED_VALUE_KEY)){
                    speedStr = intent.getStringExtra(BroadcastUtils.SPEED_VALUE_KEY);
                    speed =Float.parseFloat(speedStr);
                }

            }
            if(intent.getAction().equals(BroadcastUtils.BLE_CONNECT_STATE)){
                int state = intent.getIntExtra(BroadcastUtils.KEY_BLE_STATE, 0);
                if(state ==1){
                    app.showToast("骑行开始");
                    speed =0;
                    running = 0;
                    speedStr="0";
                    carluli = 0;
                    timeSe="00:00:00";
                }else{
                    app.showToast("骑行结束");
                }
            }

        }
    }
    @Override
    protected void initView() {
        float v = app.deviceNotes.optWheelR(false, 1f);
        ljTv.setText(v+"寸");
        mCarLuLi.setText(app.deviceNotes.optKg(false,60)+"");
        jdsTv.setText(app.deviceNotes.optMotorJds(false, 1)+"");
        volTv.setText(app.deviceNotes.opeMotorVol(false, 1)+"V");
//        dangTv.setText(app.deviceNotes.opeMotorDang(false, "低"));

//        lockCb.setChecked(app.deviceNotes.enabledLock(false, 1) == 1 ? true : false);
//        unlockCb.setChecked(app.deviceNotes.enabledUnLock(false, 1) == 1 ? true : false);
//        findCb.setChecked(app.deviceNotes.enabledAlarm(false, 1) == 1 ? true : false);
//        anzhuoCb.setChecked(app.deviceNotes.enabledSeat(false, 1) == 1 ? true : false);
//        autolockCb.setChecked(app.deviceNotes.enabledAutoGuard(false, 1) == 1 ? true : false);
//        mutelockCb.setChecked(app.deviceNotes.enabledMuteGuard(false, 1) == 1 ? true : false);

//        lockCb.setOnCheckedChangeListener(checkBoxListener);
//        unlockCb.setOnCheckedChangeListener(checkBoxListener);
//        findCb.setOnCheckedChangeListener(checkBoxListener);
//        anzhuoCb.setOnCheckedChangeListener(checkBoxListener);
//        autolockCb.setOnCheckedChangeListener(checkBoxListener);
//        mutelockCb.setOnCheckedChangeListener(checkBoxListener);
        ljLayout.setOnClickListener(this);
        jdsLayout.setOnClickListener(this);
//        volLayout.setOnClickListener(this);
//        speedLayout.setOnClickListener(this);
        danweiLayout.setOnClickListener(this);
        kaluli.setOnClickListener(this);
    }

   /* CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton cb, boolean state) {

            if(cb == lockCb){
                app.deviceNotes.enabledLock(true,state ? 1: 0);
                if(state){
                    app.ble.lock();
                }
            }else if(cb == unlockCb){
                app.deviceNotes.enabledUnLock(true,state ? 1: 0);
                if(state){
                    app.ble.unlock();
                }
            }else if(cb == findCb){
                app.deviceNotes.enabledAlarm(true,state ? 1: 0);
                if(state){
                    app.ble.findCarAlarm();
                }
            }else if(cb == anzhuoCb){
                app.deviceNotes.enabledSeat(true,state ? 1: 0);
                if(state){
                    app.ble.openSeat();
                }
            }else if(cb == autolockCb){
                app.deviceNotes.enabledAutoGuard(true,state ? 1: 0);
                app.ble.setAutoArmRangePercent(state);
            }else if(cb == mutelockCb){
                app.deviceNotes.enabledMuteGuard(true,state ? 1: 0);
                if(state){
                    app.ble.setClientArm();
                }else{
                    app.ble.openVoice();
                }
            }

        }
    };
*/
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ljLayout:
                DialogUtils.getInstance().showSelectList(this, "选择轮径(英寸)",new String[]{
                        "10","10.5","11","11.5","12","12.5","13","13.5","14","14.5","15","15.5","16","16.5",
                        "17","17.5","18","18.5","19","19.5","20","20.5","21","21.5","22","22.5","23","23.5","24","24.5","25","25.5","26","26.5","27",
                        "27.5","28","28.5","29","29.5","30"}, new DialogCallback() {
                    @Override
                    public void typeStr(String type) {
                        ljTv.setText(type+"寸");
                        app.deviceNotes.optWheelR(true, Float.valueOf(type));
                    }
                });
                break;
            case R.id.jdsLayout:
                DialogUtils.getInstance().showSelectList(this,"选择极对数", new String[]{
                        "2","3","4","5","6","7","8","9","10",
                        "11","12","13","14","15","16","17","18","19","20",
                        "21","22","23","24","25","26","27","28","29","30",
                        "31","32","33","34","35","36","37","38","39","40",
                        "41","42","43","44","45","46","47","48","49","50",
                        "51","52","53","54","55","56","57","58","59","60",
                        "61","62","63","64","65","66","67","68","69","70",
                        "71","72","73","74","75","76","77","78","79","80"
                }, new DialogCallback() {
                    @Override
                    public void typeStr(String type) {
                        jdsTv.setText(type);
                        app.deviceNotes.optMotorJds(true, Integer.valueOf(type));
                    }
                });
                break;
           /* case R.id.speedLayout:
                DialogUtils.getInstance().showSelectList(this, new String[]{"低","中","高"}, new DialogCallback() {
                    @Override
                    public void typeStr(String type) {
                        dangTv.setText(type);
                        app.deviceNotes.opeMotorDang(true, type);
                    }
                });
                break;*/
            /*case volLayout:
                DialogUtils.getInstance().showSelectList(this, new String[]{"12","26","48","60","64","72","80"}, new DialogCallback() {
                    @Override
                    public void typeStr(String type) {
                        volTv.setText(type);
                        app.deviceNotes.opeMotorVol(true, Integer.valueOf(type));
                    }
                });
                break;*/
            case R.id.danweiLayout:
                DialogUtils.getInstance().showSelectList(this,"选择单位",new String[]{"km","mi"}, new DialogCallback() {
                    @Override
                    public void typeStr(String type) {
                        danWei.setText(type);
                        String[] split = totalmile.getText().toString().split(" ");
                        totalmile.setText(split[0]+" "+type);
                        app.deviceNotes.speedDanWei(true, type);
                    }
                });
                break;
            case R.id.caluli:
                DialogUtils.getInstance().showSelectList(this, "选择体重(Kg)",new String[]{
                        "21","22","23","24","25","26","27","28","29","30",
                        "31","32","33","34","35","36","37","38","39","40",
                        "41","42","43","44","45","46","47","48","49","50",
                        "51","52","53","54","55","56","57","58","59","60",
                        "61","62","63","64","65","66","67","68","69","70",
                        "71","72","73","74","75","76","77","78","79","80",
                        "81","82","83","84","85","86","87","88","89","90",
                        "91","92","93","94","95","96","97","98","99","100",

                    }, new DialogCallback() {
                    @Override
                    public void typeStr(String type) {
                        int parseInt = Integer.parseInt(type);
                        int optKg = app.deviceNotes.optKg(true, parseInt);

                        mCarLuLi.setText(type);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bleStateReceiver);
    }
}
