package com.qdigo.jindouyun.utils;


import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/***
 * 存储设备记录
 * 
 * @author fu
 *
 */
public class DeviceNotes {

	public final String DEVICE_HISTORY_KEY = "device_history_key";
	public final String DEVICE_LOCK_KEY = "device_lock_key";// 上锁
	public final String DEVICE_UNLOCK_KEY = "device_unlock_key";// 解锁
	public final String DEVICE_ALARM_KEY = "device_alarm_key";// 报警
	public final String DEVICE_SEAT_KEY = "device_seat_key";// 鞍座开启
	public final String DEVICE_AUTO_GUARD_KEY = "device_auto_guard_key";// 自动设防
	public final String DEVICE_MUTE_GUARD_KEY = "device_mute_guard_key";// 静音布防
	public final String WHEEL_R_KEY = "wheel_r_key";// 轮径
	public final String MOTOR_JDS_KEY = "motor_jds_key";// 极对数
	public final String BATTERY_TYPE_key = "battery_type_key";
	public final String MOTOR_DANGWEI = "motor_dangwei_key";//档位
	public final String USE_DEVICE_KEY = "use_device_key";
	public final String PASSWORD_KEY ="password_key";
	public final String SPEED_DANWEI_KEY ="speed_dan_wei_key";
	public final String KG_KEY ="kg_key";
	SettingShareData set;
	private Context ctx;
	DeviceHistory deviceHistory = new DeviceHistory();
	private static DeviceNotes notes;

	public static DeviceNotes getInstance() {
		return notes == null ? notes = new DeviceNotes() : notes;
	}

	private DeviceNotes() {
	}

	public void init(Context ctx) {
		this.ctx = ctx;
		set = SettingShareData.getInstance(ctx);
		readDeviceList();
	}

	private void readDeviceList() {
		String notes = set.getKeyValueString(DEVICE_HISTORY_KEY, "");
		if (!TextUtils.isEmpty(notes)) {
			deviceHistory = new Gson().fromJson(notes, DeviceHistory.class);
		}

		if (deviceHistory == null)
			deviceHistory = new DeviceHistory();

	}

	public void saveList() {
		String str = new Gson().toJson(deviceHistory);
		set.setKeyValue(DEVICE_HISTORY_KEY, str);
	}

	public void save(DeviceDB.Record record) {
		deviceHistory.add(record);
		saveList();
	}

	public int enabledLock(boolean setOrGet, int state) {
		if (setOrGet) {
			set.setKeyValue(DEVICE_LOCK_KEY, state);
		}
		return set.getKeyValueInt(DEVICE_LOCK_KEY, 1);
	}

	public int enabledUnLock(boolean setOrGet, int state) {
		if (setOrGet) {
			set.setKeyValue(DEVICE_UNLOCK_KEY, state);
		}
		return set.getKeyValueInt(DEVICE_UNLOCK_KEY, 1);
	}

	public int enabledAlarm(boolean setOrGet, int state) {
		if (setOrGet) {
			set.setKeyValue(DEVICE_ALARM_KEY, state);
		}
		return set.getKeyValueInt(DEVICE_ALARM_KEY, 1);
	}

	public int enabledSeat(boolean setOrGet, int state) {
		if (setOrGet) {
			set.setKeyValue(DEVICE_SEAT_KEY, state);
		}
		return set.getKeyValueInt(DEVICE_SEAT_KEY, 1);
	}

	public int enabledAutoGuard(boolean setOrGet, int state) {
		if (setOrGet) {
			set.setKeyValue(DEVICE_AUTO_GUARD_KEY, state);
		}
		return set.getKeyValueInt(DEVICE_AUTO_GUARD_KEY, 1);
	}
	
	public int enabledMuteGuard(boolean setOrGet, int state){
		if (setOrGet) {
			set.setKeyValue(DEVICE_MUTE_GUARD_KEY, state);
		}
		return set.getKeyValueInt(DEVICE_MUTE_GUARD_KEY, 1);
	}
	
	
	public float optWheelR(boolean setOrGet, float value){
		if (setOrGet) {
			set.setKeyValue(WHEEL_R_KEY, value);
		}
		return set.getKeyValueFloat(WHEEL_R_KEY);
	}
	
	public int optMotorJds(boolean setOrGet, int value){
		if (setOrGet) {
			set.setKeyValue(MOTOR_JDS_KEY, value);
		}
		return set.getKeyValueInt(MOTOR_JDS_KEY, 30);
	}
	
	public int opeMotorVol(boolean setOrGet, int value){
		if (setOrGet) {
			set.setKeyValue(BATTERY_TYPE_key, value);
		}
		return set.getKeyValueInt(BATTERY_TYPE_key, 60);
	}
	
	public String opeMotorDang(boolean setOrGet, String value){
		if (setOrGet) {
			set.setKeyValue(MOTOR_DANGWEI, value);
		}
		return set.getKeyValueString(MOTOR_DANGWEI, "低");
	}

