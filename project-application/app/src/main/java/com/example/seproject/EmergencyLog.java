package com.example.seproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Lightweight model for emergency entries that bypass regular visitor request flow.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class EmergencyLog {

    private String logId;
    private String guardId;
    private String emergencyType;
    private String reason;
    private long timestamp;

    public EmergencyLog() {
        // Required empty constructor for Firestore deserialization.
    }

    /**
     * Construct an emergency log with guard id and type. Reason defaults to null.
     *
     * @param guardId       identifier of the guard creating the log
     * @param emergencyType type/category of the emergency
     */
    public EmergencyLog(@NonNull String guardId, @NonNull String emergencyType) {
        this(guardId, emergencyType, null);
    }

    public EmergencyLog(@NonNull String guardId, @NonNull String emergencyType, @Nullable String reason) {
        this.logId = "EMR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.guardId = guardId;
        this.emergencyType = emergencyType;
        this.reason = reason;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * @return unique log id generated for this emergency
     */
    public String getLogId() {
        return logId;
    }

    /**
     * @return guard id that created the emergency log
     */
    public String getGuardId() {
        return guardId;
    }

    /**
     * @return emergency type/category
     */
    public String getEmergencyType() {
        return emergencyType;
    }

    /**
     * @return optional human-entered reason; may be empty string
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return epoch milliseconds when the emergency was created
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Callback interface for asynchronous save operations.
     */
    public interface SaveCallback {
        /** Called when the save operation succeeds. */
        void onSuccess();

        /**
         * Called when the save operation fails.
         *
         * @param errorMessage human-readable error description
         */
        void onFailure(@NonNull String errorMessage);
    }

    /**
     * Persist the emergency log document under the "emergencyLogs" collection.
     *
     * @param callback callback invoked on success or failure
     */
    public void saveToDatabase(@NonNull SaveCallback callback) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("logId", logId);
        payload.put("guardId", guardId);
        payload.put("emergencyType", emergencyType);
        payload.put("reason", reason == null ? "" : reason);
        payload.put("timestamp", timestamp);
        payload.put("createdAt", FieldValue.serverTimestamp());
        payload.put("isActive", true);

        FirebaseFirestore.getInstance()
                .collection("emergencyLogs")
                .document(logId)
                .set(payload)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(
                        e.getMessage() == null ? "Failed to log emergency." : e.getMessage()));
    }

    /**
     * Save the emergency as an active emergency in the "activeEmergencies" collection.
     *
     * @param callback callback invoked on success or failure
     */
    public void saveAsActiveEmergency(@NonNull SaveCallback callback) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("logId", logId);
        payload.put("guardId", guardId);
        payload.put("emergencyType", emergencyType);
        payload.put("reason", reason == null ? "" : reason);
        payload.put("timestamp", timestamp);
        payload.put("activatedAt", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance()
                .collection("activeEmergencies")
                .document(logId)
                .set(payload)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(
                        e.getMessage() == null ? "Failed to activate emergency." : e.getMessage()));
    }
}
