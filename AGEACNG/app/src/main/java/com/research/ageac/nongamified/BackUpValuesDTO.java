package com.research.ageac.nongamified;

public class BackUpValuesDTO {

    private String username;
    private int daysStreak;
    private String lastActiveDate;
    private String lastDBUploadDateTime;

    public String getLastDBUploadDateTime() {
        return lastDBUploadDateTime;
    }

    public void setLastDBUploadDateTime(String lastDBUploadDateTime) {
        this.lastDBUploadDateTime = lastDBUploadDateTime;
    }

    public int getDaysStreak() {
        return daysStreak;
    }

    public void setDaysStreak(int daysStreak) {
        this.daysStreak = daysStreak;
    }

    public String getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(String lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
