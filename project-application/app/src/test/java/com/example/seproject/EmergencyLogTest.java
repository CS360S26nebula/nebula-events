package com.example.seproject;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link EmergencyLog} getters and constructor logic.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class EmergencyLogTest {

    @Test
    public void constructor_setsBasicFields() {
        String guardId = "G-123";
        String type = "Ambulance";
        EmergencyLog log = new EmergencyLog(guardId, type);

        assertNotNull(log.getLogId());
        assertTrue(log.getLogId().startsWith("EMR-"));
        assertEquals(guardId, log.getGuardId());
        assertEquals(type, log.getEmergencyType());
        assertNull(log.getReason());
        assertTrue(log.getTimestamp() > 0);
    }

    @Test
    public void constructor_withReason_setsReason() {
        String guardId = "G-456";
        String type = "Other";
        String reason = "Power outage in Block B";
        EmergencyLog log = new EmergencyLog(guardId, type, reason);

        assertEquals(reason, log.getReason());
    }

    @Test
    public void defaultConstructor_initializesWithEmptyValues() {
        EmergencyLog log = new EmergencyLog();
        assertNull(log.getLogId());
        assertNull(log.getGuardId());
        assertNull(log.getEmergencyType());
        assertNull(log.getReason());
        assertEquals(0L, log.getTimestamp());
    }
}
