package com.example.seproject;

/**
 * Active emergency entry model used by the emergency list screen.
 */
public class ActiveEmergencyEntry {
    private String logId;
    private String guardId;
    private String emergencyType;
    private String reason;
    private long timestamp;

    public ActiveEmergencyEntry() {
    }

    public String getLogId() {
        return logId;
    }

    public String getGuardId() {
        return guardId;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public String getReason() {
        return reason;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
