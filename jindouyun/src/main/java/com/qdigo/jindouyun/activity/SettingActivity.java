package com.qdigo.jindouyun.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qdigo.jindouyun.BaseActivity;
import com.qdigo.jindouyun.R;
import com.qdigo.jindouyun.utils.DialogCallback;
import com.qdigo.jindouyun.utils.DialogUtils;

import butterknife.Bind;

import static com.qdigo.jindouyun.MyApplication.app;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ljTv)
    TextView  ljTv;
    @Bind(R.id.jdsTv)
    TextView  jdsTv;
    @Bind(R.id.volTv)
    TextView  volTv;
    @Bind(R.id.dangTv)
    TextView  dangTv;

    @Bind(R.id.lockCb)
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
    CheckBox mutelockCb;
    @Bind(R.id.ljLayout)
    LinearLayout ljLayout;
    @Bind(R.id.jdsLayout)
    LinearLayout jdsLayout;
    @Bind(R.id.volLayout)
    LinearLayout volLayout;
    @Bind(R.id.speedLayout)
    LinearLayout speedLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_setting,"设置");
    }

    @Override
    protected void initView() {

        ljTv.setText(app.deviceNotes.optWheelR(false, 1)+"寸");
        jdsTv.setText(app.deviceNotes.optMotorJds(false, 1)+"");
        volTv.setText(app.deviceNotes.opeMotorVol(false, 1)+"V");
        dangTv.setText(app.deviceNotes.opeMotorDang(false, "低"));

        lockCb.setChecked(app.deviceNotes.enabledLock(false, 1) == 1 ? true : false);
        unlockCb.setChecked(app.deviceNotes.enabledUnLock(false, 1) == 1 ? true : false);
        findCb.setChecked(app.deviceNotes.enabledAlarm(false, 1) == 1 ? true : false);
        anzhuoCb.setChecked(app.deviceNotes.enabledSeat(false, 1) == 1 ? true : false);
        autolockCb.setChecked(app.deviceNotes.enabledAutoGuard(false, 1) == 1 ? true : false);
        mutelockCb.setChecked(app.deviceNotes.enabledMuteGuard(false, 1) == 1 ? true : false);

        lockCb.setOnCheckedChangeListener(checkBoxListener);
        unlockCb.setOnCheckedChangeListener(checkBoxListener);
        findCb.setOnCheckedChangeListener(checkBoxListener);
        anzhuoCb.setOnCheckedChangeListener(checkBoxListener);
        autolockCb.setOnCheckedChangeListener(checkBoxListener);
        mutelockCb.setOnCheckedChangeListener(checkBoxListener);
        ljLayout.setOnClickListener(this);
        jdsLayout.setOnClickListener(this);
        volLayout.setOnClickListener(this);
        speedLayout.setOnClickListener(this);
    }

    CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ljLayout:
                DialogUtils.getInstance().showSelectList(this, new String[]{"10","12","14","16","18","20","22","24","26"}, new DialogCallback() {
                    @Override
                    public void typeStr(String type) {
                        ljTv.setText(type+"寸");
                        app.deviceNotes.optWheelR(true, Integer.valueOf(type));
                    }
                });
                break;
            case R.id.jdsLayout:
                DialogUtils.getInstance().showSelectList(this, new String[]{"20","23","28","30"}, new DialogCallback() {
                    @Override
                    public void typeStr(String type) {
                        jdsTv.setText(type);
                        app.deviceNotes.optMotorJds(true, Integer.valueOf(type));
                    }
                });
                break;
            case R.id.speedLayout:
                DialogUtils.getInstance().showSelectList(this, new String[]{"低","中","高"}, new DialogCallback() {
                    @Override
                    public void typeStr(String type) {
                        dangTv.setText(type);
                        app.deviceNotes.opeMotorDang(true, type);
                    }
                });
                break;
            case R.id.volLayout:
                DialogUtils.getInstance().showSelectList(this, new String[]{"12","26","48","60","64","72","80"}, new DialogCallback() {
                    @Override
                    public void typeStr(String type) {
                        volTv.setText(type);
                        app.deviceNotes.opeMotorVol(true, Integer.valueOf(type));
                    }
                });
                break;
            default:
                break;
        }
    }
}
