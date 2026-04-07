package com.example.seproject;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Local JVM tests for {@link CreateVisitorEntryRules}.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class CreateVisitorEntryRulesTest {

    private String validEmail;

    @Before
    public void setUp() {
        validEmail = "faculty.user@university.edu";
    }

    @Test
    public void isInvitorEmailFormatValid_happyPath_true() {
        assertTrue(CreateVisitorEntryRules.isInvitorEmailFormatValid(validEmail));
    }

    @Test
    public void isInvitorEmailFormatValid_null_false() {
        assertFalse(CreateVisitorEntryRules.isInvitorEmailFormatValid(null));
    }

    @Test
    public void isInvitorEmailFormatValid_missingAt_false() {
        assertFalse(CreateVisitorEntryRules.isInvitorEmailFormatValid("faculty.university.edu"));
    }

    @Test
    public void isInvitorEmailFormatValid_missingDomain_false() {
        assertFalse(CreateVisitorEntryRules.isInvitorEmailFormatValid("faculty@"));
    }

    @Test
    public void statusForStaffCreatedEntry_returnsApproved() {
        assertEquals("Approved", CreateVisitorEntryRules.statusForStaffCreatedEntry());
    }

    @Test
    public void buildPassId_hasPrefixAndLength() {
        String passId = CreateVisitorEntryRules.buildPassId();
        assertTrue(passId.startsWith("PASS-"));
        assertEquals(13, passId.length());
    }

    @Test
    public void buildPassId_suffixUppercaseAlnum() {
        String suffix = CreateVisitorEntryRules.buildPassId().substring(5);
        assertTrue(suffix.matches("^[A-Z0-9]{8}$"));
    }
}
