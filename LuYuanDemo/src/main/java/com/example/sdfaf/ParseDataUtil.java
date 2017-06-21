package com.example.sdfaf;

import android.util.Log;

public class ParseDataUtil {
	private static final String TAG = "deq";
	private static int mWheelPairParameter = 23;
	private static int mLastTimeCounter;
	private static int mLastHallCounter;
	private static int mLastCurrentCounter;
	private static float mLastSpeed = 0;
	private static float mLastCurrent = 0;
	private static int mLastEnergyLevel = 0;

	// xiaowang
	private static int mPolePairs = 28;
	public enum BatteryType {
		BATTERY12V, BATTERY36V, BATTERY48V, BATTERY60V, BATTERY64V, BATTERY72V, BATTERY80V
	}
	private static final BatteryRange[] kBatteryTable = {
			new BatteryRange(BatteryType.BATTERY12V, 8.0f, 20.0f),    //don't care for motor cycle.
			new BatteryRange(BatteryType.BATTERY36V, 32.0f, 43.0f),
			new BatteryRange(BatteryType.BATTERY48V, 42.0f, 59.0f),
			new BatteryRange(BatteryType.BATTERY60V, 52.0f, 73.5f),
			new BatteryRange(BatteryType.BATTERY64V, 56.0f, 78.8f),
			new BatteryRange(BatteryType.BATTERY72V, 64.0f, 79.0f),
			new BatteryRange(BatteryType.BATTERY80V, 72.0f, 89.0f)
	};
	private static class BatteryRange {
		BatteryType type;
		float min;
		float max;

		BatteryRange(BatteryType battery, float minVolt, float maxVolt) {
			type = battery;
			min = minVolt;
			max = maxVolt;
		}
	}
	private static final InterpolateItem[] kTemperatureTable = {
			//NCP??XV103
			new InterpolateItem(36, 125.0f),
			new InterpolateItem(41, 120.0f),
			new InterpolateItem(46, 115.0f),
			new InterpolateItem(53, 110.0f),
			new InterpolateItem(61, 105.0f),
			new InterpolateItem(70, 100.0f),
			new InterpolateItem(81, 95.0f),
			new InterpolateItem(94, 90.0f),
			new InterpolateItem(110, 85.0f),
			new InterpolateItem(128, 80.0f),
			new InterpolateItem(151, 75.0f),
			new InterpolateItem(178, 70.0f),
			new InterpolateItem(211, 65.0f),
			new InterpolateItem(252, 60.0f),
			new InterpolateItem(302, 55.0f),
			new InterpolateItem(363, 50.0f),
			new InterpolateItem(440, 45.0f),
			new InterpolateItem(536, 40.0f),
			new InterpolateItem(656, 35.0f),
			new InterpolateItem(807, 30.0f),
			new InterpolateItem(1000, 25.0f),
			new InterpolateItem(1247, 20.0f),
			new InterpolateItem(1565, 15.0f),
			new InterpolateItem(1978, 10.0f),
			new InterpolateItem(2519, 5.0f),
			new InterpolateItem(3233, 0.0f),
			new InterpolateItem(4181, -5.0f),
			new InterpolateItem(5456, -10.0f),
			new InterpolateItem(7175, -15.0f),
			new InterpolateItem(9533, -20.0f),
			new InterpolateItem(12777, -25.0f),
			new InterpolateItem(17319, -30.0f),
			new InterpolateItem(23739, -35.0f),
			new InterpolateItem(32900, -40.0f)
	};
	private static class InterpolateItem {
		int idx;
		float v;
		InterpolateItem(int index, float value) {
			idx = index;
			v = value;
		}
	}
	//jinhui
	public enum WheelDiameter {
		DIAMETER10, DIAMETER16, DIAMETER12, DIAMETER14, DIAMETER18, DIAMETER20, DIAMETER22, DIAMETER24, DIAMETER26
	}
	private static WheelDiameter mWheelDiameter = WheelDiameter.DIAMETER16;
	private static int mControllerType = 0;

