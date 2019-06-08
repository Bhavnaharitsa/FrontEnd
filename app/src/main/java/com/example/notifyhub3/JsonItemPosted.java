package com.example.notifyhub3;

public class JsonItemPosted {

    String notification_id;
    String channel_id;
    String app_package_name;
    String notification_body;
    int user_hash;
    long arrival_time;
    public JsonItemPosted(){}

    public JsonItemPosted(String notificationId, String notificationBody, String ChannelId, String AppPackageName, int UserHash, long ArrivalTime) {
        this.notification_id = notificationId;
        this.channel_id = ChannelId;
        this.user_hash = UserHash;
        this.notification_body=notificationBody;
        this.arrival_time = ArrivalTime;
        this.app_package_name = AppPackageName;
        this.notification_body = notificationBody;


    }

    public String getNotificationId() {
        return notification_id;
    }

    public void setNotificationId(String notificationId) {
        this.notification_id = notificationId;
    }

    public String getAppPackageName() {
        return app_package_name;
    }

    public void setAppPackageName(String app_package_name) {
        this.app_package_name = app_package_name;
    }

    public String getChannelId() {
        return channel_id;
    }

    public void setChannelId(String ChannelId) {
        this.channel_id = ChannelId;
    }

    public String getNotificationBody() {
        return notification_body;
    }

    public void setNotificationBody(String NotificationBody) {
        this.channel_id = NotificationBody;
    }

    public long getArrivalTime() {
        return arrival_time;
    }

    public void setArrivalTime(String ArrivalTime) {
        this.arrival_time = Long.parseLong(ArrivalTime);
    }

    public int getUserHash() {
        return user_hash;
    }

    public void setUserHash(int UserHash) {
        this.user_hash = UserHash;
    }

}