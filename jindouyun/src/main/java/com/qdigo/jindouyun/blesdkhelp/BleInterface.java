package com.qdigo.jindouyun.blesdkhelp;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.qdigo.jindouyun.MyApplication;
import com.qdigo.jindouyun.R;
import com.qdigo.jindouyun.activity.MainActivity;
import com.qdigo.jindouyun.utils.BroadcastUtils;
import com.qdigo.jindouyun.utils.DeviceDB;
import com.qdigo.jindouyun.utils.ParseDataUtils;
import com.xiaofu_yan.blux.blue_guard.BlueGuard;
import com.xiaofu_yan.blux.le.server.BluxSsServer;
import com.xiaofu_yan.blux.smart_bike.SmartBike;
import com.xiaofu_yan.blux.smart_bike.SmartBikeManager;
import com.xiaofu_yan.blux.smart_bike.SmartBikeServerConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.qdigo.jindouyun.MyApplication.app;

/***
 * 蓝牙接口通讯
 * @author fu
 *
 */
public class BleInterface {
	public SmartBikeServerConnection mConnection;
	public SmartBikeManager mBlueGuardManager;
	public SmartBike mSmartBike;
	private List<IBlueCallback> blueCallbacks = new ArrayList<IBlueCallback>();
	private Context mCtx;
	
	private DeviceDB.Record lastDevice;
	
	private static BleInterface bleInterface;
	public static BleInterface getInstance(){
		return bleInterface == null ? bleInterface = new BleInterface() : bleInterface;
	}
	
	private BleInterface(){
		
	}
	
	public void initBle(Context context){
		mCtx = context;
		mConnection = new SmartBikeServerConnection();
		mConnection.delegate = new ServerConnectionDelegate();
		
		lastDevice = DeviceDB.load(mCtx);
		
				
	}
	
	public void disconverBleDevice(){
		if(mBlueGuardManager != null){
			mBlueGuardManager.scanSmartBike();
		}
	}
	
	public void closeDisconverBleDevice(){
		if(mBlueGuardManager != null){
			mBlueGuardManager.stopScan();
		}
	}
	
	public void connectDevice(String mac){
		if(mBlueGuardManager != null){
			mBlueGuardManager.getDevice(mac);
		}
	}
	
	public void disConnectDevice(){
		if(!isSmartBikeAvailable()) return;
		mSmartBike.cancelConnect();
	}

	public boolean isScanning(){
		return mBlueGuardManager ==null?false:mBlueGuardManager.isScanning();
	}
	
	public void lock(){
		if(!isSmartBikeAvailable()) return;
		if(!isRunning()){
			mSmartBike.setState(BlueGuard.State.ARMED);
		}else{
			app.showToast("行驶中，无法使用遥控功能!");
		}
		
	}
	
	public void unlock(){
		if(!isSmartBikeAvailable()) return;
		if(!isRunning()){
			mSmartBike.setState(BlueGuard.State.STOPPED);
		}else{
			 app.showToast("行驶中，无法使用遥控功能!");
		}
		
	}
	
	public void openSeat(){
		if(!isSmartBikeAvailable()) return;
		/*if(!isRunning()){
			mSmartBike.openTrunk();
		}else{
			 MyApplication.app.showToast("行驶中，无法使用遥控功能!");
		}*/
		mSmartBike.openTrunk();
	}
	
	public void start(){
		if(!isSmartBikeAvailable()) return;
		if(!isRunning()){
			mSmartBike.setState(BlueGuard.State.STARTED);
		}else{
			 app.showToast("行驶中，无法使用遥控功能!");
		}
	}
	
	public void stop(){
		if(!isSmartBikeAvailable()) return;
		if(!isRunning()){
			mSmartBike.setState(BlueGuard.State.STOPPED);
		}else{
			 app.showToast("行驶中，无法使用遥控功能!");
		}
	}

