package com.stalmate.user.utilities;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PriceFormatter {

    public static String roundDecimalByTwoDigits(Object input) {
        if (input==null){
            return "";
        }

        try {
            // DecimalFormat df = new DecimalFormat("#0.00");
            // return df.format(input).replace(",", ".");
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat formatter = (DecimalFormat)nf;
            formatter.applyPattern("#0.00");
            return formatter.format(input).replace(",", ".");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String roundDecimalByOneDigit(Object input) {
        try {
            DecimalFormat df = new DecimalFormat("#0.0");
            return df.format(input).replace(",", ".");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long getAmountInCents(double amount) {
        return Math.round((amount * 100));
    }

    public static double getAmountInDollars(int amountInCents) {
        return amountInCents / 100;
    }

    public static double getAmountInDollars(double amountInCents) {
        return amountInCents / 100;
    }

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static String changeDateToTime(String serverdate) {
        if (serverdate==null){
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date d = null;
        try {
            d = sdf.parse(serverdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.format(d);
    }

    public static String visaDateFormat(String serverDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date d = null;
        try {
            d = sdf.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output.format(d);
    }




    long getDateTimeInMilliseconds() throws ParseException {
        String myDate = "2014/10/29 18:10:45";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = sdf.parse(myDate);
        long millis = date.getTime();
        return millis;
    }

    public static long totalEstinamedTimeForCounter(String serverDateWithTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
        /*    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

         */
        Date d = new Date();
        try {
            d = sdf.parse(serverDateWithTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long weddingTime=  d.getTime();
Log.d("adasdasda",String.valueOf(weddingTime));

        long currenttime= System.currentTimeMillis();

        Log.d("adasdasda",String.valueOf(currenttime));
        return weddingTime-currenttime;
    }



    public static int getNumberOFDaysBetweenTwoDates(String startDate, String endDate) {
        float daysBetween = 0;
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd",new Locale("en"));
        try {
            Date dateBefore = myFormat.parse(startDate);
            Date dateAfter = myFormat.parse(endDate);
            long difference = dateAfter.getTime() - dateBefore.getTime();
            daysBetween = (difference / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) daysBetween;
    }




    public static String dobFormat(String serverDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date d = null;
        try {
            d = sdf.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output.format(d);
    }

    public static String ymdFormatfromDmy(String serverDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date d = null;
        try {
            d = sdf.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output.format(d);
    }
    public static String getCouponDateFormat(String serverDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        Date d = null;
        try {
            d = sdf.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output.format(d);
    }




    public static String getSlotTimeFormat(String serverTime) {
String time="";
        try {


            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(serverTime);
            System.out.println(_24HourDt);
            System.out.println(_12HourSDF.format(_24HourDt));
            Log.d("popopo",_12HourSDF.format(_24HourDt));
            time=_12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;
    }


    public static Date getDateObject(String serverDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMMM-dd", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date d = null;
        try {
            d = sdf.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
            try {
                d = sdf.parse("2013-10-15");
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return d;
    }

    public static String getMonth(String serverDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMMM-dd", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("MMMM", Locale.US);
        Date d = null;
        try {
            d = sdf.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output.format(d);
    }




    public static List<String> timeArray() {
        List<String> list = new ArrayList<>();
        list.add("00:00");
        list.add("00:30");
        list.add("01:00");
        list.add("01:30");
        list.add("02:00");
        list.add("02:30");
        list.add("03:00");
        list.add("03:30");
        list.add("04:00");
        list.add("04:30");
        list.add("05:00");
        list.add("05:30");
        list.add("06:00");
        list.add("06:30");
        list.add("07:00");
        list.add("07:30");
        list.add("08:00");
        list.add("08:30");
        list.add("09:00");
        list.add("09:30");
        list.add("10:00");
        list.add("10:30");
        list.add("11:00");
        list.add("11:30");
        list.add("12:00");
        list.add("12:30");
        list.add("13:00");
        list.add("13:30");
        list.add("14:00");
        list.add("14:30");
        list.add("15:00");
        list.add("15:30");
        list.add("16:00");
        list.add("16:30");
        list.add("17:00");
        list.add("17:30");
        list.add("18:00");
        list.add("18:30");
        list.add("19:00");
        list.add("19:30");
        list.add("20:00");
        list.add("20:30");
        list.add("21:00");
        list.add("21:30");
        list.add("22:00");
        list.add("22:30");
        list.add("23:00");
        list.add("23:30");
        return list;
    }

    public static String formatMonthAndYear(String time) {
        if (time==null){
            return "";
        }
        Date dateObj = null;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy",new Locale("en"));
            dateObj = sdf.parse(time);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("MMMM dd, yyyy",new Locale("en")).format(dateObj);
    }








    public static int getDrawerDragDistance(Context context){
        int totalWidthInDp = (int)dpFromPx(context, context.getResources().getDisplayMetrics().widthPixels);
        double widthForContentView = totalWidthInDp*0.35;
        return totalWidthInDp-(int)widthForContentView;
    }

    private static float dpFromPx(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    private static int getModolus(int min){
        int mod = (min + 30)%60;
        return (min + 30) - mod;
    }

    public static String getTime(String time){

        String parts[] = time.split(":");
        int min = Integer.parseInt(parts[1]);
        int hours = Integer.parseInt(parts[0]);

        if (getModolus(min) == 0){
            int h = hours + 1;
            int m = 30;
            return h+":"+m;
        }else if (getModolus(min) == 60){
            int h = hours + 2;
            int m = 0;
            return h+":00";
        }else {
            return "00:00";
        }
    }

    public static int startCalValidation(String time){
        String parts[] = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int min = Integer.parseInt(parts[1]);
        if (hours>22){
            return 0;
        }else {
            return 0;
        }

    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }




}
