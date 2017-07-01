package com.qdigo.jindouyun.utils;

import android.content.Context;
import android.content.Intent;

public class BroadcastUtils {
	public static final String BLE_CONNECT_STATE = "ble_connect_state";
	public static final int BLE_CONNECTED = 1;
	public static final int BLE_DISCONNECT = 0;
	public static final String KEY_BLE_STATE = "ble_state";
	
	public static final String MILEAGE_ACTION = "mileage_action";
	public static final String MILEAGE_VALUE_KEY = "mileage_value_key";
	public static final String SPEED_VALUE_KEY = "speed_value_key";
	public static final String BATTERY_VALUE_KEY = "battery_value_key";
	public static final String MILEAGE_VALUE_INCREASE_KEY = "mileage_increase_value_key";
	public static final String DANGWEI_KEY ="dang_wei_key";
	public static final String ARS_CODE_KEY ="ars_code_key";
	public static final String RUNNING_TIME_KEY="running_time_key";
	private Context ctx;
	private static BroadcastUtils broadcastUtils;
	public static BroadcastUtils getInstance(){
		return broadcastUtils == null ? broadcastUtils = new BroadcastUtils() : broadcastUtils;
	}
	
	private BroadcastUtils(){}
	
	public void init(Context ctx){
		this.ctx = ctx;
	}
	
	public void sendBleState(int state){
		Intent intent = new Intent(BLE_CONNECT_STATE);
		intent.putExtra(KEY_BLE_STATE, state);
		ctx.sendBroadcast(intent);
	}
	
	public void sendMileage(String value){
		Intent intent = new Intent(MILEAGE_ACTION);
		intent.putExtra(MILEAGE_VALUE_KEY, value);
		ctx.sendBroadcast(intent);
	}
	
	public void sendSpeed(String value){
		Intent intent = new Intent(MILEAGE_ACTION);
		intent.putExtra(SPEED_VALUE_KEY, value);
		ctx.sendBroadcast(intent);
	}
	
	public void sendBattery(String value){
		Intent intent = new Intent(MILEAGE_ACTION);
		intent.putExtra(BATTERY_VALUE_KEY, value);
		ctx.sendBroadcast(intent);
	}
	
	public void sendMileageDm(String value){
		Intent intent = new Intent(MILEAGE_ACTION);
		intent.putExtra(MILEAGE_VALUE_INCREASE_KEY, value);
		ctx.sendBroadcast(intent);
	}

	public void sendDangWei(int dangwei) {
		Intent intent = new Intent(MILEAGE_ACTION);
		intent.putExtra(DANGWEI_KEY, dangwei);
		ctx.sendBroadcast(intent);
	}

	public void sendArs(String ars) {
		Intent intent = new Intent(MILEAGE_ACTION);
		intent.putExtra(ARS_CODE_KEY, ars);
		ctx.sendBroadcast(intent);
	}

	public void sendMileageRunningTime(String time){
		Intent intent = new Intent(MILEAGE_ACTION);
		intent.putExtra(RUNNING_TIME_KEY, time);
		ctx.sendBroadcast(intent);
	}
}
