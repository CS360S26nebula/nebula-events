package com.example.seproject;

import org.junit.Test;
import java.text.ParseException;
import static org.junit.Assert.*;

/**
 * Local unit test suite for VisitorRequestTimerValidator.
 * Uses the isTimeWithinBounds to avoid Firebase fetch
 */
public class RequestValidatorTest {

    @Test
    public void inTheMiddle() throws ParseException {
        boolean result = VisitorRequestTimerValidator.isTimeWithinBounds("12:00pm", "10:00am", "4:00pm");
        assertTrue("12pm should be valid between 10am and 4pm", result);
    }

    @Test
    public void exactOpeningTime() throws ParseException {
        boolean result = VisitorRequestTimerValidator.isTimeWithinBounds("10:00am", "10:00am", "4:00pm");
        assertTrue("Exact opening time should be allowed", result);
    }

    @Test
    public void exactClosingTime() throws ParseException {
        boolean result = VisitorRequestTimerValidator.isTimeWithinBounds("4:00pm", "10:00am", "4:00pm");
        assertTrue("Exact closing time should be allowed", result);
    }

    @Test
    public void timeTooEarly() throws ParseException {
        boolean result = VisitorRequestTimerValidator.isTimeWithinBounds("9:59am", "10:00am", "4:00pm");
        assertFalse("9:59am should be rejected", result);
    }

    @Test
    public void timeTooLate() throws ParseException {
        boolean result = VisitorRequestTimerValidator.isTimeWithinBounds("4:01pm", "10:00am", "4:00pm");
        assertFalse("4:01pm should be rejected", result);
    }

    @Test
    public void fixFormatTest() throws ParseException {
        boolean result = VisitorRequestTimerValidator.isTimeWithinBounds(" 12 : 00 PM ", "10:00am", "4:00pm");
        assertTrue("Sanitizer should handle weird spacing and capitalization", result);
    }

    @Test(expected = ParseException.class)
    public void malformedTime_ThrowsParseException() throws ParseException {
        VisitorRequestTimerValidator.isTimeWithinBounds("Ten o'clock", "10:00am", "4:00pm");
    }
}