package com.example.seproject.admin;

import com.google.firebase.firestore.PropertyName;


/**
 * Sets up Gate Timings for days to set times during which gate access is allowed
 *
 * @author Umer Ashraf
 * @version 2.0
 */
public class DailyGateTimings {
    private String dayOfWeek;
    private boolean isGateOpen;
    private String openingTime;
    private String closingTime;

    public DailyGateTimings() {}

    public DailyGateTimings(String dayOfWeek, boolean isGateOpen, String openingTime, String closingTime) {
        this.dayOfWeek = dayOfWeek;
        this.isGateOpen = isGateOpen;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    @PropertyName("dayOfWeek")
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    @PropertyName("gateOpen")
    public boolean isGateOpen() {
        return isGateOpen;
    }
    @PropertyName("openingTime")
    public String getOpeningTime() {
        return openingTime;
    }
    @PropertyName("closingTime")
    public String getClosingTime() {
        return closingTime;
    }
    @PropertyName("dayOfWeek")
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    @PropertyName("gateOpen")
    public void setGateOpen(boolean gateOpen) {
        isGateOpen = gateOpen;
    }
    @PropertyName("openingTime")
    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }
    @PropertyName("closingTime")
    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }
}
