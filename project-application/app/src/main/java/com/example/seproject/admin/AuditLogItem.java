package com.example.seproject.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Model class representing one audit log entry displayed in the security audit report.
 * <p>
 * This model is used to normalize different kinds of record like entry, exit, ad-hoc request,
 * and emergency logs into one common structure for display and filtering.
 * </p>
 *
 * @author Abdullah Ahmad
 * @version 1.0
 */

public class AuditLogItem {

    public static final int TYPE_ENTRY          = 1;
    public static final int TYPE_EXIT           = 2;
    public static final int TYPE_ADHOC_REQUEST  = 3;
    public static final int TYPE_EMERGENCY      = 4;

    private final int     type;
    private final String  eventId;
    private final String  visitorOrType;
    private final String  actorId;
    private final String  actorName;
    private final long    eventTimeMillis;
    private final String  extraDetail;

    private AuditLogItem(int type, String eventId, String visitorOrType,
                         String actorId, String actorName,
                         long eventTimeMillis, String extraDetail) {
        this.type            = type;
        this.eventId         = eventId;
        this.visitorOrType   = visitorOrType;
        this.actorId         = actorId;
        this.actorName       = actorName;
        this.eventTimeMillis = eventTimeMillis;
        this.extraDetail     = extraDetail;
    }

    public static AuditLogItem entry(@NonNull String passId,
                                     @NonNull String visitorName,
                                     @Nullable String actorId,
                                     @Nullable String actorName,
                                     long checkedInAtMillis,
                                     @Nullable String visitReason) {
        return new AuditLogItem(
                TYPE_ENTRY,
                passId, visitorName,
                actorId, actorName,
                checkedInAtMillis,
                visitReason);
    }

    public static AuditLogItem exit(@NonNull String passId,
                                    @NonNull String visitorName,
                                    @Nullable String actorId,
                                    @Nullable String actorName,
                                    long checkedOutAtMillis,
                                    @Nullable String visitReason) {
        return new AuditLogItem(
                TYPE_EXIT,
                passId, visitorName,
                actorId, actorName,
                checkedOutAtMillis,
                visitReason);
    }

    public static AuditLogItem adhocRequest(@NonNull String requestId,
                                            @NonNull String visitorName,
                                            @Nullable String actorId,
                                            @Nullable String actorName,
                                            long createdAtMillis,
                                            @Nullable String visitReason,
                                            @NonNull String status) {
        String detail = (visitReason != null && !visitReason.trim().isEmpty())
                ? visitReason + "  |  Status: " + status
                : "Status: " + status;
        return new AuditLogItem(
                TYPE_ADHOC_REQUEST,
                requestId, visitorName,
                actorId, actorName,
                createdAtMillis,
                detail);
    }

    public static AuditLogItem emergency(@NonNull String logId,
                                         @NonNull String emergencyType,
                                         @Nullable String guardId,
                                         @Nullable String guardName,
                                         long timestamp,
                                         @Nullable String reason) {
        return new AuditLogItem(
                TYPE_EMERGENCY,
                logId, emergencyType,
                guardId, guardName,
                timestamp,
                reason);
    }

    public int    getType()             { return type; }
    public String getEventId()          { return eventId; }
    public String getVisitorOrType()    { return visitorOrType; }
    public String getActorId()          { return actorId; }
    public String getActorName()        { return actorName; }
    public long   getEventTimeMillis()  { return eventTimeMillis; }
    public String getExtraDetail()      { return extraDetail; }

    @NonNull
    public String getTypeLabel() {
        switch (type) {
            case TYPE_ENTRY:         return "Entry";
            case TYPE_EXIT:          return "Exit";
            case TYPE_ADHOC_REQUEST: return "Ad-hoc";
            case TYPE_EMERGENCY:     return "Emergency";
            default:                 return "Event";
        }
    }
}