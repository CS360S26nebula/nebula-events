package com.example.seproject;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link RequestDisplayFormatter}.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class RequestDisplayFormatterTest {

    private Request request;

    @Before
    public void setUp() {
        request = new Request();
        request.setVisitDate("  01/04/2026  ");
        request.setVisitTime(" 3:00 PM ");
    }

    @Test
    public void dashIfEmpty_nullOrEmpty_returnsEmDash() {
        assertEquals("—", RequestDisplayFormatter.dashIfEmpty(null));
        assertEquals("—", RequestDisplayFormatter.dashIfEmpty(""));
    }

    @Test
    public void dashIfEmpty_nonEmpty_unchanged() {
        assertEquals("ABC", RequestDisplayFormatter.dashIfEmpty("ABC"));
    }

    @Test
    public void formatVisitDateTime_bothTrimmed_combined() {
        String s = RequestDisplayFormatter.formatVisitDateTime(request);
        assertEquals("01/04/2026 | 3:00 PM", s);
    }

    @Test
    public void formatVisitDateTime_bothNull_returnsDash() {
        Request r = new Request();
        assertEquals("—", RequestDisplayFormatter.formatVisitDateTime(r));
    }

    @Test
    public void formatVisitDateTime_onlyDate() {
        Request r = new Request();
        r.setVisitDate("02/04/2026");
        assertEquals("02/04/2026", RequestDisplayFormatter.formatVisitDateTime(r));
    }

    @Test
    public void formatRejectedOn_negativeMillis_fallsBackToVisitDateTime() {
        request.setRejectedAtMillis(-1L);
        String s = RequestDisplayFormatter.formatRejectedOn(request);
        assertNotNull(s);
        assertTrue(s.contains("01/04/2026"));
    }

    @Test
    public void formatRejectedOn_positiveMillis_formatsTimestamp() {
        request.setRejectedAtMillis(1_700_000_000_000L);
        String s = RequestDisplayFormatter.formatRejectedOn(request);
        assertNotNull(s);
        assertTrue(s.length() > 4);
    }

    @Test
    public void formatRejectedOn_zero_fallsBackToVisitDateTime() {
        request.setRejectedAtMillis(0L);
        assertEquals("01/04/2026 | 3:00 PM", RequestDisplayFormatter.formatRejectedOn(request));
    }
}
