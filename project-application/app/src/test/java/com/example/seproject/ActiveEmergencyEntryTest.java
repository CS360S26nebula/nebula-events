package com.example.seproject;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link ActiveEmergencyEntry} serialization support.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class ActiveEmergencyEntryTest {

    @Test
    public void defaultConstructor_allowsNullFields() {
        ActiveEmergencyEntry entry = new ActiveEmergencyEntry();
        assertNull(entry.getLogId());
        assertNull(entry.getGuardId());
        assertNull(entry.getEmergencyType());
        assertNull(entry.getReason());
        assertEquals(0L, entry.getTimestamp());
    }
}
