package com.example.seproject;

import androidx.annotation.Nullable;

/**
 * Determines whether a Firestore request's {@code requestStatus} should appear on a faculty
 * request list screen for a given tab (target) status.
 *
 * <p>Happy path: faculty opens a tab (e.g. Pending); this type loads all documents for the
 * signed-in {@code requesterUid}, then filters each row with {@link #matches(String, String)} so
 * only rows matching that tab are shown.</p>
 *
 * <p>State transitions are driven by backend updates to {@code requestStatus} (e.g. Pending →
 * Pre-Approved → Approved, or Pending → Rejected). This class does not mutate data; it only
 * classifies a snapshot status string against the UI tab.</p>
 *
 * @author Moiz Imran
 * @version 1.0
 */
public final class FacultyRequestStatusMatcher {

    private static final String STATUS_PRE_APPROVED = "Pre-Approved";
    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_APPROVED = "Approved";
    private static final String STATUS_REJECTED = "Rejected";

    private FacultyRequestStatusMatcher() {
    }

    /**
     * Returns whether {@code requestStatus} should be shown when the list tab is
     * {@code targetStatus}.
     *
     * @param targetStatus  the tab filter (e.g. {@code "Pending"}, {@code "Approved"}); may be
     *                      null or empty, in which case only a non-null exact equality match could
     *                      succeed for the default branch
     * @param requestStatus the status from the request document; null and empty are treated as no
     *                      match
     * @return {@code true} if the request belongs on the tab for the given target status
     */
    public static boolean matches(@Nullable String targetStatus, @Nullable String requestStatus) {
        if (EmptyStrings.isEmpty(requestStatus)) {
            return false;
        }
        if (STATUS_PENDING.equals(targetStatus)) {
            return STATUS_PENDING.equals(requestStatus) || "pending".equalsIgnoreCase(requestStatus);
        }
        if (STATUS_APPROVED.equals(targetStatus)) {
            return STATUS_APPROVED.equals(requestStatus) || STATUS_PRE_APPROVED.equals(requestStatus);
        }
        if (STATUS_PRE_APPROVED.equals(targetStatus)) {
            return STATUS_PRE_APPROVED.equals(requestStatus);
        }
        if (STATUS_REJECTED.equals(targetStatus)) {
            return STATUS_REJECTED.equals(requestStatus) || "rejected".equalsIgnoreCase(requestStatus);
        }
        return targetStatus != null && targetStatus.equals(requestStatus);
    }
}
