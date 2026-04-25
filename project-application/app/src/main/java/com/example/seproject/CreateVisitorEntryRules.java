package com.example.seproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Pure rules used by {@link CreateVisitorEntry} so local JVM tests can verify behavior without
 * Android/Firebase runtime dependencies.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public final class CreateVisitorEntryRules {

    private static final Pattern SIMPLE_EMAIL =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private CreateVisitorEntryRules() {
    }

    /**
     * @param invitorEmail email entered in form; may be null
     * @return true when non-empty and matches a basic email pattern
     */
    public static boolean isInvitorEmailFormatValid(@Nullable String invitorEmail) {
        return !EmptyStrings.isEmpty(invitorEmail) && SIMPLE_EMAIL.matcher(invitorEmail).matches();
    }

    /**
     * @return fixed status used when admin/guard creates an entry
     */
    @NonNull
    public static String statusForStaffCreatedEntry() {
        return "Approved";
    }

    /**
     * @return fixed status used when admin/guard creates an entry
     */
    @NonNull
    public static String statusForAdhocEntry() {
        return "Pending";
    }
    /**
     * @return pass id with {@code PASS-XXXXXXXX} format (8 uppercase chars)
     */
    @NonNull
    public static String buildPassId() {
        return "PASS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
