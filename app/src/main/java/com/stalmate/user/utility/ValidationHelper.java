package com.stalmate.user.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ValidationHelper {


    private static final String ERROR_MIN_PASS = "Password is short.";
    private static final String ERROR_MAX_PASS = "Password is long.";
    private static final String ERROR_MIN_PINCODE = "Password is short.";

    public static boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.toLowerCase().trim().matches(emailPattern);
    }
    /*public static boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
        return email.toLowerCase().trim().matches(emailPattern);
    }*/

    public static boolean isValidAccountNumber(String accNumber) {
        //String accountNumberPattern = "d{4}+-d{4}+-d{4}+-d{4}";
        return accNumber.length() > 0;
    }


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    public static boolean isValidPassword(EditText passwordET, String password) {

        int minLength = 6, maxLength = 20;

        if (password.length() >= minLength && password.length() <= maxLength) {
            return true;
        } else if (password.length() < minLength) {
            passwordET.setError(ERROR_MIN_PASS);
            return false;
        } else if (password.length() > maxLength) {
            passwordET.setError(ERROR_MAX_PASS);
            return false;
        } else {
            return false;
        }
    }

    public static boolean isValidPassword(String password) {
        int minLength = 6, maxLength = 20;
        return password != null && password.length() >= minLength && password.length() <= maxLength;
    }

    public static boolean isValidPinCode(EditText pinCodeEditText, String pinNumber) {
        int minLength = 6;
        if (pinNumber.length() < 6) {
            pinCodeEditText.setError(ERROR_MIN_PINCODE);
        } else {
            return false;
        }
        return true;
    }

    public static boolean isNull(String input) {
        return (input == null || input.trim().equals("") || input.length() < 1 || input.trim().equals("null"));
    }

    public static String optional(String input) {
        if (input == null || input.trim().equals("") || input.length() < 1 || input.trim().equals("null")) {
            return "";
        } else {
            return input;
        }
    }


    public static String optionalBlank(String input) {
        if (input == null || input.trim().equals("") || input.length() < 1 || input.trim().equals("null")) {
            return "";
        } else {
            return input;
        }
    }

    public static String NullPrice(String input) {
        if (input == null || input.trim().equals("") || input.length() < 1 || input.trim().equals("null")) {
            return "0.0";
        } else {
            return input;
        }
    }

    public static String optional(String input, @NonNull String optionalValue) {
        if (input == null || input.trim().equals("") || input.length() < 1 || input.trim().equals("null")) {
            return optionalValue;
        } else {
            return input;
        }
    }

    public static String input(String input){

        if(input==null||input.trim().equals("")||input.length()<1||input.trim().equals("null")){

            return "";

        }else {

            return input;
        }

    }

    public static boolean isValidNumber(String number) {

        return number == null || number.trim().equals("");
    }

    public static void showSnackBar(View view, String message) {

        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();

    }


    public static boolean licenseExpiryValidation(String input) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
        Date expiry = null;
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(simpleDateFormat.parse(input));
            c.add(Calendar.MONTH, 1);
            String expiry1 = simpleDateFormat.format(c.getTime());
            expiry = simpleDateFormat.parse(expiry1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert expiry != null;
        return expiry.before(new Date());
    }

    /*public static String simpleDateFormat(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        Date d = new Date();

        return dateFormat.format(d);
    }*/



    public static String CalendarFormat(String serverDate) {
        Date d1 = null;
        SimpleDateFormat sdf;
        SimpleDateFormat output = null;
        sdf = new SimpleDateFormat("EEEE\ndd MMM yyyy");
        output = new SimpleDateFormat("dd MMM yyyy");
        try {
            d1 = sdf.parse(String.valueOf(serverDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output.format(d1);
    }



    public static double amountFormat(double amount) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return Double.valueOf(df.format(amount));
    }




    public static String CheckHoliday(String date) {
        SimpleDateFormat sdf;
        SimpleDateFormat output = null;

        //sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf = new SimpleDateFormat("EEEE\ndd MMM yyyy");
        output = new SimpleDateFormat("yyyy-MM-dd");

        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        Date d = null;
        try {
            d = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output.format(d);
    }


}
