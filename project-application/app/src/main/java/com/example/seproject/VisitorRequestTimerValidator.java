package com.example.seproject;

import com.example.seproject.admin.DailyGateTimings;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Responsible for validating visitor entry requests against gate timings from database
 * Utilizes a callback interface to return the validation results.
 *
 * @author Umer Ashraf
 * @version 1.2
 */
public class VisitorRequestTimerValidator {

    /**
     * Callback interface.
     */
    public interface ValidationCallback {
        /**
         * @param isValid      True if requested time is within the allowed timings; false otherwise.
         * @param errorMessage An error message to display if validation fails
         */
        void onResult(boolean isValid, String errorMessage);
    }

    /**
     * Validates whether a requested date and time fall within the
     * allowed operating hours for the gate.
     *
     * @param visitDateStr Requested date string as "dd/MM/yyyy"
     * @param visitTimeStr Requested time string in AM/PM
     * @param callback     The listener to handle success or failure result.
     */
    public static void validate(String visitDateStr, String visitTimeStr, ValidationCallback callback) {

        String dayOfWeek;
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date date = dateFormatter.parse(visitDateStr);
            SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            dayOfWeek = dayFormatter.format(date);
        } catch (ParseException e) {
            callback.onResult(false, "Invalid date format.");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("GateTimings")
                .document(dayOfWeek)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        DailyGateTimings timing = documentSnapshot.toObject(DailyGateTimings.class);
                        if (timing == null) {
                            callback.onResult(false, "Database mapping error.");
                            return;
                        }

                        if (!timing.isGateOpen()) {
                            callback.onResult(false, "The gate is closed to visitors on " + dayOfWeek + "s.");
                            return;
                        }

                        try {
                            if (isTimeWithinBounds(visitTimeStr, timing.getOpeningTime(), timing.getClosingTime())) {
                                callback.onResult(true, null);
                            } else {
                                callback.onResult(false, "Gate is only open from " + timing.getOpeningTime() + " to " + timing.getClosingTime() + " on " + dayOfWeek + "s.");
                            }
                        } catch (ParseException e) {
                            callback.onResult(false, "Error calculating time slots.");
                        }

                    } else {
                        callback.onResult(false, "No gate timings configured for " + dayOfWeek + ".");
                    }
                })
                .addOnFailureListener(e -> callback.onResult(false, "Failed to connect to the database."));
    }

    /**
     * Checks if a requested time string falls between an open and close time.
     * Isolated so it can be rigorously unit tested without needing Firebase connection.
     *
     * @param requestedTimeStr The time the visitor wants to enter
     * @param openTimeStr      Gate opening time
     * @param closeTimeStr     Gate closing time
     * @return True if the time within bounds, false otherwise.
     * @throws ParseException If any of the time strings are not in the expected format.
     */
    public static boolean isTimeWithinBounds(String requestedTimeStr, String openTimeStr, String closeTimeStr) throws ParseException {
        String cleanReq = requestedTimeStr.replace(" ", "").toLowerCase(Locale.ENGLISH);
        String cleanOpen = openTimeStr.replace(" ", "").toLowerCase(Locale.ENGLISH);
        String cleanClose = closeTimeStr.replace(" ", "").toLowerCase(Locale.ENGLISH);

        SimpleDateFormat timeParser = new SimpleDateFormat("h:mma", Locale.ENGLISH);
        Date requestedTime = timeParser.parse(cleanReq);
        Date openingTime = timeParser.parse(cleanOpen);
        Date closingTime = timeParser.parse(cleanClose);

        return !requestedTime.before(openingTime) && !requestedTime.after(closingTime);
    }
}