	public void setPair(String pair){
		if(mSmartBike != null&& mSmartBike.connected()){
			MyApplication.logBug("--setPair  ="+ pair);
			app.deviceNotes.opePassWord(true,pair);
			mSmartBike.setPairPasskey(Integer.decode(pair));
			DeviceDB.save(mCtx,lastDevice);
		}else{
			app.deviceNotes.opePassWord(true,pair);
		}
	}
	public void setSmartBikeArmConfigTrue(){
		if(mSmartBike != null){
			mSmartBike.setAlarmConfig(true, true);
		}
	}
	
	public void setSensity(int dang){
		if(!isSmartBikeAvailable()) return;
		setSmartBikeArmConfigTrue();
		String sensitive = app.deviceNotes.opeMotorDang(true, "低");

		if(mSmartBike != null){
			mSmartBike.setShockSensitivity(dang);
		}
	}


	
	public void findCarAlarm(){
		if(!isSmartBikeAvailable()) return;
		/*if(!isRunning()){
			mSmartBike.playSound(BlueGuard.Sound.FIND);
		}else{
			 MyApplication.app.showToast("行驶中，无法使用遥控功能!");
		}*/
		mSmartBike.playSound(BlueGuard.Sound.FIND);
	}
	
	public boolean isSmartBikeAvailable(){
		return mSmartBike != null && mSmartBike.connected();
	}

	public SmartBike getSmartBike(){
		return mSmartBike;
	}
	
	
	protected boolean isRunning(){
		if((mSmartBike.state() == BlueGuard.State.RUNNING) /*|| (mSmartBike.state() == BlueGuard.State.STARTED)*/){
			return true;
		}else{
			return false;
		}
	}
	public  void startSmartBikeService(Context context){		
		Notification notification;
		Notification.Builder nb = new Notification.Builder(context);
		nb.setSmallIcon(R.drawable.logo);
		nb.setTicker("筋斗云");
		nb.setContentTitle("筋斗云");
		nb.setContentText("蓝牙服务正在运行!");
		notification = nb.build();
		Intent settingViewIntent = new Intent();
		settingViewIntent.setClass(context, MainActivity.class);
		notification.contentIntent = PendingIntent.getActivity(context, 0, settingViewIntent, 0);
		System.out.println("---------------------startBluxSsServer");
		BluxSsServer.sharedInstance().setForeGroundNotification(notification);
		BluxSsServer.sharedInstance().start(context);
	}
	
	public void binder(Activity activity){
		if(mBlueGuardManager == null){
			boolean b = mConnection.connect(activity);
			MyApplication.logBug(b ? "mConnection success" :"mConnection fail" );
		}
	}
	
	public void addBlueCallback(IBlueCallback blueCallback) {
		blueCallbacks.add(blueCallback);
	}
	
	public void removeBlueCallBack(IBlueCallback blueCallback){
		blueCallbacks.remove(blueCallback);
	}
	
	// BluxConnection delegate
	class ServerConnectionDelegate extends SmartBikeServerConnection.Delegate {
		@Override
		public void smartBikeServerConnected(SmartBikeManager smartBikeManager) {
			MyApplication.logBug("binder success");
			mBlueGuardManager = smartBikeManager;
			mBlueGuardManager.delegate = new SmartBikeManagerDelegate();

			if(lastDevice != null){
				if(!TextUtils.isEmpty(lastDevice.identifier)){
					mBlueGuardManager.getDevice(lastDevice.identifier);
				}
			}
		}
	}

	// SmartGuardManager delegate
	class SmartBikeManagerDelegate extends SmartBikeManager.Delegate {
		@Override
		public void smartBikeManagerFoundSmartBike(String identifier, String name) {
			if(!TextUtils.isEmpty(identifier)){
				MyApplication.logBug("device identifier:"+identifier);
			}
			
			if(!TextUtils.isEmpty(name)){
				MyApplication.logBug("device name:"+name);
			}
			
			DeviceDB.Record rec = new DeviceDB.Record(name, identifier, null);
			for(IBlueCallback back : blueCallbacks){
				back.discoverDevice(rec);
			}
				
		}

