package com.example.seproject;

import com.example.seproject.admin.DailyGateTimings;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Validates requests ensuring that they are for within the times when gate is open
 *
 * @author Umer Ashraf
 * @version 1.0
 */
public class VisitorRequestTimerValidator {
    public interface ValidationCallback {
        void onResult(boolean isValid, String errorMessage);
    }

    /**
     * visitDateStr : The date string from the UI (e.g., "15/04/2026")
     * visitTimeStr : The time string from the UI (e.g., "2:30 PM")
     * callback :     The listener that will handle the result
     */
    public static void validate(String visitDateStr, String visitTimeStr, ValidationCallback callback) {

        String dayOfWeek;
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date date = dateFormatter.parse(visitDateStr);
            SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            dayOfWeek = dayFormatter.format(date); // Converts "15/04/2026" to "Wednesday"
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
                            String cleanRequested = visitTimeStr.replace(" ", "").toLowerCase(Locale.ENGLISH);
                            String cleanOpen = timing.getOpeningTime().replace(" ", "").toLowerCase(Locale.ENGLISH);
                            String cleanClose = timing.getClosingTime().replace(" ", "").toLowerCase(Locale.ENGLISH);

                            SimpleDateFormat timeParser = new SimpleDateFormat("h:mma", Locale.ENGLISH);
                            Date requestedTime = timeParser.parse(cleanRequested);
                            Date openingTime = timeParser.parse(cleanOpen);
                            Date closingTime = timeParser.parse(cleanClose);

                            if (requestedTime.before(openingTime) || requestedTime.after(closingTime)) {
                                callback.onResult(false, "Gate is only open from " + timing.getOpeningTime() + " to " + timing.getClosingTime() + " on " + dayOfWeek + "s.");
                            } else {
                                callback.onResult(true, null);
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
}
