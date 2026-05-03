package com.example.seproject;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for blacklist date and CNIC matching behavior.
 *
 * @author Yahya Ali
 * @version 1.0
 */

public class BlacklistRulesTest {

    @Test
    public void normalizeCnic_removesDashesAndSpaces() {
        String result = normalizeCnic("12345-1234567-1");

        assertEquals("1234512345671", result);
    }

    @Test
    public void normalizeCnic_keepsOnlyDigits() {
        String result = normalizeCnic("abc 12345-1234567-1 xyz");

        assertEquals("1234512345671", result);
    }

    @Test
    public void blacklistRecordActive_listShouldShowEvenBeforeStartDate() {
        boolean isActive = true;

        assertTrue(shouldShowInBlacklistList(isActive));
    }

    @Test
    public void blacklistRecordInactive_listShouldNotShow() {
        boolean isActive = false;

        assertFalse(shouldShowInBlacklistList(isActive));
    }

    @Test
    public void visitInsideBlacklistPeriod_shouldBeBlocked() {
        long startTime = parseDateTime("16/05/2026", "9:00 AM");
        long endTime = parseDateTime("17/05/2026", "5:00 PM");
        long visitTime = parseDateTime("16/05/2026", "10:00 AM");

        boolean result = shouldBlockEntry(true, startTime, endTime, visitTime);

        assertTrue(result);
    }

    @Test
    public void visitBeforeBlacklistPeriod_shouldNotBeBlocked() {
        long startTime = parseDateTime("16/05/2026", "9:00 AM");
        long endTime = parseDateTime("17/05/2026", "5:00 PM");
        long visitTime = parseDateTime("15/05/2026", "10:00 AM");

        boolean result = shouldBlockEntry(true, startTime, endTime, visitTime);

        assertFalse(result);
    }

    @Test
    public void visitAfterBlacklistPeriod_shouldNotBeBlocked() {
        long startTime = parseDateTime("16/05/2026", "9:00 AM");
        long endTime = parseDateTime("17/05/2026", "5:00 PM");
        long visitTime = parseDateTime("19/05/2026", "10:00 AM");

        boolean result = shouldBlockEntry(true, startTime, endTime, visitTime);

        assertFalse(result);
    }

    @Test
    public void visitExactlyAtBlacklistStart_shouldBeBlocked() {
        long startTime = parseDateTime("16/05/2026", "9:00 AM");
        long endTime = parseDateTime("17/05/2026", "5:00 PM");
        long visitTime = parseDateTime("16/05/2026", "9:00 AM");

        boolean result = shouldBlockEntry(true, startTime, endTime, visitTime);

        assertTrue(result);
    }

    @Test
    public void visitExactlyAtBlacklistEnd_shouldBeBlocked() {
        long startTime = parseDateTime("16/05/2026", "9:00 AM");
        long endTime = parseDateTime("17/05/2026", "5:00 PM");
        long visitTime = parseDateTime("17/05/2026", "5:00 PM");

        boolean result = shouldBlockEntry(true, startTime, endTime, visitTime);

        assertTrue(result);
    }

    @Test
    public void inactiveBlacklistRecord_shouldNotBlockEntry() {
        long startTime = parseDateTime("16/05/2026", "9:00 AM");
        long endTime = parseDateTime("17/05/2026", "5:00 PM");
        long visitTime = parseDateTime("16/05/2026", "10:00 AM");

        boolean result = shouldBlockEntry(false, startTime, endTime, visitTime);

        assertFalse(result);
    }

    @Test
    public void nullActiveValue_shouldNotBlockEntry() {
        long startTime = parseDateTime("16/05/2026", "9:00 AM");
        long endTime = parseDateTime("17/05/2026", "5:00 PM");
        long visitTime = parseDateTime("16/05/2026", "10:00 AM");

        boolean result = shouldBlockEntry(null, startTime, endTime, visitTime);

        assertFalse(result);
    }

    @Test
    public void invalidVisitTime_shouldNotBlockEntry() {
        long startTime = parseDateTime("16/05/2026", "9:00 AM");
        long endTime = parseDateTime("17/05/2026", "5:00 PM");
        long visitTime = -1L;

        boolean result = shouldBlockEntry(true, startTime, endTime, visitTime);

        assertFalse(result);
    }

    private String normalizeCnic(String cnic) {
        if (cnic == null) {
            return "";
        }

        return cnic.replaceAll("[^0-9]", "");
    }

    private boolean shouldShowInBlacklistList(boolean isActive) {
        return isActive;
    }

    private boolean shouldBlockEntry(Boolean isActive, Long startTime, Long endTime, long visitTime) {
        if (isActive == null || !isActive) {
            return false;
        }

        if (startTime == null || endTime == null) {
            return false;
        }

        if (visitTime < 0) {
            return false;
        }

        return visitTime >= startTime && visitTime <= endTime;
    }

    private long parseDateTime(String visitDate, String visitTime) {
        try {
            String source = visitDate + " " + visitTime;
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.ENGLISH);
            Date date = formatter.parse(source);

            if (date == null) {
                return -1L;
            }

            return date.getTime();
        } catch (Exception ignored) {
            return -1L;
        }
    }
}