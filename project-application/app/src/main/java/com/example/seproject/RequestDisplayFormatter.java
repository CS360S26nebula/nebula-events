package com.example.seproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Shared presentation helpers for visitor {@link Request} rows (empty placeholders, date/time,
 * rejection timestamp).
 *
 * <p>Happy path: non-null date/time strings are trimmed and combined as {@code "date | time"};
 * blank fields render as an em dash. For rejected rows, a positive {@code rejectedAtMillis}
 * formats as a locale-specific date-time string; otherwise visit date/time fall back is used.</p>
 *
 * <p>Null arguments are handled without throwing in the public format methods listed below.</p>
 *
 * @author Moiz Imran
 * @version 1.0
 */
public final class RequestDisplayFormatter {

    private static final String EM_DASH = "—";

    private RequestDisplayFormatter() {
    }

    /**
     * Returns an em dash when the value is null or has length zero (same idea as
     * {@link android.text.TextUtils#isEmpty(CharSequence)}).
     *
     * @param value raw string, may be null
     * @return {@code value} or {@code "—"} when empty
     */
    @NonNull
    public static String dashIfEmpty(@Nullable String value) {
        return (value == null || value.isEmpty()) ? EM_DASH : value;
    }

    /**
     * Formats visit date and time for list subtitles.
     *
     * @param request request whose {@code visitDate} and {@code visitTime} may be null
     * @return combined {@code "date | time"}, or a single part, or {@code "—"} when both blank
     */
    @NonNull
    public static String formatVisitDateTime(@NonNull Request request) {
        String date = request.getVisitDate() == null ? "" : request.getVisitDate().trim();
        String time = request.getVisitTime() == null ? "" : request.getVisitTime().trim();
        if (date.isEmpty() && time.isEmpty()) {
            return EM_DASH;
        }
        if (time.isEmpty()) {
            return date;
        }
        if (date.isEmpty()) {
            return time;
        }
        return date + " | " + time;
    }

    /**
     * Formats the "rejected on" line: uses {@link Request#getRejectedAtMillis()} when positive,
     * otherwise {@link #formatVisitDateTime(Request)}.
     *
     * @param request request with optional rejection timestamp and visit date/time
     * @return formatted string for display
     */
    @NonNull
    public static String formatRejectedOn(@NonNull Request request) {
        if (request.getRejectedAtMillis() > 0) {
            return new SimpleDateFormat("dd/MM/yyyy | h:mm a", Locale.ENGLISH)
                    .format(new Date(request.getRejectedAtMillis()));
        }
        return formatVisitDateTime(request);
    }
}
