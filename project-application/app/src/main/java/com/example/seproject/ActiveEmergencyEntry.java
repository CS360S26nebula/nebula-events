package com.example.seproject;

/**
 * Active emergency entry model used by the emergency list screen.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class ActiveEmergencyEntry {
    private String logId;
    private String guardId;
    private String emergencyType;
    private String reason;
    private long timestamp;
    /**
     * No-arg constructor used by Firestore during deserialization.
     */
    public ActiveEmergencyEntry() {
    }

    /**
     * @return unique log identifier generated for the emergency entry; may be null
     */
    public String getLogId() {
        return logId;
    }

    /**
     * @return guard identifier who created or reported the emergency; may be null
     */
    public String getGuardId() {
        return guardId;
    }

    /**
     * @return emergency type label (e.g., "Fire", "Medical"); may be null
     */
    public String getEmergencyType() {
        return emergencyType;
    }

    /**
     * @return optional human-entered reason for the emergency; may be null
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return epoch milliseconds when the emergency was created; zero when unset
     */
    public long getTimestamp() {
        return timestamp;
    }
}
