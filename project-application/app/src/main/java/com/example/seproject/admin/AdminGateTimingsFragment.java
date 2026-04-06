package com.example.seproject.admin;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.seproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

/**
 * Sets up Gate Timings for days to set times during which gate access is allowed
 *
 * @author Umer Ashraf
 * @version 1.0
 */

public class AdminGateTimingsFragment extends Fragment {

    private boolean hasUnsavedChanges = false;

    public AdminGateTimingsFragment() {
        super(R.layout.acitivity_admin_gate_timings);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (hasUnsavedChanges) {
                showBackConfirmationDialog();
            } else {
                requireActivity().onBackPressed();
            }
        });
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> {
            if (hasUnsavedChanges) {
                showCancelConfirmationDialog();
            } else {
                requireActivity().onBackPressed();
            }
        });
        view.findViewById(R.id.btnSave).setOnClickListener(v -> {
            if (hasUnsavedChanges) {
                showSaveConfirmationDialog();
            } else {
                Toast.makeText(getContext(), "No changes to save", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup Monday
        setupDayToggle(view, R.id.rowMonday, R.id.detailsMonday, R.id.iconMonday);
        setupCheckbox(view, R.id.cbGateOpenMonday);
        setupTimePicker(view, R.id.btnOpeningTimeMonday);
        setupTimePicker(view, R.id.btnClosingTimeMonday);

        // Setup Tuesday
        setupDayToggle(view, R.id.rowTuesday, R.id.detailsTuesday, R.id.iconTuesday);
        setupCheckbox(view, R.id.cbGateOpenTuesday);
        setupTimePicker(view, R.id.btnOpeningTimeTuesday);
        setupTimePicker(view, R.id.btnClosingTimeTuesday);

        // Setup Wednesday
        setupDayToggle(view, R.id.rowWednesday, R.id.detailsWednesday, R.id.iconWednesday);
        setupCheckbox(view, R.id.cbGateOpenWednesday);
        setupTimePicker(view, R.id.btnOpeningTimeWednesday);
        setupTimePicker(view, R.id.btnClosingTimeWednesday);

        // Setup Thursday
        setupDayToggle(view, R.id.rowThursday, R.id.detailsThursday, R.id.iconThursday);
        setupCheckbox(view, R.id.cbGateOpenThursday);
        setupTimePicker(view, R.id.btnOpeningTimeThursday);
        setupTimePicker(view, R.id.btnClosingTimeThursday);

        // Setup Friday
        setupDayToggle(view, R.id.rowFriday, R.id.detailsFriday, R.id.iconFriday);
        setupCheckbox(view, R.id.cbGateOpenFriday);
        setupTimePicker(view, R.id.btnOpeningTimeFriday);
        setupTimePicker(view, R.id.btnClosingTimeFriday);

        // Setup Saturday
        setupDayToggle(view, R.id.rowSaturday, R.id.detailsSaturday, R.id.iconSaturday);
        setupCheckbox(view, R.id.cbGateOpenSaturday);
        setupTimePicker(view, R.id.btnOpeningTimeSaturday);
        setupTimePicker(view, R.id.btnClosingTimeSaturday);

        // Setup Sunday
        setupDayToggle(view, R.id.rowSunday, R.id.detailsSunday, R.id.iconSunday);
        setupCheckbox(view, R.id.cbGateOpenSunday);
        setupTimePicker(view, R.id.btnOpeningTimeSunday);
        setupTimePicker(view, R.id.btnClosingTimeSunday);
    }

    private void showBackConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Discard Changes?")
                .setMessage("Are you sure you want to go back? Any unsaved changes will be lost.")
                .setPositiveButton("Discard", (dialog, which) -> requireActivity().onBackPressed())
                .setNegativeButton("Keep Editing", null)
                .show();
    }

    private void showCancelConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Discard Changes?")
                .setMessage("Are you sure you want to cancel changes?")
                .setPositiveButton("Discard", (dialog, which) -> requireActivity().onBackPressed())
                .setNegativeButton("Keep Editing", null)
                .show();
    }

    private void showSaveConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Save Gate Timings")
                .setMessage("Are you sure you want to save changes?")
                .setPositiveButton("Save", (dialog, which) -> {
                    // TODO: Implement your saving logic here
                    Toast.makeText(getContext(), "Gate timings saved!", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupDayToggle(View view, int rowId, int detailsId, int iconId) {
        View row = view.findViewById(rowId);
        View details = view.findViewById(detailsId);
        TextView icon = view.findViewById(iconId);

        row.setOnClickListener(v -> {
            if (details.getVisibility() == View.VISIBLE) {
                details.setVisibility(View.GONE);
                icon.setText("\u203A"); // Right chevron icon
            } else {
                details.setVisibility(View.VISIBLE);
                icon.setText("\u2304"); // Down chevron icon
            }
        });
    }

    private void setupCheckbox(View view, int checkboxId) {
        // Triggered only on physical user taps
        view.findViewById(checkboxId).setOnClickListener(v -> hasUnsavedChanges = true);
    }

    private void setupTimePicker(View view, int buttonId) {
        MaterialButton button = view.findViewById(buttonId);

        button.setOnClickListener(v -> {
            // Defaulting to 10:00 for the picker starting position
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getContext(),
                    (picker, hourOfDay, minute) -> {
                        String amPm = hourOfDay >= 12 ? "pm" : "am";
                        int displayHour = hourOfDay % 12;
                        if (displayHour == 0) displayHour = 12;

                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d%s", displayHour, minute, amPm);

                        // Check if the time actually changed before updating the flag
                        if (!button.getText().toString().equals(formattedTime)) {
                            button.setText(formattedTime);
                            hasUnsavedChanges = true;
                        }
                    },
                    10, 0, false // false sets it to 12-hour AM/PM format
            );
            timePickerDialog.show();
        });
    }
}