	public static void parseData(byte[] data) {
		// TODO Auto-generated method stub
		//[-40, 91, 1, 0, 0, -80, -95, 0, 0, 111, 0, 0, 0, 85, 0, 2]
		//[-39, 92, 1, 0, 0, 50, -93, 0, 0, 112, 0, 0, 0, 85, 0, 2]
		//[-42, 123, 1, 0, 0, 86, -72, 0, 0, 123, 0, 0, 0, 85, 0, 2]
		//[-42, 119, 1, 0, 0, 86, -72, 0, 0, 123, 0, 0, 0, 85, 0, 2]
		//-39,93, 1, 0, 0, -77, -92, 0, 0, 113, 0, 0, 0, 85, 0, 2
//		byte [] data ={-42, 123, 1, 0, 0, 86, -72, 0, 0, 123, 0, 0, 0, 85, 0, 2};
		
		//时间计数
		int time_counter =le2l(data, 1);
		//霍尔计数
		int hall_counter = le2l(data, 5);
		//电流计数
		int current_counter = le2l(data, 9);
		// 速度 km/h
		float speed = convert_speed(time_counter, hall_counter);
		// 里程 km
		float mileage = convert_mileage(hall_counter);
		// 电流
		float current = convert_current(time_counter, current_counter);
		//energy level
		int el = convert_el(time_counter, current_counter);
		//剩余电量百分比
		int batCap = data[13] & 0xff;
		/**
		 * 错误码 	data [14] :ars 低四位表示故障码
		 * 			int 1 ：表示转把故障
		 * 			int 2：表示刹车故障
		 * 			int 3 ：表示电机霍尔故障
		 * 			int 4 ：表示控制器故障
		 */
		int ars = data[14] & 0x0f;
		
		/**
		 * 表示车辆状态   data[15] gear
		 * 				01： 1档   最佳里程
		 * 			    02： 2档    爬坡档
		 * 				03 ：3挡   速度提升
		 */
		int gear = data[15] & 0x03;//低两位表示车辆档位
		boolean speedLimited = (data[15] & 0x04) == 0 ? false : true; //第三位 表示是否限速
		boolean cruise = (data[15] & 0x08) == 0 ? false : true;	//
		Log.i(TAG, "[time_counter:" + time_counter + "]");
		Log.i(TAG,"[hall_counter:" + hall_counter + "]");
		Log.i(TAG,"[current_counter:" + current_counter + "]");
		Log.i(TAG,"[speed:" + speed + "]");
		Log.i(TAG,"[mileage:" + mileage + "]");
		Log.i(TAG,"[el:" + el + "]");
		Log.i(TAG,"[batCap:" + batCap + "]");
		Log.i(TAG,"[ars:" + ars + "]");
		Log.i(TAG,"[gear:" + gear + "]");
		Log.i(TAG,"[speedLimited:" + speedLimited + "]");
		Log.i(TAG,"[cruise:" + cruise + "]");
		
		
		
		
		mLastTimeCounter = time_counter;
		mLastHallCounter = hall_counter;
		mLastCurrentCounter = current_counter;
		
		
	}

	/**
	 * 计算energy level
	 * @param time_counter
	 * @param current_counter
	 * @return
	 */
	public static int convert_el(int time_counter, int current_counter) {
		if(time_counter == 0)
			return mLastEnergyLevel;
		mLastEnergyLevel = current_counter / time_counter + 1;
		return mLastEnergyLevel;
	}

	/**
	 * 计算电流
	 * @param time_counter
	 * @param current_counter
	 * @return
	 */
	
	public static float convert_current(int time_counter, int current_counter) {
		if(mLastTimeCounter > time_counter || mLastCurrentCounter > current_counter) {
			mLastTimeCounter = 0;
			mLastCurrentCounter = 0;
		}
		int time = time_counter - mLastTimeCounter;
		if(time == 0)
			return mLastCurrent;
		
		int current = (current_counter - mLastCurrentCounter) / time;
		mLastCurrent = current;
		return current;
	}

	/**
	 * 计算里程
	 * @param hall_counter
	 * @return
	 */
	
	
	public  static float convert_mileage(int hall_counter) {
		double km = 0;
		// diameter10: perimeter is 1.319m	diameter12: perimeter is 1.602m	diameter16: perimeter is 1.445m
		// (Pi * D * N) / (6 * P)
		/*if (mWheelDiameter == WheelDiameter.DIAMETER10) {
			km = 1.319 / 1000.0 * hall_counter / 6.0 / mWheelPairParameter;
		} else if (mWheelDiameter == WheelDiameter.DIAMETER16) {
			km = 1.445 / 1000.0 * hall_counter / 6.0 / mWheelPairParameter;
		}*/
//		float circumference = wheelDiameterToCircumference(mWheelDiameter);
		float circumference = wheelDiameterToCircumference(mWheelDiameter);
		km = circumference / 1000.0 * hall_counter / 6.0 / mWheelPairParameter;
		return (float) (km);
	}

	/**
	 * 计算速度
	 * @param time_counter 时间计数
	 * @param hall_counter 霍尔计数
	 * @return 单位：KM/H
	 */
	public static float convert_speed(int time_counter, int hall_counter) {
		
		if(mLastTimeCounter > time_counter || mLastHallCounter > hall_counter) {
			mLastTimeCounter = 0;
			mLastHallCounter = 0;
		}
		int time = time_counter - mLastTimeCounter;
		int hall = hall_counter - mLastHallCounter;
		
		if(time == 0)
			return mLastSpeed;
		
		// time unit is in .5 second.
		double speed = convert_mileage(hall) * 2 * 3600 / time;
		System.out.println("[dT:" + time + " dHall:" + hall + "]"+"\n[ speed :" + speed + "]");
		mLastSpeed = (float) speed;
		return (float) speed;
	}

	/**
	 * 截取 [i,i+3] 低位在前
	 * @param buffer
	 * @param index
	 * @return
	 */
	public static int le2l(byte[] buffer, int index) {
		int l;
		l = buffer[index + 3];
		l = (int) ((l << 8) | (buffer[index + 2] & 0xff));
		l = (int) ((l << 8) | (buffer[index + 1] & 0xff));
		l = (int) ((l << 8) | (buffer[index] & 0xff));
		return l;
	}

