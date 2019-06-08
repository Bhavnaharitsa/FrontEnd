package com.example.notifyhub3;

public class JsonItemRemoved {
    String interaction_time;
    String interaction_type;
    String notification_id;
    String channel_id;
    String app_package_name;
    int user_hash;
    long arrival_time;
  public JsonItemRemoved(){}

    public JsonItemRemoved(String notificationId, String ChannelId,String AppPackageName,int UserHash, long ArrivalTime,String InteractionTime, String InteractionType) {
        this.notification_id = notificationId;
        this.channel_id = ChannelId;
        this.user_hash = UserHash;
        this.arrival_time = ArrivalTime;
        this.app_package_name = AppPackageName;

        this.interaction_type=InteractionType;
        this.interaction_time=InteractionTime;

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



    public long getArrivalTime() {
        return arrival_time;
    }

    public void setArrivalTime(String ArrivalTime) {
        this.arrival_time = Long.parseLong(ArrivalTime);
    }

    public String getInteractionTime() {
        return interaction_time;
    }

    public void setInteractionTime(String InteractionTime) {
        this.interaction_time = InteractionTime;
    }
    public int getUserHash() {
        return user_hash;
    }

    public void setUserHash(int UserHash) {
        this.user_hash = UserHash;
    }
    public String getInteractionType() {
        return interaction_type;
    }
    public void setInteractionType(String  InteractionType) {
        this.interaction_type = InteractionType;
    }

}
