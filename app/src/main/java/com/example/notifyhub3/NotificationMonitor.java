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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
  //  public static StatusBarNotification mRemovedNotification;
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
                 //   updateCurrentNotifications();
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

    String notification_id = sbn.getKey();
    String app_package_name = sbn.getPackageName();
    String channel_id = sbn.getNotification().getChannelId();
    long arrival_time =sbn.getPostTime(); //Is set later on
    String notification_body= (String) sbn.getNotification().tickerText;
    int user_hash=sbn.getUser().hashCode();

    final String URL = "http://3.13.113.167:5000/routes/notification_posted";
    JSONArray JsonArrayNotificationPosted;
  try{
      String notif_id=(String) sbn.getKey();
      Log.d(TAG, "NOTIFICATION ID" + notif_id);

      String app_pack_name=sbn.getPackageName();
      Log.d(TAG, "APP PACKAGE NAME" + app_pack_name);

      String channelID=sbn.getNotification().getChannelId();
      Log.d(TAG, "CHANNEL ID"+ channelID);

      long arrivalTime=sbn.getPostTime();
      Log.d(TAG, " ARRIVAL TIME"+ arrivalTime);

      String notif_body=(String) sbn.getNotification().tickerText;
      Log.d(TAG, "NOTIFICATION BODY" + notif_body);

      int UserHash= sbn.getUser().hashCode();
      Log.d(TAG, " USER HASH"+UserHash);

  }
  catch (Exception exception){
      Log.d(TAG, "Error Occured" + exception);
  }

   JsonArrayNotificationPosted= new JSONArray();
   JsonItemPosted item = new JsonItemPosted(notification_id,notification_body,channel_id,app_package_name,user_hash,arrival_time);
   JSONObject jObj = null;
   try{
   jObj = toJson(item);
        } catch (JSONException e) {
        Log.d(TAG, " JSON Error" +e);

   }
   JsonArrayNotificationPosted.put(jObj);
   Log.d(TAG, "onNotificationPosted: " + jObj.toString());
   SendHttpNotificationPosted(URL, jObj);
   Log.d(TAG, "onNotificationPosted: " + JsonArrayNotificationPosted.toString());
   Toast.makeText(getApplicationContext(), JsonArrayNotificationPosted.toString(), Toast.LENGTH_SHORT).show();
   }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap, int reason) {
        JSONArray JsonArrayNotificationRemoved;
        // TO FIND DATE AND TIME
        String InteractionTime = "";
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = dateFormat.format(date);
        InteractionTime = formattedDate;
        Log.d(TAG, "onNotificationRemoved: " + sbn.getPackageName() + " Time: " + formattedDate);

        // TO FIND USER'S INTERACTION WITH THE NOTIFICATION
        String myReason = "";
        if (reason == REASON_CLICK)
            myReason = "User clicked";
        else if (reason == REASON_CANCEL)
            myReason = "User swiped";
        else if (reason == REASON_CANCEL_ALL)
            myReason = "Clear all";
        final String URLNotificationRemoved = "http://3.13.113.167:5000/routes/interaction_data";

        super.onNotificationRemoved(sbn, rankingMap, reason);
        String notification_id = sbn.getKey();
        Log.d(TAG, " NOTIFICATION ID " + notification_id);
        String app_package_name = sbn.getPackageName();
        Log.d(TAG, "APP PACKAGE NAME " + app_package_name);
        String channel_id = sbn.getNotification().getChannelId();
        Log.d(TAG, " CHANNEL ID " + channel_id);
        String interaction_time = InteractionTime;
        Log.d(TAG, " INTERACTION TIME " + interaction_time);
        String interaction_type = myReason;
        Log.d(TAG, " INTERACTION TYPE " + interaction_type);
        long arrival_time = sbn.getPostTime();
        Log.d(TAG, " ARRIVAL TIME " + arrival_time);
        int user_hash = sbn.getUser().hashCode();
        try {
            Log.d(TAG, " USER HASH " + user_hash);
        } catch (Exception e) {
            Log.d(TAG, " Error Occured " + e);
        }
        JsonArrayNotificationRemoved = new JSONArray();
        JsonItemRemoved itemRemoved = new JsonItemRemoved(notification_id, channel_id, app_package_name, user_hash, arrival_time, interaction_time, interaction_type);
        JSONObject jObj = null;
        try {
            jObj = toJson(itemRemoved);
        } catch (JSONException e) {
            Log.d(TAG, " JSON Error" + e);

        }
        JsonArrayNotificationRemoved.put(jObj);
        Log.d(TAG, "onNotificationPosted: " + jObj.toString());
        SendHttpPostNotificationRemoved(URLNotificationRemoved, jObj);
        Log.d(TAG, "onNotificationPosted: " + JsonArrayNotificationRemoved.toString());
        Toast.makeText(getApplicationContext(), JsonArrayNotificationRemoved.toString(), Toast.LENGTH_SHORT).show();

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

// HTTP PARSING FOR NOTIFICATION REMOVED
    public static JSONObject toJson(JsonItemRemoved item) throws JSONException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(item);
        JSONObject jObj = new JSONObject(json);
        return jObj;
    }

    public void SendHttpPostNotificationRemoved(String URL, JSONObject jsonBody){
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
                }}) {
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

    //HTTP REQUEST FOR NOTIFICATION POSTED

    public static JSONObject toJson(JsonItemPosted item) throws JSONException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(item);
        JSONObject jObj = new JSONObject(json);
        return jObj;
    }

    public void SendHttpNotificationPosted(String URL, JSONObject jsonBody){
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
                }}) {
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