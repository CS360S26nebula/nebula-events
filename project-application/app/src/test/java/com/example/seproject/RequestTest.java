package com.example.seproject;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for {@link Request} getters, setters, and constructor defaults.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class RequestTest {

    private Request request;

    /**
     * Builds a fresh {@link Request} before each test for isolated state.
     */
    @Before
    public void setUp() {
        request = new Request(
                "rid-1",
                "Visitor",
                "42101",
                "0300",
                "Meeting",
                "01/04/2026",
                "10:00 AM",
                "Host",
                "Faculty",
                "Pending",
                1000L,
                "",
                null);
    }

    @Test
    public void constructor_setsFieldsAndRejectedAtZero() {
        assertEquals("rid-1", request.getRequestId());
        assertEquals("Visitor", request.getVisitorName());
        assertEquals("Pending", request.getRequestStatus());
        assertEquals(1000L, request.getCreatedAtMillis());
        assertEquals(0L, request.getRejectedAtMillis());
        assertNotNull(request.getPassId());
    }

    @Test
    public void setters_updateState() {
        request.setRequestStatus("Rejected");
        request.setRejectionReason("No show");
        request.setRejectedAtMillis(500L);
        request.setPassId("PASS-ABC");

        assertEquals("Rejected", request.getRequestStatus());
        assertEquals("No show", request.getRejectionReason());
        assertEquals(500L, request.getRejectedAtMillis());
        assertEquals("PASS-ABC", request.getPassId());
    }

    @Test
    public void defaultConstructor_allowsNullFields() {
        Request empty = new Request();
        assertNull(empty.getRequestId());
        assertEquals(0L, empty.getCreatedAtMillis());
        assertEquals(0L, empty.getRejectedAtMillis());
    }

    @Test
    public void negativeTimestamps_stored() {
        request.setCreatedAtMillis(-1L);
        request.setRejectedAtMillis(-5L);
        assertEquals(-1L, request.getCreatedAtMillis());
        assertEquals(-5L, request.getRejectedAtMillis());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyList_getFirst_throws() {
        new ArrayList<Request>().get(0);
    }

    @Test(expected = NumberFormatException.class)
    public void parseInt_invalid_throwsNumberFormatException() {
        Integer.parseInt("x");
    }
}
