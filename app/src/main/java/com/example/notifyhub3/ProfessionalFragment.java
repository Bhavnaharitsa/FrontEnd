package com.example.notifyhub3;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

public class ProfessionalFragment extends Fragment implements ItemTouchCallback {

    private static String TAG = "ProfessionalFragment";
    private static final String TAG_PRE = "[" + ProfessionalFragment.class.getSimpleName() + "] ";


    private RecyclerView recyclerView;
    private FastAdapter<NotificationView> fastAdapter;
    private ItemAdapter<NotificationView> itemAdapter;

    BroadcastReceiver broadcastReceiver;
    private Context mContext;

    private boolean isEnabledNLS = false;

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_others, container, false);
        recyclerView = fragmentView.findViewById(R.id.recycler_others);
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(fastAdapter);

                    fastAdapter.withSelectable(true);
            fastAdapter.withOnClickListener(new OnClickListener<NotificationView>() {
                @Override
                public boolean onClick(@Nullable View v, IAdapter<NotificationView> adapter, NotificationView item, int position) {
                    Intent intent = new Intent(NotificationMonitor.ACTION_NLS_CONTROL);
                    intent.putExtra("packagename", item.getMessage());
                    mContext.sendBroadcast(intent);
                    itemAdapter = new ItemAdapter<>();
                    fastAdapter = FastAdapter.with(itemAdapter);
                    recyclerView.setAdapter(fastAdapter);
                    fastAdapter.notifyAdapterDataSetChanged();
//                    getCurrentNotificationString();
                    return false;
                }
            });

        //Handle Broadcasts from NotificationMonitor service
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               if(intent.getStringExtra("notificationremoved") != null){
                   itemAdapter = new ItemAdapter<>();

                   recyclerView.setAdapter(fastAdapter);
                   Toast.makeText(mContext, "Refresh again to see live notifications", Toast.LENGTH_LONG).show();
               }
            }
        };
        IntentFilter intentFilter = new IntentFilter(NotificationMonitor.ACTION_NLS_CONTROL);
        mContext.registerReceiver(broadcastReceiver, intentFilter);

        setHasOptionsMenu(true);
        return fragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContext.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.refresh_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
//                Intent intent = new Intent(NotificationMonitor.ACTION_NLS_CONTROL);
//                intent.putExtra("command", "get_others");
//                mContext.sendBroadcast(intent);
                getCurrentNotificationString();
//                Toast.makeText(mContext, "Refresh clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean itemTouchOnMove(int oldPosition, int newPosition) {
        return false;
    }

    @Override
    public void itemTouchDropped(int oldPosition, int newPosition) {

    }

    private String getCurrentNotificationString() {
        String listNos = "";
        StatusBarNotification[] currentNos = NotificationMonitor.getCurrentNotifications();
        if (currentNos != null) {
            for (int i = 0; i < currentNos.length; i++) {
//                    listNos = i +" " + currentNos[i].getPackageName() + "\n" + listNos;

                if(Constants.PROFESSIONAL_LIST.contains(currentNos[i].getPackageName())) {
                    itemAdapter.add(new NotificationView(currentNos[i].getPackageName()));
                    fastAdapter.notifyAdapterDataSetChanged();
                }
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
            } else {
                result = String.format(getResources().getQuantityString(R.plurals.active_notification_count_nonzero, n, n));
            }

            result = result + "\n" + getCurrentNotificationString();

//                mTextView.setText(result);
        } else {
//                mTextView.setTextColor(Color.RED);
//                mTextView.setText("Please Enable Notification Access");
        }
    }

    private void logNLS(Object object) {
        Log.i(TAG, TAG_PRE + object);
    }

    private boolean isEnabled() {
        String pkgName = mContext.getPackageName();
        final String flat = Settings.Secure.getString(mContext.getContentResolver(),
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
        manager.notify((int) System.currentTimeMillis(), ncBuilder.build());
    }

    private void cancelNotification(Context context, boolean isCancelAll) {
        Intent intent = new Intent();
        intent.setAction(NotificationMonitor.ACTION_NLS_CONTROL);
        if (isCancelAll) {
            intent.putExtra("command", "cancel_all");
        } else {
            intent.putExtra("command", "cancel_last");
        }
        context.sendBroadcast(intent);
    }

    private void showCreateNotification() {
        if (NotificationMonitor.mPostedNotification != null) {
            String result = NotificationMonitor.mPostedNotification.getPackageName() + "\n"
                    + NotificationMonitor.mPostedNotification.getTag() + "\n"
                    + NotificationMonitor.mPostedNotification.getId() + "\n" + "\n";
//                        + mTextView.getText();
            result = "Create notification:" + "\n" + result;
//                mTextView.setText(result);
        }
    }

    private void openNotificationAccess() {
        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(mContext)
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
}
