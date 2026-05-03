package com.example.seproject.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.seproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/**
 * Admin blacklist user form and Firestore update flow.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class BlackListUserAdminFragment extends Fragment {
    private static final String REQUEST_KEY_BLACKLIST_CONFIRM = "request_key_blacklist_confirm";
    private static final String BUNDLE_KEY_CONFIRMED = "bundle_key_confirmed";

    private EditText etName;
    private EditText etCnic;
    private EditText etReason;
    private Spinner spRole;
    private Spinner spStartDate;
    private Spinner spStartMonth;
    private Spinner spStartYear;
    private Spinner spStartTime;
    private Spinner spStartAmPm;
    private Spinner spEndDate;
    private Spinner spEndMonth;
    private Spinner spEndYear;
    private Spinner spEndTime;
    private Spinner spEndAmPm;
    private MaterialButton btnBlacklist;
    private MaterialButton btnRemoveBlacklist;
    private FirebaseFirestore firestore;

    public BlackListUserAdminFragment() {
        /**
         * Creates the blacklist user admin fragment.
         */

        super(R.layout.blacl_list_user_admin_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        /**
         * Sets up the blacklist form, spinner values, buttons, and confirmation dialog result.
         *
         * @param view               root view of the fragment
         * @param savedInstanceState previous state if the fragment is being recreated
         */

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
        firestore = FirebaseFirestore.getInstance();
        initViews(view);
        btnBlacklist.setOnClickListener(v -> openConfirmationPopup());
        btnRemoveBlacklist.setOnClickListener(v -> removeFromBlacklist());
        getChildFragmentManager().setFragmentResultListener(
                REQUEST_KEY_BLACKLIST_CONFIRM,
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    if (bundle.getBoolean(BUNDLE_KEY_CONFIRMED, false)) {
                        submitBlacklist();
                    }
                });

        bindSpinner(spRole, new String[]{"Select person type", "Guest", "Contractor"});
        String[] date = buildRange("Date", 1, 31);
        String[] month = new String[]{"Month", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] year = buildRange("Year", 2024, currentYear);
        bindSpinner(spStartDate, date);
        bindSpinner(spStartMonth, month);
        bindSpinner(spStartYear, year);
        bindSpinner(spEndDate, date);
        bindSpinner(spEndMonth, month);
        bindSpinner(spEndYear, year);
        String[] times = new String[]{"Time", "12:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00"};
        String[] meridiem = new String[]{"AM/PM", "AM", "PM"};
        bindSpinner(spStartTime, times);
        bindSpinner(spStartAmPm, meridiem);
        bindSpinner(spEndTime, times);
        bindSpinner(spEndAmPm, meridiem);
    }

    private void initViews(@NonNull View view) {
        /**
         * Connects blacklist form fields and buttons with their XML views.
         *
         * @param view root view containing the form controls
         */

        etName = view.findViewById(R.id.et_guest_name);
        etCnic = view.findViewById(R.id.et_cnic);
        etReason = view.findViewById(R.id.et_reason);
        spRole = view.findViewById(R.id.sp_role);
        spStartDate = view.findViewById(R.id.sp_start_date);
        spStartMonth = view.findViewById(R.id.sp_start_month);
        spStartYear = view.findViewById(R.id.sp_start_year);
        spStartTime = view.findViewById(R.id.sp_start_time);
        spStartAmPm = view.findViewById(R.id.sp_start_ampm);
        spEndDate = view.findViewById(R.id.sp_end_date);
        spEndMonth = view.findViewById(R.id.sp_end_month);
        spEndYear = view.findViewById(R.id.sp_end_year);
        spEndTime = view.findViewById(R.id.sp_end_time);
        spEndAmPm = view.findViewById(R.id.sp_end_ampm);
        btnBlacklist = view.findViewById(R.id.btn_blacklist);
        btnRemoveBlacklist = view.findViewById(R.id.btn_remove_blacklist);
    }

    private void openConfirmationPopup() {
        /**
         * Validates the form and opens the confirmation dialog before blacklisting.
         */

        if (!validateInput()) {
            return;
        }
        BlackListUserConfirmationAdminFragment dialog = new BlackListUserConfirmationAdminFragment();
        dialog.show(getChildFragmentManager(), "blacklist_confirmation");
    }

    private void submitBlacklist() {
        /**
         * Saves a blacklist record in Firestore after validating the selected period.
         */

        if (!validateInput()) {
            return;
        }

        long startTimeMilliseconds = parseDateTimeMillis(
                selectedValue(spStartDate),
                selectedValue(spStartMonth),
                selectedValue(spStartYear),
                selectedValue(spStartTime),
                selectedValue(spStartAmPm));

        long endTimeMilliseconds = parseDateTimeMillis(
                selectedValue(spEndDate),
                selectedValue(spEndMonth),
                selectedValue(spEndYear),
                selectedValue(spEndTime),
                selectedValue(spEndAmPm));

        if (startTimeMilliseconds < 0 || endTimeMilliseconds < 0) {
            toast("Please enter a valid blacklist period.");
            return;
        }

        if (endTimeMilliseconds <= startTimeMilliseconds) {
            toast("End date/time must be after start date/time.");
            return;
        }

        setBlacklistLoading(true);

        String fullName = etName.getText().toString().trim();
        String cnic = etCnic.getText().toString().trim();
        String normalizedCnic = normalizeCnic(cnic);
        String personType = selectedValue(spRole);
        String reason = etReason.getText().toString().trim();
        long now = System.currentTimeMillis();

        if (normalizedCnic.isEmpty()) {
            setBlacklistLoading(false);
            toast("Please enter a valid CNIC number.");
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> blacklistFields = new HashMap<>();
        blacklistFields.put("fullName", fullName);
        blacklistFields.put("cnicNumber", cnic);
        blacklistFields.put("normalizedCnic", normalizedCnic);
        blacklistFields.put("personType", personType);
        blacklistFields.put("blacklistReason", reason);
        blacklistFields.put("blacklistStartTimeMilliseconds", startTimeMilliseconds);
        blacklistFields.put("blacklistEndTimeMilliseconds", endTimeMilliseconds);
        blacklistFields.put("isActive", true);
        blacklistFields.put("blacklistedAt", now);

        if (currentUser != null) {
            blacklistFields.put("blacklistedByAdminUid", currentUser.getUid());

            if (currentUser.getEmail() != null) {
                blacklistFields.put("blacklistedByAdminEmail", currentUser.getEmail());
            }
        }

        firestore.collection("blacklistedIndividuals")
                .document(normalizedCnic)
                .set(blacklistFields)
                .addOnSuccessListener(unused -> {
                    updateMatchingUserAsBlacklisted(fullName, cnic, personType, reason,
                            startTimeMilliseconds, endTimeMilliseconds, now);

                    setBlacklistLoading(false);
                    toast("CNIC black-listed successfully.");
                    clearForm();
                })
                .addOnFailureListener(error -> {
                    setBlacklistLoading(false);
                    String message = error.getMessage();
                    toast(message == null ? "Could not black-list CNIC." : message);
                });
    }

    private void updateMatchingUserAsBlacklisted(String fullName, String cnic, String personType, String reason,
                                                 long startTimeMilliseconds, long endTimeMilliseconds, long now) {
        /**
         * Updates a matching user profile with blacklist information when found.
         *
         * @param fullName                  full name entered in the form
         * @param cnic                      CNIC entered in the form
         * @param personType                selected person type
         * @param reason                    blacklist reason
         * @param startTimeMilliseconds     blacklist start time in milliseconds
         * @param endTimeMilliseconds       blacklist end time in milliseconds
         * @param now                       current time in milliseconds
         */

        firestore.collection("users")
                .whereEqualTo("cnicNumber", cnic)
                .whereEqualTo("role", personType)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String docName = doc.getString("fullName");

                        if (docName == null || !docName.equalsIgnoreCase(fullName)) {
                            continue;
                        }

                        Map<String, Object> fields = new HashMap<>();
                        fields.put("isBlacklisted", true);
                        fields.put("blacklistReason", reason);
                        fields.put("blacklistedByAdmin", true);

                        // Existing project had this misspelled field name, so keep it for safety.
                        fields.put("blacklistStartTimeMiliseconds", startTimeMilliseconds);

                        // Correct field name for future use.
                        fields.put("blacklistStartTimeMilliseconds", startTimeMilliseconds);

                        fields.put("blacklistEndTimeMilliseconds", endTimeMilliseconds);
                        fields.put("blacklistUpdatedAt", now);

                        doc.getReference().update(fields);
                    }
                });
    }

    private void removeFromBlacklist() {
        /**
         * Marks a blacklist record as inactive while keeping it in Firestore as history.
         */

        if (!validateRemoveInput()) {
            return;
        }

        setRemoveBlacklistLoading(true);

        String cnic = etCnic.getText().toString().trim();
        String normalizedCnic = normalizeCnic(cnic);
        long now = System.currentTimeMillis();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> fields = new HashMap<>();
        fields.put("isActive", false);
        fields.put("removedAt", now);

        if (currentUser != null) {
            fields.put("removedByAdminUid", currentUser.getUid());

            if (currentUser.getEmail() != null) {
                fields.put("removedByAdminEmail", currentUser.getEmail());
            }
        }

        firestore.collection("blacklistedIndividuals")
                .document(normalizedCnic)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        setRemoveBlacklistLoading(false);
                        toast("This CNIC is not currently blacklisted.");
                        return;
                    }

                    Boolean isActive = documentSnapshot.getBoolean("isActive");

                    if (isActive == null || !isActive) {
                        setRemoveBlacklistLoading(false);
                        toast("This CNIC is not currently blacklisted.");
                        return;
                    }

                    firestore.collection("blacklistedIndividuals")
                            .document(normalizedCnic)
                            .update(fields)
                            .addOnSuccessListener(unused -> {
                                updateMatchingUserAsRemovedFromBlacklist(cnic, now);

                                setRemoveBlacklistLoading(false);
                                toast("CNIC removed from blacklist successfully.");
                                clearForm();
                            })
                            .addOnFailureListener(error -> {
                                setRemoveBlacklistLoading(false);
                                String message = error.getMessage();
                                toast(message == null ? "Could not remove CNIC from blacklist." : message);
                            });
                })
                .addOnFailureListener(error -> {
                    setRemoveBlacklistLoading(false);
                    String message = error.getMessage();
                    toast(message == null ? "Could not check blacklist record." : message);
                });
    }

    private void updateMatchingUserAsRemovedFromBlacklist(String cnic, long now) {
        /**
         * Clears blacklist indicators from matching user profiles.
         *
         * @param cnic CNIC entered in the form
         * @param now  current time in milliseconds
         */

        firestore.collection("users")
                .whereEqualTo("cnicNumber", cnic)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Map<String, Object> fields = new HashMap<>();
                        fields.put("isBlacklisted", false);
                        fields.put("blacklistReason", "");
                        fields.put("blacklistedByAdmin", false);
                        fields.put("blacklistUpdatedAt", now);

                        doc.getReference().update(fields);
                    }
                });
    }

    private boolean validateInput() {
        /**
         * Checks whether all required blacklist form fields have valid values.
         *
         * @return true if the form can be submitted, otherwise false
         */

        String fullName = etName.getText().toString().trim();
        String cnic = etCnic.getText().toString().trim();
        String reason = etReason.getText().toString().trim();
        String role = selectedValue(spRole);
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(cnic) || TextUtils.isEmpty(reason)) {
            toast("Please fill all required fields.");
            return false;
        }
        if ("Select person type".equals(role)) {
            toast("Please select a person type.");
            return false;
        }
        if (isPlaceholder(spStartDate) || isPlaceholder(spStartMonth) || isPlaceholder(spStartYear)
                || isPlaceholder(spStartTime) || isPlaceholder(spStartAmPm)
                || isPlaceholder(spEndDate) || isPlaceholder(spEndMonth) || isPlaceholder(spEndYear)
                || isPlaceholder(spEndTime) || isPlaceholder(spEndAmPm)) {
            toast("Please complete start/end date and time.");
            return false;
        }
        return true;
    }

    private boolean validateRemoveInput() {
        /**
         * Checks whether the CNIC needed for removal has been entered.
         *
         * @return true if removal can continue, otherwise false
         */

        String cnic = etCnic.getText().toString().trim();

        if (TextUtils.isEmpty(cnic)) {
            toast("Please enter CNIC number.");
            return false;
        }

        if (normalizeCnic(cnic).isEmpty()) {
            toast("Please enter a valid CNIC number.");
            return false;
        }

        return true;
    }

    private boolean isPlaceholder(Spinner spinner) {
        /**
         * Checks whether a spinner is still on its placeholder value.
         *
         * @param spinner spinner to check
         * @return true if the selected value is a placeholder
         */

        String value = selectedValue(spinner);
        return "Date".equals(value)
                || "Month".equals(value)
                || "Year".equals(value)
                || "Time".equals(value)
                || "AM/PM".equals(value);
    }

    private long parseDateTimeMillis(String day, String month, String year, String time, String ampm) {
        /**
         * Converts selected date and time values into milliseconds.
         *
         * @param day   selected day
         * @param month selected month
         * @param year  selected year
         * @param time  selected time
         * @param ampm  selected AM/PM value
         * @return date/time in milliseconds, or -1 if invalid
         */

        try {
            String source = day + " " + month + " " + year + " " + time + " " + ampm;
            SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy h:mm a", Locale.ENGLISH);
            Date date = formatter.parse(source);

            if (date == null) {
                return -1L;
            }

            return date.getTime();
        } catch (ParseException ignored) {
            return -1L;
        }
    }

    private void setBlacklistLoading(boolean loading) {
        /**
         * Updates button states while a blacklist record is being saved.
         *
         * @param loading true while saving is in progress
         */

        btnBlacklist.setEnabled(!loading);
        btnBlacklist.setText(loading ? "Black-listing..." : "Black-List");

        if (btnRemoveBlacklist != null) {
            btnRemoveBlacklist.setEnabled(!loading);
        }
    }

    private void setRemoveBlacklistLoading(boolean loading) {
        /**
         * Updates button states while a blacklist record is being removed.
         *
         * @param loading true while removal is in progress
         */

        btnRemoveBlacklist.setEnabled(!loading);
        btnRemoveBlacklist.setText(loading ? "Removing..." : "Remove");

        if (btnBlacklist != null) {
            btnBlacklist.setEnabled(!loading);
        }
    }

    private String selectedValue(Spinner spinner) {
        /**
         * Safely reads the selected spinner value.
         *
         * @param spinner spinner to read from
         * @return selected value or empty string if missing
         */

        Object value = spinner.getSelectedItem();
        return value == null ? "" : value.toString();
    }

    private void clearForm() {
        /**
         * Clears all form fields and resets dropdowns to their first value.
         */

        etName.setText("");
        etCnic.setText("");
        etReason.setText("");
        spRole.setSelection(0);
        spStartDate.setSelection(0);
        spStartMonth.setSelection(0);
        spStartYear.setSelection(0);
        spStartTime.setSelection(0);
        spStartAmPm.setSelection(0);
        spEndDate.setSelection(0);
        spEndMonth.setSelection(0);
        spEndYear.setSelection(0);
        spEndTime.setSelection(0);
        spEndAmPm.setSelection(0);
    }

    private String normalizeCnic(String cnic) {
        /**
         * Removes non-numeric characters from a CNIC.
         *
         * @param cnic CNIC entered by the user
         * @return CNIC containing digits only
         */

        if (cnic == null) {
            return "";
        }

        return cnic.replaceAll("[^0-9]", "");
    }

    private void toast(String message) {
        /**
         * Shows a short message if the fragment is attached.
         *
         * @param message message to show
         */

        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void bindSpinner(Spinner spinner, String[] items) {
        /**
         * Binds dropdown values to a spinner using the admin spinner style.
         *
         * @param spinner spinner to populate
         * @param items   values to show in the dropdown
         */

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_admin, items) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(position == 0 ? 0xFF999999 : 0xFF1E1E1E);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(position == 0 ? 0xFF999999 : 0xFF1E1E1E);
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_admin);
        spinner.setAdapter(adapter);
    }

    private String[] buildRange(String head, int start, int end) {
        /**
         * Builds a dropdown list with a header followed by a numeric range.
         *
         * @param head  first placeholder value
         * @param start starting number
         * @param end   ending number
         * @return array containing the placeholder and range values
         */

        String[] values = new String[(end - start + 2)];
        values[0] = head;
        for (int i = start; i <= end; i++) values[i - start + 1] = String.valueOf(i);
        return values;
    }
}