	 private static float wheelDiameterToCircumference(WheelDiameter wheel) {
		 if(0 == mControllerType) {
			 switch(wheel) {
				 case DIAMETER10: return 1.319f;
				 case DIAMETER16:
				 default: return 1.445f;
			 }
		 } else {
			 switch(wheel) {
				 case DIAMETER10: return 0.8007f;
				 case DIAMETER12: return 0.9577f;
				 case DIAMETER14: return 1.1147f;
				 case DIAMETER16: return 1.2717f;
				 case DIAMETER18: return 1.4287f;
				 case DIAMETER20: return 1.6014f;
				 case DIAMETER22: return 1.7584f;
				 case DIAMETER24: return 1.9154f;
				 case DIAMETER26: return 2.0724f;
				 default: return 0.8007f;
			 }
		 }
	 }

	/* 开始-------------------------------------------wang 数据解析---------------------------
	 int batteryADC = le2s(data, 3);
	 int temperatureADC = le2s(data, 5);
	 long speed = le2s(data, 7);
	 long mileage = le2ll(data, 9);

	 batteryADC &= 0xffff;
	 temperatureADC &= 0xffff;
	 speed &= 0xffffffff;
	 mileage &= 0xffffffff;

	 float temperature = adc_get_temperature(temperatureADC);
	 speed = pulsesToMeter(mWheelDiameter, speed * 3600);
	 mileage = pulsesToMeter(mWheelDiameter, mileage * 100);

	 float batteryVoltage = adc_get_battery_voltage(batteryADC);
	 int capacity = battery_get_capacity(batteryVoltage, mBatteryType);
     */
	private static short le2s(byte[] buffer, int offset) {
		short s;
		s = buffer[offset + 1];
		s = (short) ((s << 8) | (buffer[offset] & 0xff));
		return s;
	}

	private static int le2ll(byte[] buffer, int offset) {
		int l;
		l = buffer[offset + 3];
		l = (int) ((l << 8) | (buffer[offset + 2] & 0xff));
		l = (int) ((l << 8) | (buffer[offset + 1] & 0xff));
		l = (int) ((l << 8) | (buffer[offset] & 0xff));
		return l;
	}

	private float adc_get_temperature(int mv) {
		//Vadc = Vcc * Rx / (200kOhm + Rx) : Vcc = 3300mv
		//-> Vadc * 200kOhm + Vadc * Rx = Vcc * Rx
		//-> Rx(Vcc - Vadc) = 200kOhm * Vadc
		//-> Rx = 200kOhm * Vadc / (Vcc - Vadc)
		float temperature = -1;
		float rx;

		rx = (float) mv * 200 / (3300 - mv);
		if (rx > 0)
			temperature = interpolate((int) (rx * 100), kTemperatureTable);

		return temperature;
	}

	/**
	 * wang 获得电池电压
	 * @param mv
	 * @return
     */
	private static float adc_get_battery_voltage(int mv) {
		double vBat = mv;

		//Vadc = Vbat * 10kOhm / (1000kOhm + 10kOhm) : Vbat = 36v, 48v, 60v, 64v, 72v
		//Vadc = Vbat / 101;
		//Vbat = 101 * Vadc
		vBat /= 1000;
		vBat = (vBat * 101) + 0.7;

		return (float) vBat;
	}

	/**
	 * wang 获得电池电压（通过电池电压 和电池类型获得）
	 * @param voltage
	 * @param type
     * @return
     */
	private int battery_get_capacity(float voltage, BatteryType type) {
		for (int i = 0; i < kBatteryTable.length; i++) {
			if (kBatteryTable[i].type == type) {
				return (int) ((voltage - kBatteryTable[i].min) * 100 / (kBatteryTable[i].max - kBatteryTable[i].min));
			}
		}
		return 50;  //battery type not found.
	}


	private static long pulsesToMeter(WheelDiameter wheel, long pulses) {
		double meter = 0;
		// 28 pulses / circle
		// diameter10: perimeter is 1.319m	diameter12: perimeter is 1.602m	diameter16: perimeter is 1.445m
		/*if (wheel == WheelDiameter.DIAMETER10) {
			meter = pulses / 28.0 * 1.319;
		} else if (wheel == WheelDiameter.DIAMETER16) {
			meter = pulses / 28.0 * 1.445;
		}*/
		float circumference = wheelDiameterToCircumference(wheel);
		meter = pulses / mPolePairs * circumference;
		return (long) meter;
	}

	private float interpolate(int idx, InterpolateItem[] table) {
		float value = table[0].v;
		float last_value = table[0].v;
		int index = table[0].idx;
		int last_index = table[0].idx;

		for (int i = 0; i < table.length; i++) {
			value = table[i].v;
			index = table[i].idx;
			if (idx < table[i].idx) {
				break;
			}
			last_value = value;
			last_index = index;
		}
		if (index > last_index) {
			value = last_value + (value - last_value) * (idx - last_index) / (index - last_index);
		}
		return value;
	}

		/* 开始-------------------------------------------wang 数据解析 结束---------------------------*/
}