	public String opePassWord(boolean setOrGet, String value){
		if (setOrGet) {
			set.setKeyValue(PASSWORD_KEY, value);
		}
		return set.getKeyValueString(PASSWORD_KEY, "000000");
	}

	public void saveUseDevice(DeviceDB.Record record){
		set.setKeyValue(DEVICE_SEAT_KEY, new Gson().toJson(record));
	}
	public String speedDanWei(boolean setOrGet, String value){
		if (setOrGet) {
			set.setKeyValue(SPEED_DANWEI_KEY, value);
		}
		return set.getKeyValueString(SPEED_DANWEI_KEY, "km");
	}

	public int optKg(boolean setOrGet, int value){
		if (setOrGet) {
			set.setKeyValue(KG_KEY, value);
		}
		return set.getKeyValueInt(KG_KEY, 60);
	}


	public void delDeviceHistory(String mac){
		if(deviceHistory != null){
			
		}
		for (DeviceDB.Record record : deviceHistory.deviceList) {
			if(record.identifier.equals(mac)){
				deviceHistory.deviceList.remove(record);
				saveList();
				return;
			}
		}
	}

	private class DeviceHistory {
		public List<DeviceDB.Record> deviceList;

		public void add(DeviceDB.Record record) {
			if (deviceList == null)
				deviceList = new ArrayList<DeviceDB.Record>();
			deviceList.add(record);
		}
	}
	
	public float getWheel(){
		float v = optWheelR(false, 1);
		float wheel ;
		if (v==10){
			wheel=0.797941f;
		}else if (v==10.5){
			wheel=0.83783805f;
		}else if (v==11){
			wheel=0.8777351f;
		}else if (v==11.5){
			wheel=0.91763215f;
		}else if (v==12){
			wheel=0.9575292f;
		}else if (v==12.5){
			wheel=0.99742625f;
		}else if (v==13){
			wheel=1.0373233f;
		}else if (v==13.5){
			wheel=1.07722035f;
		}else if (v==14){
			wheel=1.1171174f;
		}else if (v==14.5){
			wheel=1.15701445f;
		}else if (v==15){
			wheel=1.1969115f;
		}else if (v==15.5){
			wheel=1.23680855f;
		}else if (v==16){
			wheel=1.2767056f;
		}else if (v==16.5){
			wheel=1.31660265f;
		}else if (v==17){
			wheel=1.3564997f;
		}else if (v==17.5){
			wheel=1.39639675f;
		}else if (v==18){
			wheel=1.4362938f;
		}else if (v==18.5){
			wheel=1.47619085f;
		}else if (v==19){
			wheel=1.5160879f;
		}else if (v==19.5){
			wheel=1.55598495f;
		}else if (v==20){
			wheel=1.595882f;
		}else if (v==20.5){
			wheel=1.63577905f;
		}else if (v==21){
			wheel=1.6756761f;
		}else if (v==21.5){
			wheel=1.71557315f;
		}else if (v==22){
			wheel=1.7554702f;
		}else if (v==22.5){
			wheel=1.79536725f;
		}else if (v==23){
			wheel=1.8352643f;
		}else if (v==23.5){
			wheel=1.87516135f;
		}else if (v==24){
			wheel=1.9150584f;
		}else if (v==24.5){
			wheel=1.95495545f;
		}else if (v==25){
			wheel=1.9948525f;
		}
		else if (v==25.5){
			wheel=2.03474955f;
		}else if (v==26){
			wheel=2.0746466f;
		}else if (v==26.5){
			wheel=2.11454365f;
		}else if (v==27){
			wheel=2.1544407f;
		}else if (v==27.5){
			wheel=2.19433775f;
		}else if (v==28){
			wheel=2.2342348f;
		}else if (v==28.5){
			wheel=2.27413185f;
		}else if (v==29){
			wheel=2.3140289f;
		}else if (v==29.5){
			wheel=2.35392595f;
		}else if (v==30){
			wheel=2.393823f;
		}else{
			wheel = 1.2767056f;
		}
		return  wheel;
		/*switch() {
          *//*case 10: return 0.8007f;
          case 12: return 0.9577f;
          case 14: return 1.1147f;
          case 16: return 1.2717f;
          case 18: return 1.4287f;
          case 20: return 1.6014f;
          case 22: return 1.7584f;
          case 24: return 1.9154f;
          case 26: return 2.0724f;
          default: return 1.2717f;*//*
			  case 10: return 0.3333f;
			  case 12: return 0.3999f;
			  case 14: return 0.4666f;
			  case 16: return 0.5333f;
			  case 18: return 0.5999f;
			  case 20: return 0.6666f;
			  case 22: return 0.7332f;
			  case 24: return 0.7999f;
			  case 26: return 0.8666f;
			  default: return 0.5333f;
		  }*/
	}

}
