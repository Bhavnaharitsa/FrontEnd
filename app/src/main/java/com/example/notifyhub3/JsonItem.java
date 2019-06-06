package com.example.notifyhub3;

public class JsonItem {
    String notificationId;
    String appId;
    String appName;
    String channelId;

    public JsonItem(){
    }

    public JsonItem(String notificationId, String appId, String appName, String channelId, String channelName, String channelType, String arrivalTime, String userName, String mobileNumber, String gender, String age) {
        this.notificationId = notificationId;
        this.appId = appId;
        this.appName = appName;
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelType = channelType;
        this.arrivalTime = arrivalTime;
        this.userName = userName;
        this.mobileNumber = mobileNumber;
        this.gender = gender;
        this.age = age;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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

    String channelName;
    String channelType;
    String arrivalTime;
    String userName;
    String mobileNumber;
    String gender;
    String age;

}
