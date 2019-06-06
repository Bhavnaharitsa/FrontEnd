package com.example.notifyhub3;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RemoteController;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotificationMonitor extends NotificationListenerService implements RemoteController.OnClientUpdateListener {
    private static final String TAG = "SevenNLS";
    private static final String TAG_PRE = "[" + NotificationMonitor.class.getSimpleName() + "] ";
    private static final int EVENT_UPDATE_CURRENT_NOS = 0;
    public static final String ACTION_NLS_CONTROL = "com.example.notifyhub3.NLSCONTROL";
    public static List<StatusBarNotification[]> mCurrentNotifications = new ArrayList<StatusBarNotification[]>();
    public static int mCurrentNotificationsCounts = 0;
    public static StatusBarNotification mPostedNotification;
    public static StatusBarNotification mRemovedNotification;
    private CancelNotificationReceiver mReceiver = new CancelNotificationReceiver();
    private static final int VERSION_SDK_INT = Build.VERSION.SDK_INT;

    public static boolean supportsNotificationListenerSettings() {
        return VERSION_SDK_INT >= 19;
    }

    private Handler mMonitorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_UPDATE_CURRENT_NOS:
                    updateCurrentNotifications();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClientChange(boolean clearing) {

    }

    @Override
    public void onClientPlaybackStateUpdate(int state) {

    }

    @Override
    public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed) {

    }

    @Override
    public void onClientTransportControlUpdate(int transportControlFlags) {

    }

    @Override
    public void onClientMetadataUpdate(RemoteController.MetadataEditor metadataEditor) {

    }

    class CancelNotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action;
            if (intent != null && intent.getAction() != null) {
                action = intent.getAction();
                if (action.equals(ACTION_NLS_CONTROL)) {
                    String command = intent.getStringExtra("command");
                    String packageName = intent.getStringExtra("packagename");
                    if (TextUtils.equals(command, "cancel_last")) {
                        if (mCurrentNotifications != null && mCurrentNotificationsCounts >= 1) {
                            StatusBarNotification sbnn = getCurrentNotifications()[mCurrentNotificationsCounts - 1];
                            cancelNotification(sbnn.getPackageName(), sbnn.getTag(), sbnn.getId());
                        }
                    } else if (TextUtils.equals(command, "cancel_all")) {
                        cancelAllNotifications();
                    } else if (TextUtils.equals(command, "get_others")) {

                    }
                }
            }
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        logNLS("onCreate...");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NLS_CONTROL);
        registerReceiver(mReceiver, filter);
        mMonitorHandler.sendMessage(mMonitorHandler.obtainMessage(EVENT_UPDATE_CURRENT_NOS));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // a.equals("b");
        logNLS("onBind...");
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
    String notifId = "" + sbn.getId();
    String appName = sbn.getPackageName();
    String channelId = sbn.getNotification().getChannelId();
    String channelName = "dummy_channel_name"; //figure out later
    String channelType = "dummy_channel_type" ; // dummy data as of now

    String arrivalTime = ""; //Is set later on
    String userName = "dummy_user";
    String mobileNum = "9876" ;
    String gender = "A";
    String age = "10";
    String appId = "";

    final String URL = "http://ec2-18-221-242-64.us-east-2.compute.amazonaws.com:5000/notification_event_data";
    JSONArray mJsonArray;
   try {
       StatusBarNotification[] activeNotifications = getActiveNotifications();
       Log.d(TAG, "onNotificationPosted: " + getPackageName() + "Active Notifications" + activeNotifications);
   }
   catch (Exception e){}
   try {
       String text = (String) sbn.getNotification().tickerText;
       Log.d(TAG, "onNotificationsPosted: " + getPackageName() + "Key " + text);
   }
   catch (Exception e){}
   try {
       String getKey = (String) sbn.getKey();
       Log.d(TAG, "onNotificationsPosted: " + getPackageName() + "Key " + getKey);
   }
   catch (Exception e){}
   try {
       StatusBarNotification clonedObject = sbn.clone();
       Log.d(TAG, " onNotificationsPosted: " + getPackageName() + "Clone Data " + clonedObject);
   }
   catch (Exception e){}
   try {
       int describeObject = sbn.describeContents();
       Log.d(TAG, "onNotificationsPxosted: " + getPackageName() + "Clone Data " + describeObject);
   }
   catch (Exception e){}
        updateCurrentNotifications();
   try {
       logNLS("onNotificationPosted...");
       logNLS("have " + mCurrentNotificationsCounts + " active notifications");
       mPostedNotification = sbn;
   }
   catch (Exception e){}
   try {

       Calendar cal = Calendar.getInstance();
       Date date = cal.getTime();
       DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
       DateFormat dateFormat = new SimpleDateFormat("yyyy / MM / dd ");
       String formattedTime = timeFormat.format(date);
       String formattedDate = dateFormat.format(date);
       arrivalTime = formattedTime;
       Log.d(TAG, "onNotificationPosted: " + sbn.getPackageName() + " Time: " + formattedTime + " Date: " + formattedDate);

       mJsonArray = new JSONArray();
       JsonItem item = new JsonItem(
               notifId, appId, appName, channelId, channelName, channelType,
               arrivalTime, userName, mobileNum, gender, age);
       JSONObject jObj = toJson(item);
       mJsonArray.put(jObj);

       sendPOST(URL, jObj);

       Log.d(TAG, "onNotificationPosted: " + mJsonArray.toString());
       Toast.makeText(getApplicationContext(), mJsonArray.toString(), Toast.LENGTH_SHORT).show();
   }
   catch (Exception e){}

        //display(strDate);
        /*
         * Bundle extras = sbn.getNotification().extras; String
         * notificationTitle = extras.getString(Notification.EXTRA_TITLE);
         * Bitmap notificationLargeIcon = ((Bitmap)
         * extras.getParcelable(Notification.EXTRA_LARGE_ICON)); Bitmap
         * notificationSmallIcon = ((Bitmap)
         * extras.getParcelable(Notification.EXTRA_SMALL_ICON)); CharSequence
         * notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);
         * CharSequence notificationSubText =
         * extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
         * Log.i("SevenNLS", "notificationTitle:"+notificationTitle);
         * Log.i("SevenNLS", "notificationText:"+notificationText);
         * Log.i("SevenNLS", "notificationSubText:"+notificationSubText);
         * Log.i("SevenNLS",
         * "notificationLargeIcon is null:"+(notificationLargeIcon == null));
         * Log.i("SevenNLS",
         * "notificationSmallIcon is null:"+(notificationSmallIcon == null));
         */
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap, int reason) {
        super.onNotificationRemoved(sbn, rankingMap, reason);
        try {
            String context = (String) sbn.getNotification().tickerText;
            Log.d(TAG, "onNotificationRemoved: " + getPackageName() + "Key " + context);
        } catch (Exception e) {
        }
        try {
            String otherMetrics = (String) sbn.getKey();
            Log.d(TAG, "onNotificationRemoved: " + getPackageName() + "Key " + otherMetrics);
        } catch (Exception e) {
        }
        try {
            StatusBarNotification clonedObject = sbn.clone();
            Log.d(TAG, "onNotificationRemoved: " + getPackageName() + "Clone Data " + clonedObject);
        } catch (Exception e) {
        }
        try {
            int describeObject = sbn.describeContents();
            Log.d(TAG, "onNotificationRemoved: " + getPackageName() + "Clone Data " + describeObject);
        } catch (Exception e) {
        }
        updateCurrentNotifications();
        try {
            logNLS("removed...");
            logNLS("have " + mCurrentNotificationsCounts + " active notifications");
            mRemovedNotification = sbn;
        } catch (Exception e) {
        }

        Intent intent = new Intent(ACTION_NLS_CONTROL);
        intent.putExtra("notificationremoved", sbn.getPackageName());
        sendBroadcast(intent);
        try {
            Calendar cal = Calendar.getInstance();
            Date date = cal.getTime();
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String formattedDate = dateFormat.format(date);
            Log.d(TAG, "onNotificationRemoved: " + sbn.getPackageName() + " Time: " + formattedDate);
        } catch (Exception e) {
        }

        try {
            String myReason = "";
            if (reason == REASON_CLICK)
                myReason = "User clicked";
            else if (reason == REASON_CANCEL)
                myReason = "User swiped";
            else if (reason == REASON_CANCEL_ALL)
                myReason = "Clear all";

            Log.d(TAG, "onNotificationRemoved: " + sbn.getPackageName() + " " + myReason);
        } catch (Exception e) {
        }
    }

    private void updateCurrentNotifications() {
        try {
            StatusBarNotification[] activeNos = getActiveNotifications();
            if (mCurrentNotifications.size() == 0) {
                mCurrentNotifications.add(null);
            }
            mCurrentNotifications.set(0, activeNos);
            mCurrentNotificationsCounts = activeNos.length;
        } catch (Exception e) {
            logNLS("Should not be here!!");
            e.printStackTrace();
        }
    }

    public static StatusBarNotification[] getCurrentNotifications() {
            if (mCurrentNotifications.size() == 0) {
                logNLS("mCurrentNotifications size is ZERO!!");
                return null;
            }
            return mCurrentNotifications.get(0);
    }

    private static void logNLS(Object object) {
        Log.i(TAG, TAG_PRE + object);
    }

    @SuppressLint("InlinedApi")
    @TargetApi(19)
    public static Intent getIntentNotificationListenerSettings()
    {
        final String ACTION_NOTIFICATION_LISTENER_SETTINGS;
        if (VERSION_SDK_INT >= 22)
        {
            ACTION_NOTIFICATION_LISTENER_SETTINGS = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
        }
        else
        {
            ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
        }

        return new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
    }

    public static JSONObject toJson(JsonItem item){
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("notif_id", item.getNotificationId());
            jObj.put("app_id", item.getAppId());
            jObj.put("app_name", item.getAppName());
            jObj.put("channel_id", item.getChannelId());
            jObj.put("channel_name", item.getChannelName());
            jObj.put("arrival_time", item.getArrivalTime());
            jObj.put("user_name", item.getUserName());
            jObj.put("mobile_number", item.getMobileNumber());
            jObj.put("gender", item.getGender());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObj;
    }

    public void sendPOST(String URL, JSONObject jsonBody){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {

                        responseString = String.valueOf(response.statusCode);

                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}