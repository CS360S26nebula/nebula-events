package com.example.seproject;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for {@link User}.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class UserTest {

    private User user;

    @Before
    public void setUp() {
        user = new User(
                "Ada Lovelace",
                "Faculty",
                "10/12/1815",
                "ada@example.com",
                "1234567890",
                "11111");
    }

    @Test
    public void constructor_populatesAllFields() {
        assertEquals("Ada Lovelace", user.getFullName());
        assertEquals("Faculty", user.getRole());
        assertEquals("10/12/1815", user.getDateOfBirth());
        assertEquals("ada@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("11111", user.getCnicNumber());
    }

    @Test
    public void setters_mutateProfile() {
        user.setFullName("Bob");
        user.setRole("Admin");
        assertEquals("Bob", user.getFullName());
        assertEquals("Admin", user.getRole());
    }

    @Test
    public void noArgConstructor_fieldsNull() {
        User u = new User();
        assertNull(u.getFullName());
        assertNull(u.getEmail());
    }

    @Test
    public void nullConstructorArgs_stored() {
        User u = new User(null, null, null, null, null, null);
        assertNull(u.getFullName());
        assertNotNull(u);
    }
}
