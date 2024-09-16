package com.example.alarmclock.listcomponent;

public class ListItem {

    private int alarmID = -1;
    private String alarmName = null;
    private String time = null;
    private String activityType = null; // 朝活の種類
    private int studyTime = 0; // 勉強時間（分）

    // Getter and Setter for alarmName
    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    // Getter and Setter for time
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // Getter for Hour and Minute from time
    public String getHour() {
        return getTime().substring(0, 2);
    }

    public String getMinitsu() {
        return getTime().substring(3, 5);
    }

    // Getter and Setter for alarmID
    public void setAlarmID(int alarmID) {
        this.alarmID = alarmID;
    }

    public int getAlarmID() {
        return alarmID;
    }

    // Getter and Setter for activityType (朝活の種類)
    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    // Getter and Setter for studyTime (勉強時間)
    public int getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(int studyTime) {
        this.studyTime = studyTime;
    }
}
