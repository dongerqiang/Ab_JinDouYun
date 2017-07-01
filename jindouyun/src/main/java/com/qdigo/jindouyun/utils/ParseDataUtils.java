package com.qdigo.jindouyun.utils;

import android.content.Context;
import android.util.Log;

import java.text.DecimalFormat;

import static com.qdigo.jindouyun.MyApplication.app;


/**
 * Created by jpj on 2017-05-22.
 */

public class ParseDataUtils {
    public static final String TAG = "ParseDataUtils";

    /*public static void main(String[] arg){
        byte[] data = {0x7f,0,0,0x10,0x7f,0x76,0x59,0x02,0x58,0x49,0x56,0x12,0x02,0x0f};
        System.out.print(Arrays.toString(data));
        parseARS(data);
        parseDangWei(data);
        parseDianliu(data);
        parseMile(data);
        parseRideTime(data);
        parseVoltage(data);
//        parseSpeed(data);
    }*/
    /**
     * 解析速度
     * @param data
     * @return
     */
    public static String parseSpeed(Context mContext, byte[] data){
        //霍尔计数
        int hallCount = data[6+2] & 0xff;
        hallCount<<=8;
        hallCount |= data[5+2] & 0xff;

//        float zhiJin =((int) SPUtils.get(mContext,Constant.LUN_JING_KEY,16) *0.0333f);
        float circumference = app.deviceNotes.getWheel();
        int p = app.deviceNotes.optMotorJds(false, 1);
//        int p=(int) SPUtils.get(mContext,Constant.JI_DUI_SHU_KEY,23);
//        float zhiJin =16*0.0333f;
//        int p = 23;
        float speed = (float) ((hallCount*7200*circumference*Math.PI)/(6*p*1000*3));
        String dot2String = dot2String(speed);
        Log.w(TAG,"parseSpeed == "+dot2String);
        System.out.print("parseSpeed == "+dot2String);
        return dot2String;
    }

    /**
     * 骑行时间 根据霍尔数来统计
     * @param data
     * @return
     */

    public static float parseRideTime(byte[] data){
        //霍尔计数
        int hallCount = data[5+2] & 0xff;
        hallCount<<=8;
        hallCount |= data[6+2] & 0xff;
        Log.w(TAG,"parseRideTime == "+hallCount);

        System.out.print("parseRideTime == "+hallCount);
        return hallCount;
    }

    /**
     * 解析错误码
     * @param data
     * @return
     */
    public static String parseARS(byte[] data){
        int asr = data[11+2] & 0xff;
        StringBuffer sb = new StringBuffer();

        if((asr & 0x01) != 0){
            sb.append("电机故障");
        }

        if((asr & 0x02) != 0){
            sb.append(",霍尔故障");
        }

        if((asr & 0x04) != 0){
            sb.append(",转把故障");
        }

        if((asr & 0x08) != 0){
            sb.append(",刹把故障");
        }

        if((asr & 0x10) != 0){
            sb.append(",电池欠压故障");
        }

        if((asr & 0x20) != 0){
            sb.append(",电池高温故障");
        }

        if((asr & 0x40) != 0){
            sb.append(",电池欠压故障");
        }

        if((asr &0x80) != 0){
            sb.append(",电池过冲故障");
        }
        if((asr &0xff) == 0){
            sb.append("设备正常");
        }
        Log.w(TAG,"parseARS == "+sb.toString());
        System.out.print("parseARS == "+sb.toString());
        return sb.toString();
    }

    /**
     * 解析总里程
     * @param data
     * @return
     */
    public static String parseMile(byte[] data){
        //总里程
        int mile = data[7+2] & 0xff;
        mile<<=8;
        mile |=data[8+2] &0xff;
        String dot2String = dot2String((float) mile / 10);
        Log.w(TAG,"parseMile == "+dot2String);
        System.out.print("parseMile == "+dot2String);
        return dot2String;
    }


    /**
     * 解析电压
     * @param data
     * @return
     */
    public static String parseVoltage(byte[] data){
        //电压值
        int voltage = data[1+2] & 0xff;
        voltage<<=8;
        voltage |= data[2+2] & 0xff;
        String dot2String = dot2String((float) voltage / 100);
        Log.w(TAG,"parseVoltage == "+dot2String);
        System.out.print("parseVoltage == "+dot2String);
        return dot2String;
    }

    public static String parseBattery(byte[] data){
        //电量
        int battery = data[4] & 0xff;
        return battery+ "%";
    }
    /**
     * 解析电流
     * @param data
     * @return
     */
    public static String parseDianliu(byte[] data){
        //电流值
        int dianliu = data[3+2] & 0xff;
        String dot2String = dot2String((float) dianliu / 10);
        Log.w(TAG,"parseDianliu == "+dot2String);
        System.out.print("parseDianliu == "+dot2String);
        return dot2String;
    }

    /**
     * 解析档位
     * @param data
     * @return
     */
    public static int parseDangWei(byte[] data){
        int ars = getLow4(data[9+2]);
        Log.w(TAG,"parseDangWei ==" +ars);
        System.out.print("parseDangWei ==" +ars);
        return ars;
    }

    /**
     * 解析数据
     * @param data
     */
    public static void parseData(byte[] data){
        //电压值
        int voltage = data[1+2] & 0xff;
        voltage<<=8;
        voltage |= data[2+2] & 0xff;
        //电流值
        int dianLiu = data[3+2] & 0xff;

        //霍尔计数
        int hallCount = data[5+2] & 0xff;
        hallCount<<=8;
        hallCount |= data[6+2] & 0xff;

        //总里程
        int mile = data[7+2] & 0xff;
        mile<<=8;
        mile |=data[8+2] &0xff;
        mile = mile/10;

        //电池类型及档位
        int batteryType = getHeight4(data[9+2]);

        int ars = getLow4(data[9+2]);


        //报警及报警器状态
        int baoJin = getHeight4(data[10+2]);

        int baoJinState = getLow4(data[10+2]);

        //故障码
        int errorCode = data[11+2] & 0xff;
    }

    public static int getHeight4(byte data){//获取高四位
        int height;
        height = ((data & 0xf0) >> 4);
        return height;
    }

    public static int getLow4(byte data){//获取低四位
        int low;
        low = (data & 0x0f);
        Log.w(TAG,"档位 : "+low);
        return low;
    }
     public static String dot2String(float number){
         DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
         String filesize = df.format(number);//返回的是String类型的
         return filesize;
     }

    public static String dot22String(float number){
        DecimalFormat df = new DecimalFormat("0.0");//格式化小数，不足的补0
        String filesize = df.format(number);//返回的是String类型的
        return filesize;
    }

    public static String kmToMi(String km){
        double v = Double.parseDouble(km);
        v = v*0.621371f;
        String s = dot2String((float) v);
        return s;
    }

    public static String miToKm(String mi){
        double m = Double.parseDouble(mi);
        m = m/0.621371f;
        String s = dot2String((float) m);
        return s;
    }

    /**
     * 格式化时间
     * @param time
     * @return
     */
    public static String FormatMiss(long time){
        String hh=time/3600>9?time/3600+"":"0"+time/3600;
        String mm=(time% 3600)/60>9?(time% 3600)/60+"":"0"+(time% 3600)/60;
        String ss=(time% 3600) % 60>9?(time% 3600) % 60+"":"0"+(time% 3600) % 60;
        return hh+":"+mm+":"+ss;
    }
}
