package com.example.seproject.admin;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.seproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Responsible for managing the gate access timings. Allows administrators
 * to set specific opening and closing times for each day of the week.
 * It handles local state, unsaved changes tracking, and ensures synchronization
 * with the database.
 *
 * @author Umer Ashraf
 * @version 2.0
 * 100th Commit :)
 */
public class AdminGateTimingsFragment extends Fragment {

    /** Flag to track if changes were made. */
    private boolean hasUnsavedChanges = false;

    /** Local memory holding the data model for each day of the week. */
    private List<DailyGateTimings> weeklyTimings = new ArrayList<>();

    /** Instance of the database. */
    private FirebaseFirestore database;

    /**
     * Inflates the associated layout for the gate timings activity/fragment.
     */
    public AdminGateTimingsFragment() {
        super(R.layout.acitivity_admin_gate_timings);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Initializes the database (default values), and then triggers fetch
     * from the database, and wires up all click listeners.
     *
     * @param view The View returned by {@link #onCreateView}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseFirestore.getInstance();

        initializeDefaultData();
        fetchGateTimingsFromDatabase(view);

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

        // Monday
        setupDayToggle(view, R.id.rowMonday, R.id.detailsMonday, R.id.iconMonday);
        setupCheckbox(view, R.id.cbGateOpenMonday, weeklyTimings.get(0));
        setupTimePicker(view, R.id.btnOpeningTimeMonday, weeklyTimings.get(0), true);
        setupTimePicker(view, R.id.btnClosingTimeMonday, weeklyTimings.get(0), false);

        // Tuesday
        setupDayToggle(view, R.id.rowTuesday, R.id.detailsTuesday, R.id.iconTuesday);
        setupCheckbox(view, R.id.cbGateOpenTuesday, weeklyTimings.get(1));
        setupTimePicker(view, R.id.btnOpeningTimeTuesday, weeklyTimings.get(1), true);
        setupTimePicker(view, R.id.btnClosingTimeTuesday, weeklyTimings.get(1), false);

        // Wednesday
        setupDayToggle(view, R.id.rowWednesday, R.id.detailsWednesday, R.id.iconWednesday);
        setupCheckbox(view, R.id.cbGateOpenWednesday, weeklyTimings.get(2));
        setupTimePicker(view, R.id.btnOpeningTimeWednesday, weeklyTimings.get(2), true);
        setupTimePicker(view, R.id.btnClosingTimeWednesday, weeklyTimings.get(2), false);

        // Thursday
        setupDayToggle(view, R.id.rowThursday, R.id.detailsThursday, R.id.iconThursday);
        setupCheckbox(view, R.id.cbGateOpenThursday, weeklyTimings.get(3));
        setupTimePicker(view, R.id.btnOpeningTimeThursday, weeklyTimings.get(3), true);
        setupTimePicker(view, R.id.btnClosingTimeThursday, weeklyTimings.get(3), false);

        // Friday
        setupDayToggle(view, R.id.rowFriday, R.id.detailsFriday, R.id.iconFriday);
        setupCheckbox(view, R.id.cbGateOpenFriday, weeklyTimings.get(4));
        setupTimePicker(view, R.id.btnOpeningTimeFriday, weeklyTimings.get(4), true);
        setupTimePicker(view, R.id.btnClosingTimeFriday, weeklyTimings.get(4), false);

        // Saturday
        setupDayToggle(view, R.id.rowSaturday, R.id.detailsSaturday, R.id.iconSaturday);
        setupCheckbox(view, R.id.cbGateOpenSaturday, weeklyTimings.get(5));
        setupTimePicker(view, R.id.btnOpeningTimeSaturday, weeklyTimings.get(5), true);
        setupTimePicker(view, R.id.btnClosingTimeSaturday, weeklyTimings.get(5), false);

        // Sunday
        setupDayToggle(view, R.id.rowSunday, R.id.detailsSunday, R.id.iconSunday);
        setupCheckbox(view, R.id.cbGateOpenSunday, weeklyTimings.get(6));
        setupTimePicker(view, R.id.btnOpeningTimeSunday, weeklyTimings.get(6), true);
        setupTimePicker(view, R.id.btnClosingTimeSunday, weeklyTimings.get(6), false);
    }

    /**
     * Initializes the weeklyTimings list with objects with default values.
     * Acts as a safe skeleton for the UI to bind to while the actual data
     * is being fetched asynchronously.
     */
    private void initializeDefaultData() {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : days) {
            weeklyTimings.add(new DailyGateTimings(day, false, "10:00am", "10:00pm"));
        }
    }

