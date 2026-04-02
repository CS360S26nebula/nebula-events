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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private FirebaseFirestore firestore;

    public BlackListUserAdminFragment() {
        super(R.layout.blacl_list_user_admin_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
        firestore = FirebaseFirestore.getInstance();
        initViews(view);
        btnBlacklist.setOnClickListener(v -> openConfirmationPopup());
        getChildFragmentManager().setFragmentResultListener(
                REQUEST_KEY_BLACKLIST_CONFIRM,
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    if (bundle.getBoolean(BUNDLE_KEY_CONFIRMED, false)) {
                        submitBlacklist();
                    }
                });

        bindSpinner(spRole, new String[]{"Select user role", "Admin", "Faculty", "Guard"});
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
    }

    private void openConfirmationPopup() {
        if (!validateInput()) {
            return;
        }
        BlackListUserConfirmationAdminFragment dialog = new BlackListUserConfirmationAdminFragment();
        dialog.show(getChildFragmentManager(), "blacklist_confirmation");
    }

    private void submitBlacklist() {
        if (!validateInput()) {
            return;
        }
        long startTimeMiliseconds = parseDateTimeMillis(
                selectedValue(spStartDate),
                selectedValue(spStartMonth),
                selectedValue(spStartYear),
                selectedValue(spStartTime),
                selectedValue(spStartAmPm));
        long endTimeMiliseconds = parseDateTimeMillis(
                selectedValue(spEndDate),
                selectedValue(spEndMonth),
                selectedValue(spEndYear),
                selectedValue(spEndTime),
                selectedValue(spEndAmPm));
        if (startTimeMiliseconds < 0 || endTimeMiliseconds < 0) {
            toast("Please enter a valid blacklist period.");
            return;
        }
        if (endTimeMiliseconds <= startTimeMiliseconds) {
            toast("End date/time must be after start date/time.");
            return;
        }

        btnBlacklist.setEnabled(false);
        btnBlacklist.setText("Black-listing...");

        String fullName = etName.getText().toString().trim();
        String cnic = etCnic.getText().toString().trim();
        String role = selectedValue(spRole);
        String reason = etReason.getText().toString().trim();
        long now = System.currentTimeMillis();

        firestore.collection("users")
                .whereEqualTo("cnicNumber", cnic)
                .whereEqualTo("role", role)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        setBlacklistLoading(false);
                        toast("No matching user found.");
                        return;
                    }

                    int updates = 0;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String docName = doc.getString("fullName");
                        if (docName == null || !docName.equalsIgnoreCase(fullName)) {
                            continue;
                        }
                        Map<String, Object> fields = new HashMap<>();
                        fields.put("isBlacklisted", true);
                        fields.put("blacklistReason", reason);
                        fields.put("blacklistedByAdmin", true);
                        fields.put("blacklistStartTimeMiliseconds", startTimeMiliseconds);
                        fields.put("blacklistEndTimeMilliseconds", endTimeMiliseconds);
                        fields.put("blacklistUpdatedAt", now);
                        doc.getReference().update(fields);
                        updates++;
                    }

                    setBlacklistLoading(false);
                    if (updates == 0) {
                        toast("No matching user found.");
                        return;
                    }
                    toast("User black-listed successfully.");
                    clearForm();
                })
                .addOnFailureListener(error -> {
                    setBlacklistLoading(false);
                    String message = error.getMessage();
                    toast(message == null ? "Could not black-list user." : message);
                });
    }

    private boolean validateInput() {
        String fullName = etName.getText().toString().trim();
        String cnic = etCnic.getText().toString().trim();
        String reason = etReason.getText().toString().trim();
        String role = selectedValue(spRole);
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(cnic) || TextUtils.isEmpty(reason)) {
            toast("Please fill all required fields.");
            return false;
        }
        if ("Select user role".equals(role)) {
            toast("Please select a user role.");
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

    private boolean isPlaceholder(Spinner spinner) {
        String value = selectedValue(spinner);
        return "Date".equals(value)
                || "Month".equals(value)
                || "Year".equals(value)
                || "Time".equals(value)
                || "AM/PM".equals(value);
    }

    private long parseDateTimeMillis(String day, String month, String year, String time, String ampm) {
        try {
            String source = day + " " + month + " " + year + " " + time + " " + ampm;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy h:mm a", Locale.ENGLISH);
            LocalDateTime dateTime = LocalDateTime.parse(source, formatter);
            return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception ignored) {
            return -1L;
        }
    }

    private void setBlacklistLoading(boolean loading) {
        btnBlacklist.setEnabled(!loading);
        btnBlacklist.setText(loading ? "Black-listing..." : "Black-List");
    }

    private String selectedValue(Spinner spinner) {
        Object value = spinner.getSelectedItem();
        return value == null ? "" : value.toString();
    }

    private void clearForm() {
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

    private void toast(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void bindSpinner(Spinner spinner, String[] items) {
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
        String[] values = new String[(end - start + 2)];
        values[0] = head;
        for (int i = start; i <= end; i++) values[i - start + 1] = String.valueOf(i);
        return values;
    }
}
