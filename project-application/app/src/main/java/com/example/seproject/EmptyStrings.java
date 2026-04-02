package com.example.seproject;

import androidx.annotation.Nullable;

/**
 * Null/empty checks for {@link String} without calling {@link android.text.TextUtils}, so logic
 * can run in local JVM unit tests (where Android framework methods are not mocked).
 *
 * <p>For {@link String}, this matches {@link android.text.TextUtils#isEmpty(CharSequence)}:
 * null or length zero.</p>
 *
 * @author Moiz Imran
 * @version 1.0
 */
final class EmptyStrings {

    private EmptyStrings() {
    }

    /**
     * @param s any string, may be null
     * @return {@code true} if null or {@link String#isEmpty()}
     */
    static boolean isEmpty(@Nullable String s) {
        return s == null || s.isEmpty();
    }
}
