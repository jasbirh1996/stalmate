package com.stalmate.user.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.stalmate.user.utilities.Constants;


public class NotificationsBroadCast extends BroadcastReceiver {
    private NotificationCallBacks callBacks;
    public static final int ORDER_CONFIRMED = -1;
    public static final int PICK_UP=-4;
    public static final int ORDER_COMPLETED=-5;
    public static final int ORDER_CANCELLED=-6;

    Bundle notificationBundle;
    String notificationType;
    String userId;
    String title, body;
    private Context contextt;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.contextt=context;
        notificationBundle = intent.getExtras();
        if (notificationBundle != null) {
            Log.d("asdasda", new Gson().toJson(notificationBundle));
            notificationType = notificationBundle.getString("notificationType");
            try {
                userId = notificationBundle.getString("userId");

            }catch (Exception e){
                Log.d("exceppppp",e.getMessage());
            }
            title = notificationBundle.getString("title");
            body = notificationBundle.getString("body");
            if (callBacks != null && notificationType!=null) {

       /*         switch (notificationType) {
                    case Constants.order_confirmed:
                        notifyState(callBacks, ORDER_CONFIRMED, userId, title);
                        break;

                    case Constants.order_cancel:
                        notifyState(callBacks, ORDER_CANCELLED, userId, title);
                        break;

                    case Constants.order_pickup:
                        notifyState(callBacks, PICK_UP, userId, title);
                        break;

                    case Constants.order_complete:
                        notifyState(callBacks, ORDER_COMPLETED, userId, title);
                        break;



                    default:
                        break;
                }*/
            }
        }
    }
    private void notifyState(NotificationCallBacks callBack, int notificationType, String userId, String titile) {
        switch (notificationType) {

            case ORDER_CONFIRMED:
                callBack.onOrderConfirmed(userId);
                return;


            case  PICK_UP:

                callBack.onOrderPickUp(userId);
                break;

            case  ORDER_COMPLETED:

                callBack.onOrderCompleted(userId);
                break;

            case  ORDER_CANCELLED:

                callBack.onOrderCancelled(userId);
                break;


            default:
                break;
        }
    }



    public void removeListener() {
        this.callBacks = null;
    }

    public void addListener(NotificationCallBacks callbackss) {
        this.callBacks =callbackss;

    }

    public interface NotificationCallBacks {
        void onOrderConfirmed(String userId);
        void onOrderPickUp(String orderId);
        void onOrderCompleted(String orderId);
        void onOrderCancelled(String orderId);
    }

}
