package com.example.seproject;

import androidx.annotation.Nullable;

/**
 * Application user profile fields loaded after sign-in (e.g. from Firestore).
 *
 * <p>Happy path: all strings populated from the backend for the signed-in account.</p>
 *
 * <p>All fields may be null; callers should validate before displaying or persisting.</p>
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class User {

    private String fullName;
    private String role;
    private String dateOfBirth;
    private String email;
    private String phoneNumber;
    private String cnicNumber;

    /**
     * No-arg constructor for serializers and defaults.
     */
    public User() {
    }

    /**
     * @param fullName    display name; may be null
     * @param role        e.g. Admin, Faculty, Guard; may be null
     * @param dobString   date of birth string; may be null
     * @param email       email; may be null
     * @param phoneNumber phone; may be null
     * @param cnicNumber  CNIC; may be null
     */
    public User(@Nullable String fullName, @Nullable String role, @Nullable String dobString,
                @Nullable String email,
                @Nullable String phoneNumber, @Nullable String cnicNumber) {
        this.fullName = fullName;
        this.role = role;
        this.dateOfBirth = dobString;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.cnicNumber = cnicNumber;
    }

    /**
     * @return full name or null
     */
    @Nullable
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName name; may be null
     */
    public void setFullName(@Nullable String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return role or null
     */
    @Nullable
    public String getRole() {
        return role;
    }

    /**
     * @param role role; may be null
     */
    public void setRole(@Nullable String role) {
        this.role = role;
    }

    /**
     * @return date of birth or null
     */
    @Nullable
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * @param dateOfBirth DOB; may be null
     */
    public void setDateOfBirth(@Nullable String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return email or null
     */
    @Nullable
    public String getEmail() {
        return email;
    }

    /**
     * @param email email; may be null
     */
    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    /**
     * @return phone or null
     */
    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber phone; may be null
     */
    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return CNIC or null
     */
    @Nullable
    public String getCnicNumber() {
        return cnicNumber;
    }

    /**
     * @param cnicNumber CNIC; may be null
     */
    public void setCnicNumber(@Nullable String cnicNumber) {
        this.cnicNumber = cnicNumber;
    }
}
