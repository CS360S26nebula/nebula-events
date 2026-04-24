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

    public interface SaveCallback {
        void onSuccess();
        void onFailure(@NonNull String errorMessage);
    }

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
