package com.example.seproject;

import androidx.annotation.Nullable;

/**
 * Validates trimmed field values for a faculty-submitted visitor request before persistence.
 *
 * <p>Happy path: all required strings are non-empty, visitor type is not the placeholder
 * {@code "Select visitor type"}, and visit time is not {@code "Select visit time"}; then
 * {@link #isValid(String, String, String, String, String, String, String, String)} returns
 * {@code true}.</p>
 *
 * <p>Invalid inputs (null, empty, or placeholder selections) return {@code false}. This class
 * does not throw for normal validation failures; callers show UI feedback.</p>
 *
 * @author Moiz Imran
 * @version 1.0
 */
public final class FacultyVisitorRequestValidator {

    private static final String PLACEHOLDER_VISITOR_TYPE = "Select visitor type";
    private static final String PLACEHOLDER_VISIT_TIME = "Select visit time";

    private FacultyVisitorRequestValidator() {
    }

    /**
     * True when all mandatory text fields are non-empty (visitor type and time not checked).
     *
     * @param visitorName        visitor display name; may be null
     * @param invitorName        hosting party name; may be null
     * @param visitorPhoneNumber phone; may be null
     * @param visitorCnic        CNIC; may be null
     * @param visitReason        reason; may be null
     * @param visitDate          date; may be null
     * @return {@code false} if any argument is null or empty
     */
    public static boolean hasRequiredFields(@Nullable String visitorName,
                                            @Nullable String invitorName,
                                            @Nullable String visitorPhoneNumber,
                                            @Nullable String visitorCnic,
                                            @Nullable String visitReason,
                                            @Nullable String visitDate) {
        return !EmptyStrings.isEmpty(visitorName)
                && !EmptyStrings.isEmpty(invitorName)
                && !EmptyStrings.isEmpty(visitorPhoneNumber)
                && !EmptyStrings.isEmpty(visitorCnic)
                && !EmptyStrings.isEmpty(visitReason)
                && !EmptyStrings.isEmpty(visitDate);
    }

    /**
     * @param visitorType spinner selection; may be null
     * @return {@code false} if null, empty, or the placeholder {@code "Select visitor type"}
     */
    public static boolean isVisitorTypeSelected(@Nullable String visitorType) {
        return !EmptyStrings.isEmpty(visitorType) && !PLACEHOLDER_VISITOR_TYPE.equals(visitorType);
    }

    /**
     * @param visitTime spinner selection; may be null
     * @return {@code false} if null, empty, or the placeholder {@code "Select visit time"}
     */
    public static boolean isVisitTimeSelected(@Nullable String visitTime) {
        return !EmptyStrings.isEmpty(visitTime) && !PLACEHOLDER_VISIT_TIME.equals(visitTime);
    }

    /**
     * Validates faculty visitor request fields after trimming.
     *
     * @param visitorName         visitor display name; null/empty invalid
     * @param invitorName         hosting party name; null/empty invalid
     * @param visitorPhoneNumber  phone; null/empty invalid
     * @param visitorCnic         CNIC; null/empty invalid
     * @param visitReason         reason text; null/empty invalid
     * @param visitorType         spinner value; invalid if null/empty or placeholder
     * @param visitDate           date string; null/empty invalid
     * @param visitTime           spinner value; invalid if null/empty or placeholder
     * @return {@code true} if all checks pass; {@code false} otherwise
     */
    public static boolean isValid(@Nullable String visitorName,
                                  @Nullable String invitorName,
                                  @Nullable String visitorPhoneNumber,
                                  @Nullable String visitorCnic,
                                  @Nullable String visitReason,
                                  @Nullable String visitorType,
                                  @Nullable String visitDate,
                                  @Nullable String visitTime) {
        return hasRequiredFields(visitorName, invitorName, visitorPhoneNumber, visitorCnic,
                visitReason, visitDate)
                && isVisitorTypeSelected(visitorType)
                && isVisitTimeSelected(visitTime);
    }
}