		@Override
		public void smartBikeManagerGotSmartBike(SmartBike smartBike) {
			MyApplication.logBug("smartBikeManagerGotSmartBike");
			mSmartBike = smartBike;
			mSmartBike.delegate = new SmartBikeDelegate();
			lastDevice = DeviceDB.load(mCtx);
			if(lastDevice != null){
				if(!TextUtils.isEmpty(lastDevice.key)){
					mSmartBike.setConnectionKey(lastDevice.key);
					mSmartBike.connect();
				}else{
					// TODO: 2017-06-26  
					MyApplication.logBug("pair code === "+app.deviceNotes.opePassWord(false, ""));
					mSmartBike.pair(Integer.decode(app.deviceNotes.opePassWord(false, "")));
				}
			}else{
				// TODO: 2017-06-26  
				MyApplication.logBug("pair code === "+app.deviceNotes.opePassWord(false, ""));
				mSmartBike.pair(Integer.decode(app.deviceNotes.opePassWord(false, "")));
			}
			
		}

		
	}
	

	// SmartBike delegate
	private class SmartBikeDelegate extends SmartBike.Delegate {
		@Override
		public void blueGuardConnected(BlueGuard blueGuard) {
            mSmartBike.getAccountManager(); //
            app.broadUtils.sendBleState(BroadcastUtils.BLE_CONNECTED);
			//参数置0
			currentTime = System.currentTimeMillis();
			currentMileage=0;
			runningTime = 0 ;
            runningMileage=0;
			ParseDataUtils.lastTime = 0;
			setPair(app.deviceNotes.opePassWord(false,""));
		}
	
		@Override
		public void blueGuardDisconnected(BlueGuard blueGuard, BlueGuard.DisconnectReason reason) {
			 app.broadUtils.sendBleState(BroadcastUtils.BLE_DISCONNECT);
			if(reason == BlueGuard.DisconnectReason.ERROR_PERMISSION||reason == BlueGuard.DisconnectReason.ERROR_KEY){
				if(lastDevice !=null){
					lastDevice.key = "";
//					 lastDevice.identifier="";
					DeviceDB.save(mCtx,lastDevice);
					lastDevice = DeviceDB.load(mCtx);
				}
				if(reason == BlueGuard.DisconnectReason.ERROR_PERMISSION){
					MyApplication.app.showToast("请重新连接!");
//						Toast.makeText(app, "请手动打开配对模式", Toast.LENGTH_SHORT).show();
				}else if(reason == BlueGuard.DisconnectReason.ERROR_KEY){
					MyApplication.app.showToast("开启配对后连接！");
//					 Toast.makeText(app, "error key", Toast.LENGTH_SHORT).show();
				}
			}else if(reason == BlueGuard.DisconnectReason.LINK_LOST){
				MyApplication.app.showToast("距离太远，失去连接！");
				if(lastDevice != null){
					if(!TextUtils.isEmpty(lastDevice.key)){
						mSmartBike.setConnectionKey(lastDevice.key);
						mSmartBike.connect();
					}else{
//						mSmartBike.pair(Integer.decode("000000"));
						MyApplication.logBug("pair code === "+app.deviceNotes.opePassWord(false, ""));
						mSmartBike.pair(Integer.decode(app.deviceNotes.opePassWord(false, "")));
					}
				}
			}else{
				MyApplication.app.showToast("连接失败");
			}
			/*if(reason == BlueGuard.DisconnectReason.ERROR_PERMISSION){
				lastDevice.key = "";
				lastDevice.name = "";
				lastDevice.identifier ="";
				DeviceDB.save(mCtx,lastDevice);
			}*/
		}

		@Override
		public void blueGuardName(BlueGuard blueGuard, String name) {
			
		}

		@Override
		public void blueGuardState(BlueGuard blueGuard, BlueGuard.State state) {
			
		}
		int currentDangWei = 1;
		float runningMileage=0;
        float currentMileage = 0;
		long runningTime = 0 ;
		long currentTime = System.currentTimeMillis();
		@Override
		public void smartBikeUpdateData(SmartBike smartBike, byte[] data) {
		//	[-56, -57, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 84, 0, 2]
            Log.w("ParseDataUtils","data  == "+ Arrays.toString(data));
			long dataTime = System.currentTimeMillis();
			if(dataTime>currentTime){
				runningTime = (runningTime+(dataTime-currentTime));
				MyApplication.logBug("--increase time ="+(runningTime)+" ms");
				MyApplication.app.broadUtils.sendMileageRunningTime(ParseDataUtils.FormatMiss(runningTime/1000));

			}else{
				runningTime = 0;
			}

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < data.length; i++) {
				if(i == data.length -1){
					sb.append(Integer.toHexString(data[i]));
				}else{
					sb.append(Integer.toHexString(data[i])+":");
				}
				
			}
			