    /**
     * Displays an alert dialog confirming if the user wants to discard unsaved changes
     * when attempting to navigate back via the back button.
     */
    private void showBackConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Discard Changes?")
                .setMessage("Are you sure you want to go back? Any unsaved changes will be lost.")
                .setPositiveButton("Discard", (dialog, which) -> requireActivity().onBackPressed())
                .setNegativeButton("Keep Editing", null)
                .show();
    }

    /**
     * Displays an alert dialog confirming if the user wants to discard unsaved changes
     * when clicking the Cancel button.
     */
    private void showCancelConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Discard Changes?")
                .setMessage("Are you sure you want to cancel changes?")
                .setPositiveButton("Discard", (dialog, which) -> requireActivity().onBackPressed())
                .setNegativeButton("Keep Editing", null)
                .show();
    }

    /**
     * Displays a confirmation dialog before pushing changes to the database
     * when clicking the save button
     */
    private void showSaveConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Save Gate Timings")
                .setMessage("Are you sure you want to save changes?")
                .setPositiveButton("Save", (dialog, which) -> {

                    WriteBatch batch = database.batch();
                    for (DailyGateTimings timing : weeklyTimings) {
                        DocumentReference docRef = database.collection("GateTimings").document(timing.getDayOfWeek());
                        batch.set(docRef, timing);
                    }

                    batch.commit().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Gate timings saved", Toast.LENGTH_SHORT).show();
                            hasUnsavedChanges = false;
                            requireActivity().onBackPressed();
                        } else {
                            Toast.makeText(getContext(), "Failed to save.", Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Configures the expand/collapse behavior for a specific day's row.
     *
     * @param view      The root view of the fragment.
     * @param rowId     The view ID of the header row.
     * @param detailsId The view ID of the expanding/collapsing container.
     * @param iconId    The view ID of the icon indicating open/closed state.
     */
    private void setupDayToggle(View view, int rowId, int detailsId, int iconId) {
        View row = view.findViewById(rowId);
        View details = view.findViewById(detailsId);
        TextView icon = view.findViewById(iconId);

        row.setOnClickListener(v -> {
            if (details.getVisibility() == View.VISIBLE) {
                details.setVisibility(View.GONE);
                icon.setText("\u203A");
            } else {
                details.setVisibility(View.VISIBLE);
                icon.setText("\u2304");
            }
        });
    }

    /**
     * Binds a checkbox to its corresponding data model object.
     * Updates the model and marks the state as having unsaved changes upon interaction.
     *
     * @param view       The root view of the fragment.
     * @param checkboxId The view ID of the checkbox.
     * @param timingDay  The Java model representing the current day's configuration.
     */
    private void setupCheckbox(View view, int checkboxId, DailyGateTimings timingDay) {
        CheckBox checkBox = view.findViewById(checkboxId);

        checkBox.setOnClickListener(v -> {
            timingDay.setGateOpen(checkBox.isChecked());
            hasUnsavedChanges = true;
        });
    }

    /**
     * When a time is selected, it updates the button text and the underlying data model.
     *
     * @param view          The root view of the fragment.
     * @param buttonId      The view ID of the button triggering the picker.
     * @param timingDay     The Java model representing the current day's configuration.
     * @param isOpeningTime True if this picker is setting the opening time, false for closing time.
     */
    private void setupTimePicker(View view, int buttonId, DailyGateTimings timingDay, boolean isOpeningTime) {
        MaterialButton button = view.findViewById(buttonId);

        button.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getContext(),
                    (picker, hourOfDay, minute) -> {
                        String amPm = hourOfDay >= 12 ? "pm" : "am";
                        int displayHour = hourOfDay % 12;
                        if (displayHour == 0) displayHour = 12;

                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d%s", displayHour, minute, amPm);

                        if (!button.getText().toString().equals(formattedTime)) {
                            button.setText(formattedTime);

                            if (isOpeningTime) {
                                timingDay.setOpeningTime(formattedTime);
                            } else {
                                timingDay.setClosingTime(formattedTime);
                            }

                            hasUnsavedChanges = true;
                        }
                    },
                    10, 0, false
            );
            timePickerDialog.show();
        });
    }

    /**
     * Queries Firestore for the 'GateTimings'.
     * Converts received documents into DailyGateTimings objects and updates the local list and UI.
     *
     * @param view The root view of the fragment.
     */
    private void fetchGateTimingsFromDatabase(View view) {
        Toast.makeText(getContext(), "Loading timings...", Toast.LENGTH_SHORT).show();

        database.collection("GateTimings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : task.getResult()) {
                            DailyGateTimings timing = document.toObject(DailyGateTimings.class);
                            updateLocalTimingList(timing);
                            updateUIForDay(view, timing);
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load timings.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Merges fetched data form the database into the existing local memory.
     *
     * @param fetchedTiming The newly downloaded timing object from Firestore.
     */
    private void updateLocalTimingList(DailyGateTimings fetchedTiming) {
        for (DailyGateTimings local : weeklyTimings) {
            if (local.getDayOfWeek().equals(fetchedTiming.getDayOfWeek())) {
                local.setGateOpen(fetchedTiming.isGateOpen());
                local.setOpeningTime(fetchedTiming.getOpeningTime());
                local.setClosingTime(fetchedTiming.getClosingTime());
                break;
            }
        }
    }

    /**
     * Matches a DailyGateTimings object to its specific elements by day string
     * and updates the values.
     *
     * @param view   The root view of the fragment.
     * @param timing The specific day's configuration to display.
     */
    private void updateUIForDay(View view, DailyGateTimings timing) {
        int checkboxId = 0, openBtnId = 0, closeBtnId = 0;

        switch (timing.getDayOfWeek()) {
            case "Monday":
                checkboxId = R.id.cbGateOpenMonday;
                openBtnId = R.id.btnOpeningTimeMonday;
                closeBtnId = R.id.btnClosingTimeMonday;
                break;
            case "Tuesday":
                checkboxId = R.id.cbGateOpenTuesday;
                openBtnId = R.id.btnOpeningTimeTuesday;
                closeBtnId = R.id.btnClosingTimeTuesday;
                break;
            case "Wednesday":
                checkboxId = R.id.cbGateOpenWednesday;
                openBtnId = R.id.btnOpeningTimeWednesday;
                closeBtnId = R.id.btnClosingTimeWednesday;
                break;
            case "Thursday":
                checkboxId = R.id.cbGateOpenThursday;
                openBtnId = R.id.btnOpeningTimeThursday;
                closeBtnId = R.id.btnClosingTimeThursday;
                break;
            case "Friday":
                checkboxId = R.id.cbGateOpenFriday;
                openBtnId = R.id.btnOpeningTimeFriday;
                closeBtnId = R.id.btnClosingTimeFriday;
                break;
            case "Saturday":
                checkboxId = R.id.cbGateOpenSaturday;
                openBtnId = R.id.btnOpeningTimeSaturday;
                closeBtnId = R.id.btnClosingTimeSaturday;
                break;
            case "Sunday":
                checkboxId = R.id.cbGateOpenSunday;
                openBtnId = R.id.btnOpeningTimeSunday;
                closeBtnId = R.id.btnClosingTimeSunday;
                break;
        }

        if (checkboxId != 0) {
            ((android.widget.CheckBox) view.findViewById(checkboxId)).setChecked(timing.isGateOpen());
            ((MaterialButton) view.findViewById(openBtnId)).setText(timing.getOpeningTime());
            ((MaterialButton) view.findViewById(closeBtnId)).setText(timing.getClosingTime());
        }
    }
}