package com.qdigo.jindouyun.blesdkhelp;
import com.qdigo.jindouyun.utils.DeviceDB;
public abstract class IBlueCallback {
	public void discoverDevice(DeviceDB.Record record){}
	public void deviceUpdate(int speed,int battery){}
}
