package com.liar.testrecorder.utils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by LiarShi on 2020/5/26.
 */
public class TimeUtils {


    public static String getGapTime(long time){

        //使用中国时区的有时差8小时，所以要先扣掉 对应时区的时差
        time=time-TimeZone.getDefault().getRawOffset();
        //初始化Formatter的转换格式。
        SimpleDateFormat format =new SimpleDateFormat("HH:mm:ss");
        return format.format(time);
//       long hours = time / (1000 * 60 * 60);
//       long minutes = (time-hours*(1000 * 60 * 60 ))/(1000* 60);
//       String diffTime="";
//       if(minutes<10){
//          diffTime=hours+":0"+minutes;
//       }else{
//        diffTime=hours+":"+minutes;
//       }
//       return diffTime;

    }


}
