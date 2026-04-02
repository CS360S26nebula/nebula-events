package com.example.seproject;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests faculty tab filtering against stored {@code requestStatus} values.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class FacultyRequestStatusMatcherTest {

    @Before
    public void setUp() {
        // Intentionally empty: matcher is stateless; hook reserved for future fixtures.
    }

    @Test
    public void pendingTab_acceptsPendingCaseInsensitive() {
        assertTrue(FacultyRequestStatusMatcher.matches("Pending", "Pending"));
        assertTrue(FacultyRequestStatusMatcher.matches("Pending", "pending"));
    }

    @Test
    public void pendingTab_rejectsApproved() {
        assertFalse(FacultyRequestStatusMatcher.matches("Pending", "Approved"));
    }

    @Test
    public void approvedTab_acceptsApprovedAndPreApproved() {
        assertTrue(FacultyRequestStatusMatcher.matches("Approved", "Approved"));
        assertTrue(FacultyRequestStatusMatcher.matches("Approved", "Pre-Approved"));
    }

    @Test
    public void preApprovedTab_onlyPreApproved() {
        assertTrue(FacultyRequestStatusMatcher.matches("Pre-Approved", "Pre-Approved"));
        assertFalse(FacultyRequestStatusMatcher.matches("Pre-Approved", "Approved"));
    }

    @Test
    public void rejectedTab_acceptsRejectedCaseInsensitive() {
        assertTrue(FacultyRequestStatusMatcher.matches("Rejected", "Rejected"));
        assertTrue(FacultyRequestStatusMatcher.matches("Rejected", "rejected"));
    }

    @Test
    public void nullOrEmptyRequestStatus_neverMatches() {
        assertFalse(FacultyRequestStatusMatcher.matches("Pending", null));
        assertFalse(FacultyRequestStatusMatcher.matches("Pending", ""));
    }

    @Test
    public void unknownTarget_exactMatchOnly() {
        assertTrue(FacultyRequestStatusMatcher.matches("Custom", "Custom"));
        assertFalse(FacultyRequestStatusMatcher.matches("Custom", "Other"));
    }

    @Test
    public void nullTarget_nonNullStatus_falseUnlessEqualBranch() {
        assertFalse(FacultyRequestStatusMatcher.matches(null, "Pending"));
    }
}