			MyApplication.logBug("data="+sb.toString());

			String mile = ParseDataUtils.parseMile1(data);
			MyApplication.app.broadUtils.sendMileageDm(mile);
			float parseFloat = Float.parseFloat(mile);//km
			String error =ParseDataUtils.parseARS(data);
			String speed = ParseDataUtils.parseSpeed(mCtx,data);//  km/h
			/*if(dataTime>=currentTime){
				runningMileage += Float.parseFloat(speed)*(dataTime-currentTime)/3600/1000/3;
				MyApplication.app.broadUtils.sendMileageDm(ParseDataUtils.dot3String(runningMileage));
				MyApplication.logBug("--increase mile =  "+runningMileage+" -----------------");
			}*/



			int dangwei = ParseDataUtils.parseDangWei(data);
			mSmartBike.getShockSensitivity();
			Log.w("ParseDataUtils","dangwei  == "+ dangwei+"\n currentTime == "+currentDangWei);
            app.broadUtils.sendDangWei(dangwei);
            app.broadUtils.sendBattery(ParseDataUtils.parseVoltage(data));
			app.broadUtils.sendMileage(mile);
			app.broadUtils.sendSpeed(speed);

			app.broadUtils.sendArs(error);
			currentDangWei = dangwei;
			currentMileage = parseFloat;
			currentTime = dataTime;
		}


		@Override
		public void blueGuardPairResult(BlueGuard blueGuard, BlueGuard.PairResult result, String key) {
			MyApplication.logBug("blueGuardPairResult == result = "+result+"; key = "+key);
			if(result == BlueGuard.PairResult.SUCCESS){
				DeviceDB.Record rec = new DeviceDB.Record(blueGuard.name(), blueGuard.identifier(), key);
				if(key == null){
					key = "";
				}
				DeviceDB.save(mCtx, rec);
				lastDevice = DeviceDB.load(mCtx);
			}else if(result == BlueGuard.PairResult.ERROR_KEY){
				if(lastDevice!=null){
					lastDevice.key ="";
					DeviceDB.save(mCtx, lastDevice);
				}
			}else{
				DeviceDB.Record rec = new DeviceDB.Record(blueGuard.name(), blueGuard.identifier(), "");
				DeviceDB.save(mCtx, rec);
				lastDevice = DeviceDB.load(mCtx);
//				mSmartBike.pair(Integer.decode("000000"));
			}
			MyApplication.logBug("blueGuardPairResult === name-"+lastDevice.name+";identifier - "+lastDevice.identifier+"; key - "+lastDevice.key);

		}

		@Override
		public void blueGuardPairPasskey(BlueGuard blueGuard, String passkey) {
			MyApplication.logBug("blueGuardPairPasskey  === "+passkey);
//			app.deviceNotes.opePassWord(true,passkey);
		}
	}

	public void setAutoArmRangePercent(boolean state){
		if(!isSmartBikeAvailable()) return;
		if(!isRunning()){
			if(state){
				mSmartBike.setAutoArmRangePercent(50);
			}else{
				mSmartBike.setAutoArmRangePercent(-1);
			}
		}

	}

	public  void closeVoice(){
		if(mSmartBike != null){
			mSmartBike.setAlarmConfig(false, true);
		}else{
			MyApplication.app.showToast( "设备未连接！");
		}
	}

	public  void openVoice(){
		if(mSmartBike != null){
			mSmartBike.setAlarmConfig(true, true);
		}else{
			MyApplication.app.showToast( "设备未连接！");
		}
	}

	public void setClientArm(){
		if(!isSmartBikeAvailable()) return;
		if(!isRunning()){
			closeVoice();
			lock();
		}
	}

}
