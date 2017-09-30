package com.xiaofu_yan.blux.smart_bike;


import android.os.Bundle;

import com.xiaofu_yan.blux.blue_guard.BlueGuardManager;
import com.xiaofu_yan.blux.blue_guard.BlueGuardServerConnection;

import java.util.UUID;

public class SmartBikeManager extends BlueGuardManager {
	private static final boolean bInDebug = false;
	private static final String TAG = "SmartBike";
	
	// Public types
	public static class Delegate {
//		public void smartBikeManagerFoundSmartBike(String identifier, String name, int nMode) {}
		public void smartBikeManagerGotSmartBike(SmartBike smartBike) {}
		public void smartBikeManagerFoundSmartBike(String identifier, String name) {}
	}

	public Delegate delegate;	
	
	// Public methods.
	public boolean scanSmartBike() {
		return super.startScan(SMART_BIKE_BROADCAST_UUID);
	}

	public boolean stopScan() {
		return super.stopScan();
	}

	
	// Private constants,78667579-9C74-44a3-917A-18BAF46C7277
	private final static String  SMART_BIKE_BROADCAST_UUID ="78667579-9C74-44a3-917A-18BAF46C7277";
//	private final static String  SMART_GUARD_BROADCAST_UUID="78667579-FEB2-4a37-8672-B67FC391F49E";
	

	// Private methods
	SmartBikeManager(BlueGuardServerConnection connection, UUID serverId, UUID clientId, Bundle data) {
		super(connection, serverId, clientId, data);
	}


	protected void onFoundBlueGuard(String identifier, String name) {
		if(delegate != null) {
			delegate.smartBikeManagerFoundSmartBike(identifier, name);
		}
	}

	protected void onGotBlueGuard(BlueGuardServerConnection conn, UUID serverId, UUID clientId, Bundle data) {
		if(delegate != null) {
			SmartBike smartBike = new SmartBike((BlueGuardServerConnection) getConnection(),
					serverId, clientId, data);
			delegate.smartBikeManagerGotSmartBike(smartBike);
		}
	}

}
