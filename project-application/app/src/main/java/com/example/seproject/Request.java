package com.example.seproject;

import androidx.annotation.Nullable;

/**
 * Domain model for a visitor access request stored in Firestore (and shown in guard/admin/faculty
 * lists).
 *
 * <p>Happy path: faculty creates a row with status {@code "Pending"}; guard pre-approves
 * ({@code requestStatus} becomes {@code "Pre-Approved"}, {@code passId} set) or rejects
 * ({@code "Rejected"}, {@code rejectionReason}, {@code rejectedAtMillis}). Timestamps such as
 * {@code createdAtMillis} may be any {@code long} (including zero or negative) unless the UI
 * layer adds constraints.</p>
 *
 * <p>String fields accept null unless a screen explicitly validates them; Firestore mapping may
 * leave fields unset.</p>
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class Request {
    private String requestId;
    private String visitorName;
    private String visitorCnic;
    private String visitorMobileNumber;
    private String visitReason;
    private String visitDate;
    private String visitTime;
    private String invitorName;
    private String invitorType;
    private String requestStatus;
    private long createdAtMillis;
    private String passId;
    private String rejectionReason;
    private long rejectedAtMillis;

    /**
     * No-arg constructor for Firestore and default construction.
     */
    public Request() {
    }

    /**
     * Constructs a request with the given fields; {@code rejectedAtMillis} is initialized to
     * {@code 0}.
     *
     * @param requestId           document or logical id; may be null
     * @param visitorName         visitor name; may be null
     * @param visitorCnic         CNIC; may be null
     * @param visitorMobileNumber phone; may be null
     * @param visitReason         reason; may be null
     * @param visitDate           date string; may be null
     * @param visitTime           time string; may be null
     * @param invitorName         host name; may be null
     * @param invitorType         e.g. Faculty/Guest; may be null
     * @param requestStatus       workflow status; may be null
     * @param createdAtMillis     creation time in ms; any long, including negative
     * @param passId              issued pass id when pre-approved; may be null
     * @param rejectionReason     set when rejected; may be null
     */
    public Request(@Nullable String requestId, @Nullable String visitorName, @Nullable String visitorCnic,
                   @Nullable String visitorMobileNumber,
                   @Nullable String visitReason, @Nullable String visitDate, @Nullable String visitTime,
                   @Nullable String invitorName, @Nullable String invitorType,
                   @Nullable String requestStatus, long createdAtMillis, @Nullable String passId,
                   @Nullable String rejectionReason) {
        this.requestId = requestId;
        this.visitorName = visitorName;
        this.visitorCnic = visitorCnic;
        this.visitorMobileNumber = visitorMobileNumber;
        this.visitReason = visitReason;
        this.visitDate = visitDate;
        this.visitTime = visitTime;
        this.invitorName = invitorName;
        this.invitorType = invitorType;
        this.requestStatus = requestStatus;
        this.createdAtMillis = createdAtMillis;
        this.passId = passId;
        this.rejectionReason = rejectionReason;
        this.rejectedAtMillis = 0L;
    }

    /**
     * @return request id or null
     */
    @Nullable
    public String getRequestId() {
        return requestId;
    }

    /**
     * @param requestId new id; may be null
     */
    public void setRequestId(@Nullable String requestId) {
        this.requestId = requestId;
    }

    /**
     * @return visitor name or null
     */
    @Nullable
    public String getVisitorName() {
        return visitorName;
    }

    /**
     * @param visitorName visitor name; may be null
     */
    public void setVisitorName(@Nullable String visitorName) {
        this.visitorName = visitorName;
    }

    /**
     * @return CNIC or null
     */
    @Nullable
    public String getVisitorCnic() {
        return visitorCnic;
    }

    /**
     * @param visitorCnic CNIC; may be null
     */
    public void setVisitorCnic(@Nullable String visitorCnic) {
        this.visitorCnic = visitorCnic;
    }

    /**
     * @return mobile number or null
     */
    @Nullable
    public String getVisitorMobileNumber() {
        return visitorMobileNumber;
    }

    /**
     * @param visitorMobileNumber phone; may be null
     */
    public void setVisitorMobileNumber(@Nullable String visitorMobileNumber) {
        this.visitorMobileNumber = visitorMobileNumber;
    }

    /**
     * @return visit reason or null
     */
    @Nullable
    public String getVisitReason() {
        return visitReason;
    }

    /**
     * @param visitReason reason; may be null
     */
    public void setVisitReason(@Nullable String visitReason) {
        this.visitReason = visitReason;
    }

    /**
     * @return visit date string or null
     */
    @Nullable
    public String getVisitDate() {
        return visitDate;
    }

    /**
     * @param visitDate date; may be null
     */
    public void setVisitDate(@Nullable String visitDate) {
        this.visitDate = visitDate;
    }

    /**
     * @return visit time string or null
     */
    @Nullable
    public String getVisitTime() {
        return visitTime;
    }

    /**
     * @param visitTime time; may be null
     */
    public void setVisitTime(@Nullable String visitTime) {
        this.visitTime = visitTime;
    }

    /**
     * @return invitor name or null
     */
    @Nullable
    public String getInvitorName() {
        return invitorName;
    }

    /**
     * @param invitorName host name; may be null
     */
    public void setInvitorName(@Nullable String invitorName) {
        this.invitorName = invitorName;
    }

    /**
     * @return invitor type or null
     */
    @Nullable
    public String getInvitorType() {
        return invitorType;
    }

    /**
     * @param invitorType type; may be null
     */
    public void setInvitorType(@Nullable String invitorType) {
        this.invitorType = invitorType;
    }

    /**
     * @return status string or null
     */
    @Nullable
    public String getRequestStatus() {
        return requestStatus;
    }

    /**
     * @param requestStatus status; may be null
     */
    public void setRequestStatus(@Nullable String requestStatus) {
        this.requestStatus = requestStatus;
    }

    /**
     * @return creation time in milliseconds
     */
    public long getCreatedAtMillis() {
        return createdAtMillis;
    }

    /**
     * @return pass id or null
     */
    @Nullable
    public String getPassId() {
        return passId;
    }

    /**
     * @return rejection reason or null
     */
    @Nullable
    public String getRejectionReason() {
        return rejectionReason;
    }

    /**
     * @param passId pass id; may be null
     */
    public void setPassId(@Nullable String passId) {
        this.passId = passId;
    }

    /**
     * @param rejectionReason reason; may be null
     */
    public void setRejectionReason(@Nullable String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    /**
     * @return rejection timestamp in ms; non-positive means "not set" for display formatters
     */
    public long getRejectedAtMillis() {
        return rejectedAtMillis;
    }

    /**
     * @param rejectedAtMillis any long; values {@code <= 0} fall back to visit date/time in UI
     */
    public void setRejectedAtMillis(long rejectedAtMillis) {
        this.rejectedAtMillis = rejectedAtMillis;
    }

    /**
     * @param createdAtMillis creation time; any long
     */
    public void setCreatedAtMillis(long createdAtMillis) {
        this.createdAtMillis = createdAtMillis;
    }
}
