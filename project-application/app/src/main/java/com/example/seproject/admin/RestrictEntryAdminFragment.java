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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Admin UI to configure restricted entry time window (placeholder data binding).
 *
 * @author Moiz Imran
 * @version 1.0
 */

public class RestrictEntryAdminFragment extends Fragment {
    /**
     * Creates the restrict entry admin fragment.
     */

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
    private MaterialButton btnRestrictEntry;
    private FirebaseFirestore firestore;

    public RestrictEntryAdminFragment() {
        super(R.layout.restrict_entry_admin_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        /**
         * Sets up the restrict entry form, spinner values, back button, and submit action.
         *
         * @param view               root view of the fragment
         * @param savedInstanceState previous state if the fragment is being recreated
         */

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        firestore = FirebaseFirestore.getInstance();
        initViews(view);

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
        btnRestrictEntry.setOnClickListener(v -> restrictEntry());
    }

    private void initViews(@NonNull View view) {
        /**
         * Connects form fields and buttons with their XML views.
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
        btnRestrictEntry = view.findViewById(R.id.btn_restrict_entry);
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
                View v = super.getView(position, convertView, parent);
                TextView text = v.findViewById(android.R.id.text1);
                text.setTextColor(position == 0 ? 0xFF999999 : 0xFF1E1E1E);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                TextView text = v.findViewById(android.R.id.text1);
                text.setTextColor(position == 0 ? 0xFF999999 : 0xFF1E1E1E);
                return v;
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

    private void restrictEntry() {
        /**
         * Validates the restrict entry form and saves the blacklist record in Firestore.
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
            toast("Please enter a valid restriction period.");
            return;
        }

        if (endTimeMilliseconds <= startTimeMilliseconds) {
            toast("End date/time must be after start date/time.");
            return;
        }

        setLoading(true);

        String fullName = etName.getText().toString().trim();
        String cnic = etCnic.getText().toString().trim();
        String normalizedCnic = normalizeCnic(cnic);
        String personType = selectedValue(spRole);
        String reason = etReason.getText().toString().trim();
        long now = System.currentTimeMillis();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> blacklistFields = new HashMap<>();
        blacklistFields.put("fullName", fullName);
        blacklistFields.put("cnicNumber", cnic);
        blacklistFields.put("normalizedCnic", normalizedCnic);
        blacklistFields.put("personType", personType);
        blacklistFields.put("blacklistReason", reason);
        blacklistFields.put("blacklistStartTimeMilliseconds", startTimeMilliseconds);
        blacklistFields.put("blacklistEndTimeMilliseconds", endTimeMilliseconds);
        blacklistFields.put("blacklistedAt", now);
        blacklistFields.put("isActive", true);

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
                    setLoading(false);
                    toast("Entry restricted successfully.");
                    clearForm();
                })
                .addOnFailureListener(error -> {
                    setLoading(false);
                    String message = error.getMessage();
                    toast(message == null ? "Could not restrict entry." : message);
                });
    }

    private boolean validateInput() {
        /**
         * Checks whether the required restrict entry fields have valid values.
         *
         * @return true if the form can be submitted, otherwise false
         */

        String fullName = etName.getText().toString().trim();
        String cnic = etCnic.getText().toString().trim();
        String reason = etReason.getText().toString().trim();
        String personType = selectedValue(spRole);

        if (TextUtils.isEmpty(fullName)) {
            toast("Please enter name.");
            return false;
        }

        if ("Select person type".equals(personType)) {
            toast("Please select person type.");
            return false;
        }

        if (TextUtils.isEmpty(cnic)) {
            toast("Please enter CNIC number.");
            return false;
        }

        if (normalizeCnic(cnic).isEmpty()) {
            toast("Please enter a valid CNIC number.");
            return false;
        }

        if (TextUtils.isEmpty(reason)) {
            toast("Please enter restriction reason.");
            return false;
        }

        return true;
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
            if ("Time".equals(time)) {
                time = "12:00";
            }

            if ("AM/PM".equals(ampm)) {
                ampm = "AM";
            }

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

    private String selectedValue(Spinner spinner) {
        /**
         * Safely reads the selected spinner value.
         *
         * @param spinner spinner to read from
         * @return selected value or empty string if missing
         */

        Object selected = spinner.getSelectedItem();
        return selected == null ? "" : selected.toString();
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

    private void setLoading(boolean loading) {
        /**
         * Enables or disables the restrict button while saving the record.
         *
         * @param loading true while the record is being saved
         */

        btnRestrictEntry.setEnabled(!loading);
        btnRestrictEntry.setText(loading ? "Restricting..." : "Restrict Entry");
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
}
