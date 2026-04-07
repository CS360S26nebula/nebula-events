package com.example.seproject.admin;

import com.google.firebase.firestore.PropertyName;

/**
 * Represents the gate access information (timing+Open/Close Status) for a specific day.
 * Also acts as a data model for storing/retrieving schedule from the database.
 *
 * @author Umer Ashraf
 * @version 2.0
 */
public class DailyGateTimings {

    /** The name of the day. */
    private String dayOfWeek;

    /** Indicates is visitor entry is allowed or not. */
    private boolean isGateOpen;

    /** The time at which gate opens for visitors. */
    private String openingTime;

    /** The time at which gate closes for visitors. */
    private String closingTime;

    /**
     * Default constructor (required for Firebase)
     */
    public DailyGateTimings() {}

    /**
     * Parameterized Constructor to make a new DailyGateTimings instance with specified parameters.
     *
     * @param dayOfWeek   The day of the week
     * @param isGateOpen  True if the gate is open on this day, false otherwise.
     * @param openingTime The formatted opening time string.
     * @param closingTime The formatted closing time string.
     */
    public DailyGateTimings(String dayOfWeek, boolean isGateOpen, String openingTime, String closingTime) {
        this.dayOfWeek = dayOfWeek;
        this.isGateOpen = isGateOpen;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    /**
     * Retrieves the day of the week.
     * The {@code @PropertyName("dayOfWeek")} annotation ensures mapping
     * matches the Firestore field precisely.
     *
     * @return The day of the week.
     */
    @PropertyName("dayOfWeek")
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * Checks if the gate is open on this day.
     *
     * @return True if the gate is open, false otherwise.
     */
    @PropertyName("gateOpen")
    public boolean isGateOpen() {
        return isGateOpen;
    }

    /**
     * Retrieves the opening time of the gate.
     *
     * @return The formatted opening time string.
     */
    @PropertyName("openingTime")
    public String getOpeningTime() {
        return openingTime;
    }

    /**
     * Retrieves the closing time of the gate.
     *
     * @return The formatted closing time string.
     */
    @PropertyName("closingTime")
    public String getClosingTime() {
        return closingTime;
    }

    /**
     * Sets the day of the week.
     *
     * @param dayOfWeek The string representation of the day to set.
     */
    @PropertyName("dayOfWeek")
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Sets whether the gate is open on this day.
     *
     * @param gateOpen True to set the gate as open, false for closed.
     */
    @PropertyName("gateOpen")
    public void setGateOpen(boolean gateOpen) {
        isGateOpen = gateOpen;
    }

    /**
     * Sets the opening time for the gate.
     *
     * @param openingTime The opening time to set.
     */
    @PropertyName("openingTime")
    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    /**
     * Sets the closing time for the gate.
     *
     * @param closingTime The closing time to set.
     */
    @PropertyName("closingTime")
    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }
}