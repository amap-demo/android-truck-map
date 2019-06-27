package com.amap.demo.truckmap.utilTools;

import android.content.Context;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;

public class utils {
    public static float  FACTOR = 360*10000F;
    public static String getDateStr(Date date, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String timeStamp2Date(long time, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    public static String times(long time) {
        SimpleDateFormat sdr = new SimpleDateFormat("HH:mm");
        String times = sdr.format(new Date(time * 1000L));
        return times;

    }


    public static byte[] openAsset(Context context, String sourceFilename) {
        byte[] bytes = null;
        try {
            InputStream ins = context.getAssets().open(sourceFilename);
            bytes = new byte[ins.available()];
            ins.read(bytes);
            ins.close();
        } catch (Throwable e){

        } finally {

        }
        return bytes;
    }

    public static long date2TimeStamp(String date, String format) {
        long ret = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            ret = sdf.parse(date).getTime()/1000;
        } catch (Exception e) {

        }
        return ret;

    }

    public static long getCurrentTime() {
        return System.currentTimeMillis()/1000;
    }
}
