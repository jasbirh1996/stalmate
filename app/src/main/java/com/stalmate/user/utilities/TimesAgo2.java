package com.stalmate.user.utilities;

import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimesAgo2 {

    public static String covertTimeToText(String dataDate,Boolean isTimeStamp) {

        String convTime = null;

        String prefix = "";
        String suffix = "ago";
        try {

            if (isTimeStamp){
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);

              //  cal.setTimeInMillis(Long.parseLong(dataDate) * 1000);
                cal.setTimeInMillis(totalTimeInMilli(dataDate));
                Log.d("alshdlasd",String.valueOf(totalTimeInMilli(dataDate)));
                dataDate = DateFormat.format("yyyy-MM-dd HH:mm:ss", cal).toString();
            }


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);
            Date nowTime = new Date();


            long dateDiff = nowTime.getTime() - pasTime.getTime();
            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);
            if (second < 60) {
               // convTime = second + " Seconds " + suffix;
                convTime = "Just Now";

            } else if (minute < 60) {
                convTime = minute + " Minutes "+suffix;
            } else if (hour < 24) {
                convTime = hour + " Hours "+suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + " Years " + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + " Months " + suffix;
                } else {
                    convTime = (day / 7) + " Week " + suffix;
                }
            } else if (day < 7) {
                convTime = day+" Days "+suffix;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }
        return convTime;
    }


    public static long totalTimeInMilli(String serverDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date d = new Date();
        try {
            d = sdf.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long weddingTime=  d.getTime();
        long currenttime= System.currentTimeMillis();
        return weddingTime;
    }



}