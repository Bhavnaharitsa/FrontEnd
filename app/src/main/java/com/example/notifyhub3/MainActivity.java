package com.example.notifyhub3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ItemFilterListener;
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemTouchCallback, ItemFilterListener<NotificationView> {
    final TextView textView = (TextView) findViewById(R.id.text);
// ...

    // Instantiate the RequestQueue.
    RequestQueue queue = Volley.newRequestQueue(this);
    String url ="http://www.google.com";

    // Request a string response from the provided URL.
    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.
                    textView.setText("Response is: "+ response.substring(0,500));
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            textView.setText("That didn't work!");
        }
    });

// Add the request to the RequestQueue.


    private static final String TAG = "SevenNLS";
    private static final String TAG_PRE = "["+MainActivity.class.getSimpleName()+"] ";
    private static final int EVENT_SHOW_CREATE_NOS = 0;
    private static final int EVENT_LIST_CURRENT_NOS = 1;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private boolean isEnabledNLS = false;

        BroadcastReceiver broadcastReceiver;
    FastAdapter<NotificationView> fastAdapter;
    ItemAdapter<NotificationView> itemAdapter;

    BottomNavigationView bottomNav;

        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case EVENT_SHOW_CREATE_NOS:
                        showCreateNotification();
                        break;
                    case EVENT_LIST_CURRENT_NOS:
                        listCurrentNotification();
                        break;

                    default:
                        break;
                }
            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Fragment selectedFragment = new NotificationFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel("channelOne", "channelOne", NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }

            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getStringExtra("notificationremoved") != null) {

                    }

                }
            };

            IntentFilter intentFilter = new IntentFilter(NotificationMonitor.ACTION_NLS_CONTROL);
            registerReceiver(broadcastReceiver, intentFilter);

        }


        @Override
        protected void onResume() {
            super.onResume();
            isEnabledNLS = isEnabled();
            logNLS("isEnabledNLS = " + isEnabledNLS);
            if (!isEnabledNLS) {
                showConfirmDialog();
            }
        }


        private boolean isEnabled() {
            String pkgName = getPackageName();
            final String flat = Settings.Secure.getString(getContentResolver(),
                    ENABLED_NOTIFICATION_LISTENERS);
            if (!TextUtils.isEmpty(flat)) {
                final String[] names = flat.split(":");
                for (int i = 0; i < names.length; i++) {
                    final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                    if (cn != null) {
                        if (TextUtils.equals(pkgName, cn.getPackageName())) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private void createNotification(Context context) {
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder ncBuilder = new NotificationCompat.Builder(context);
            ncBuilder.setContentTitle("My Notification");
            ncBuilder.setContentText("Notification Listener Service Example");
            ncBuilder.setTicker("Notification Listener Service Example");
            ncBuilder.setSmallIcon(R.drawable.ic_launcher_background);
            ncBuilder.setAutoCancel(true);
            ncBuilder.setChannelId("channelOne");
            manager.notify((int)System.currentTimeMillis(),ncBuilder.build());
        }

        private void cancelNotification(Context context, boolean isCancelAll) {
            Intent intent = new Intent();
            intent.setAction(NotificationMonitor.ACTION_NLS_CONTROL);
            if (isCancelAll) {
                intent.putExtra("command", "cancel_all");
            }else {
                intent.putExtra("command", "cancel_last");
            }
            context.sendBroadcast(intent);
        }

        private String getCurrentNotificationString() {
            String listNos = "";
            StatusBarNotification[] currentNos = NotificationMonitor.getCurrentNotifications();
            if (currentNos != null) {
                for (int i = 0; i < currentNos.length; i++) {
//                    listNos = i +" " + currentNos[i].getPackageName() + "\n" + listNos;
                    itemAdapter.add(new NotificationView(currentNos[i].getPackageName()));
                    fastAdapter.notifyAdapterDataSetChanged();
                }
            }
            return listNos;
        }

        private void listCurrentNotification() {
            String result = "";
            if (isEnabledNLS) {
                if (NotificationMonitor.getCurrentNotifications() == null) {
                    logNLS("mCurrentNotifications.get(0) is null");
                    return;
                }
                int n = NotificationMonitor.mCurrentNotificationsCounts;
                if (n == 0) {
                    result = getResources().getString(R.string.active_notification_count_zero);
                }else {
                    result = String.format(getResources().getQuantityString(R.plurals.active_notification_count_nonzero, n, n));
                }

                result = result + "\n" + getCurrentNotificationString();

//                mTextView.setText(result);
            }else {
//                mTextView.setTextColor(Color.RED);
//                mTextView.setText("Please Enable Notification Access");
            }
        }

        private void clearLastNotification() {
            if (isEnabledNLS) {
                cancelNotification(this,false);
            }else {
//                mTextView.setTextColor(Color.RED);
//                mTextView.setText("Please Enable Notification Access");
            }
        }

        private void clearAllNotifications() {
            if (isEnabledNLS) {
                cancelNotification(this,true);
            }else {
//                mTextView.setTextColor(Color.RED);
//                mTextView.setText("Please Enable Notification Access");
            }
        }

        private void showCreateNotification() {
            if (NotificationMonitor.mPostedNotification != null) {
                String result = NotificationMonitor.mPostedNotification.getPackageName()+"\n"
                        + NotificationMonitor.mPostedNotification.getTag()+"\n"
                        + NotificationMonitor.mPostedNotification.getId()+"\n"+"\n";
//                        + mTextView.getText();
                result = "Create notification:"+"\n"+result;
//                mTextView.setText(result);
            }
        }

        private void openNotificationAccess() {
            startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }

        private void showConfirmDialog() {
            new AlertDialog.Builder(this)
                    .setMessage("Please enable NotificationMonitor access")
                    .setTitle("Notification Access")
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    openNotificationAccess();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // do nothing
                                }
                            })
                    .create().show();
        }

        private void logNLS(Object object) {
            Log.i(TAG, TAG_PRE+object);
        }

    @Override
    public void itemsFiltered(@Nullable CharSequence constraint, @Nullable List<NotificationView> results) {

    }

    @Override
    public void onReset() {

    }

    @Override
    public boolean itemTouchOnMove(int oldPosition, int newPosition) {
        return false;
    }

    @Override
    public void itemTouchDropped(int oldPosition, int newPosition) {

    }
}

