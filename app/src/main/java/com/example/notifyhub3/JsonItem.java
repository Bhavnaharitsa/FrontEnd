package com.example.notifyhub3;

public class JsonItem {

    String notification_id;
    String channel_id;
    String app_id;
    String app_name;
    String channel_name;
    String channel_type;
    String arrival_time;
    String user_name;
    String mobile_no;
    String gender;
    String age;
    public JsonItem(){
    }

    public JsonItem(String notificationId, String appId, String appName, String channelId, String channelName, String channelType, String arrivalTime, String userName, String mobileNumber, String gender, String age) {
        this.notification_id = notificationId;
        this.app_id = appId;
        this.app_name = appName;
        this.channel_id = channelId;
        this.channel_name = channelName;
        this.channel_type= channelType;
        this.arrival_time = arrivalTime;
        this.user_name= userName;
        this.mobile_no = mobileNumber;
        this.gender = gender;
        this.age = age;
    }

    public String getNotificationId() {
        return notification_id;
    }

    public void setNotificationId(String notificationId) {
        this.notification_id = notificationId;
    }

    public String getAppId() {
        return app_id;
    }

    public void setAppId(String appId) {
        this.app_id = appId;
    }

    public String getAppName() {
        return app_name;
    }

    public void setAppName(String appName) {
        this.app_name = appName;
    }

    public String getChannelId() {
        return channel_id;
    }

    public void setChannelId(String channelId) {
        this.channel_id= channelId;
    }

    public String getChannelName() {
        return channel_name;
    }

    public void setChannelName(String channelName) {
        this.channel_name = channelName;
    }

    public String getChannelType() {
        return channel_type;
    }

    public void setChannelType(String channelType) {
        this.channel_type = channelType;
    }

    public String getArrivalTime() {
        return arrival_time;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrival_time = arrivalTime;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String userName) {
        this.user_name= userName;
    }

    public String getMobileNumber() {
        return mobile_no;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobile_no = mobileNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }


}
