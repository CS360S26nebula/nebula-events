package com.example.seproject;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link FacultyVisitorRequestValidator}.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class FacultyVisitorRequestValidatorTest {

    private String name;
    private String invitor;
    private String phone;
    private String cnic;
    private String reason;
    private String date;
    private String type;
    private String time;

    @Before
    public void setUp() {
        name = "Guest";
        invitor = "Dr. Khan";
        phone = "03001234567";
        cnic = "12345";
        reason = "Visit";
        date = "02/04/2026";
        type = "Faculty";
        time = "9:00 AM";
    }

    @Test
    public void isValid_happyPath_true() {
        assertTrue(FacultyVisitorRequestValidator.isValid(
                name, invitor, phone, cnic, reason, type, date, time));
    }

    @Test
    public void isValid_missingName_false() {
        assertFalse(FacultyVisitorRequestValidator.isValid(
                "", invitor, phone, cnic, reason, type, date, time));
    }

    @Test
    public void isValid_placeholderVisitorType_false() {
        assertFalse(FacultyVisitorRequestValidator.isValid(
                name, invitor, phone, cnic, reason, "Select visitor type", date, time));
    }

    @Test
    public void isValid_placeholderVisitTime_false() {
        assertFalse(FacultyVisitorRequestValidator.isValid(
                name, invitor, phone, cnic, reason, type, date, "Select visit time"));
    }

    @Test
    public void hasRequiredFields_allSet_true() {
        assertTrue(FacultyVisitorRequestValidator.hasRequiredFields(
                name, invitor, phone, cnic, reason, date));
    }

    @Test
    public void hasRequiredFields_nullDate_false() {
        assertFalse(FacultyVisitorRequestValidator.hasRequiredFields(
                name, invitor, phone, cnic, reason, null));
    }

    @Test
    public void isVisitorTypeSelected_placeholder_false() {
        assertFalse(FacultyVisitorRequestValidator.isVisitorTypeSelected("Select visitor type"));
        assertTrue(FacultyVisitorRequestValidator.isVisitorTypeSelected("Guest"));
    }

    @Test
    public void isVisitTimeSelected_placeholder_false() {
        assertFalse(FacultyVisitorRequestValidator.isVisitTimeSelected("Select visit time"));
        assertTrue(FacultyVisitorRequestValidator.isVisitTimeSelected("10:00 AM"));
    }
}
