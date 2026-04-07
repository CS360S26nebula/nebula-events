package com.example.seproject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Admin/Guard form to register a visitor in Firestore {@code requests}. Documents are written as
 * {@code Approved} with a generated {@code passId}. Validation and id/status generation are
 * delegated to {@link CreateVisitorEntryRules}. {@code requesterUid} and
 * {@code requesterEmail} are set from the <strong>host faculty</strong> resolved via
 * {@code users} collection (email must match their profile / login email) so the row appears in
 * that faculty's lists in {@link RequestListActivity}. {@code createdByUid} / {@code createdByEmail}
 * record the staff member who submitted the form.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class CreateVisitorEntry extends Fragment {
    private EditText editGuestName;
    private EditText editInvitorName;
    private EditText editInvitorEmail;
    private EditText editPhone;
    private EditText editCnic;
    private EditText editVisitDate;
    private Spinner spVisitTime;
    private Spinner spVisitorType;
    private EditText editVisitReason;
    private MaterialButton btnSubmitRequest;
    private FirebaseFirestore firestore;

    public CreateVisitorEntry() {
        super(R.layout.create_visitor_entry_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
        firestore = FirebaseFirestore.getInstance();
        initViews(view);
        bindSpinner(spVisitorType, new String[]{"Select visitor type", "Faculty", "Guest", "Contractor"});
        bindSpinner(spVisitTime, buildVisitTimeOptions());
        editVisitDate.setOnClickListener(v -> openDatePicker());
        btnSubmitRequest.setOnClickListener(v -> submitRequest());
    }

    private void initViews(@NonNull View view) {
        editGuestName = view.findViewById(R.id.et_guest_name);
        editPhone = view.findViewById(R.id.et_phone);
        editVisitReason = view.findViewById(R.id.et_visit_reason);
        editInvitorName = view.findViewById(R.id.et_invitor_name);
        editInvitorEmail = view.findViewById(R.id.et_invitor_email);
        editCnic = view.findViewById(R.id.et_cnic);
        editVisitDate = view.findViewById(R.id.et_visit_date);
        spVisitTime = view.findViewById(R.id.sp_visit_time);
        spVisitorType = view.findViewById(R.id.sp_visitor_type);
        btnSubmitRequest = view.findViewById(R.id.btn_submit_request);
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (picker, year, month, dayOfMonth) -> {
                    Calendar picked = Calendar.getInstance();
                    picked.set(year, month, dayOfMonth);
                    String formatted = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(picked.getTime());
                    editVisitDate.setText(formatted);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private String selectedValue(Spinner spinner) {
        Object selected = spinner.getSelectedItem();
        return selected == null ? "" : selected.toString();
    }

    private boolean validateInput(String visitorName, String invitorName, String invitorEmail,
                                  String visitorPhoneNumber,
                                  String visitorCnic, String visitReason, String visitorType,
                                  String visitDate, String visitTime) {
        if (!FacultyVisitorRequestValidator.hasRequiredFields(visitorName, invitorName,
                visitorPhoneNumber, visitorCnic, visitReason, visitDate)) {
            toast("Please fill all required fields.");
            return false;
        }
        if (EmptyStrings.isEmpty(invitorEmail)) {
            toast("Please enter the invitor's email.");
            return false;
        }
        if (!CreateVisitorEntryRules.isInvitorEmailFormatValid(invitorEmail)) {
            toast("Please enter a valid invitor email.");
            return false;
        }
        if (!FacultyVisitorRequestValidator.isVisitorTypeSelected(visitorType)) {
            toast("Please select visitor type.");
            return false;
        }
        if (!FacultyVisitorRequestValidator.isVisitTimeSelected(visitTime)) {
            toast("Please select visit time.");
            return false;
        }
        return true;
    }

    /**
     * Looks up {@code users} by exact {@code email} field, then falls back to lowercase if different.
     */
    private void lookupFacultyByEmail(@NonNull String emailTrimmed,
                                    @NonNull BiConsumer<String, String> onFacultyFound,
                                    @NonNull Runnable onNotFound) {
        firestore.collection("users")
                .whereEqualTo("email", emailTrimmed)
                .limit(10)
                .get()
                .addOnSuccessListener(snapshot -> {
                    String uid = null;
                    String profileEmail = null;
                    for (QueryDocumentSnapshot doc : snapshot) {
                        if ("Faculty".equals(doc.getString("role"))) {
                            uid = doc.getId();
                            profileEmail = doc.getString("email");
                            break;
                        }
                    }
                    if (uid != null) {
                        onFacultyFound.accept(uid, profileEmail != null ? profileEmail : emailTrimmed);
                        return;
                    }
                    String lower = emailTrimmed.toLowerCase(Locale.ROOT);
                    if (!lower.equals(emailTrimmed)) {
                        lookupFacultyByEmail(lower, onFacultyFound, onNotFound);
                    } else {
                        onNotFound.run();
                    }
                })
                .addOnFailureListener(e -> {
                    String message = e.getMessage();
                    toast(message == null ? "Could not verify invitor email." : message);
                });
    }

    private void createVisitorEntry(String visitorName, String invitorName, String visitorPhoneNumber,
                                    String visitorCnic, String visitReason, String visitorType,
                                    String visitDate, String visitTime,
                                    @NonNull String facultyUid, @NonNull String facultyEmail,
                                    @NonNull String invitorEmailEntered) {
        String requestId = "ID-" + System.currentTimeMillis();
        String passId = CreateVisitorEntryRules.buildPassId();
        String rejectionReason = "";
        Request requestObj = new Request(
                requestId,
                visitorName,
                visitorCnic,
                visitorPhoneNumber,
                visitReason,
                visitDate,
                visitTime,
                invitorName,
                visitorType,
                CreateVisitorEntryRules.statusForStaffCreatedEntry(),
                System.currentTimeMillis(),
                passId,
                rejectionReason

        );
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("requestId", requestObj.getRequestId());
        requestMap.put("visitorName", requestObj.getVisitorName());
        requestMap.put("invitorName", requestObj.getInvitorName());
        requestMap.put("visitorMobileNumber", requestObj.getVisitorMobileNumber());
        requestMap.put("visitorCnic", requestObj.getVisitorCnic());
        requestMap.put("visitReason", requestObj.getVisitReason());
        requestMap.put("invitorType", requestObj.getInvitorType());
        requestMap.put("visitDate", requestObj.getVisitDate());
        requestMap.put("visitTime", requestObj.getVisitTime());
        requestMap.put("requestStatus", requestObj.getRequestStatus());
        requestMap.put("createdAtMillis", requestObj.getCreatedAtMillis());
        requestMap.put("createdAt", FieldValue.serverTimestamp());
        requestMap.put("passId", requestObj.getPassId());

        requestMap.put("requesterUid", facultyUid);
        requestMap.put("requesterEmail", facultyEmail);
        requestMap.put("invitorEmail", invitorEmailEntered);

        FirebaseUser staff = FirebaseAuth.getInstance().getCurrentUser();
        if (staff != null) {
            requestMap.put("createdByUid", staff.getUid());
            if (staff.getEmail() != null) {
                requestMap.put("createdByEmail", staff.getEmail());
            }
        }

        firestore.collection("requests").document(requestId).set(requestMap)
                .addOnSuccessListener(unused -> {
                    toast("Visitor entry created for " + facultyEmail + ".");
                    clearForm();
                })
                .addOnFailureListener(error -> {
                    String message = error.getMessage();
                    toast(message == null ? "Failed to create visitor entry." : message);
                });
    }

//    private void submitRequest() {
//        String visitorName = editGuestName.getText().toString().trim();
//        String invitorName = editInvitorName.getText().toString().trim();
//        String invitorEmail = editInvitorEmail.getText().toString().trim();
//        String visitorPhoneNumber = editPhone.getText().toString().trim();
//        String visitorCnic = editCnic.getText().toString().trim();
//        String visitReason = editVisitReason.getText().toString().trim();
//        String visitorType = selectedValue(spVisitorType);
//        String visitDate = editVisitDate.getText().toString().trim();
//        String visitTime = selectedValue(spVisitTime);
//        if (!validateInput(visitorName, invitorName, invitorEmail, visitorPhoneNumber, visitorCnic,
//                visitReason, visitorType, visitDate, visitTime)) {
//            return;
//        }
//
//        final String emailKey = invitorEmail;
//        lookupFacultyByEmail(emailKey,
//                (facultyUid, facultyEmail) -> createVisitorEntry(
//                        visitorName, invitorName, visitorPhoneNumber, visitorCnic, visitReason,
//                        visitorType, visitDate, visitTime, facultyUid, facultyEmail, emailKey),
//                () -> toast("No faculty account with this email exists"));
//    }

    /* EDIT BY UMER:
    * editted function to check if requested time falls within gate times.
    * */
private void submitRequest() {
    String visitorName = editGuestName.getText().toString().trim();
    String invitorName = editInvitorName.getText().toString().trim();
    String invitorEmail = editInvitorEmail.getText().toString().trim();
    String visitorPhoneNumber = editPhone.getText().toString().trim();
    String visitorCnic = editCnic.getText().toString().trim();
    String visitReason = editVisitReason.getText().toString().trim();
    String visitorType = selectedValue(spVisitorType);
    String visitDate = editVisitDate.getText().toString().trim();
    String visitTime = selectedValue(spVisitTime);

    if (!validateInput(visitorName, invitorName, invitorEmail, visitorPhoneNumber, visitorCnic,
            visitReason, visitorType, visitDate, visitTime)) {
        return;
    }

    btnSubmitRequest.setEnabled(false);

    VisitorRequestTimerValidator.validate(visitDate, visitTime, (isValid, errorMessage) -> {
        if (!isValid) {
            toast(errorMessage);
            btnSubmitRequest.setEnabled(true);
            return;
        }

        final String emailKey = invitorEmail;
        lookupFacultyByEmail(emailKey,
                (facultyUid, facultyEmail) -> {
                    createVisitorEntry(visitorName, invitorName, visitorPhoneNumber, visitorCnic, visitReason,
                            visitorType, visitDate, visitTime, facultyUid, facultyEmail, emailKey);
                    btnSubmitRequest.setEnabled(true); // Re-enable after finishing
                },
                () -> {
                    toast("No faculty account with this email exists");
                    btnSubmitRequest.setEnabled(true);
                });
    });
}

    private void clearForm() {
        editGuestName.setText("");
        editInvitorName.setText("");
        editInvitorEmail.setText("");
        editPhone.setText("");
        editCnic.setText("");
        editVisitReason.setText("");
        editVisitDate.setText("");
        spVisitorType.setSelection(0);
        spVisitTime.setSelection(0);
    }

    private String[] buildVisitTimeOptions() {
        List<String> values = new ArrayList<>();
        values.add("Select visit time");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 20);
        end.set(Calendar.MINUTE, 0);

        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
        while (!calendar.after(end)) {
            values.add(formatter.format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 15);
        }
        return values.toArray(new String[0]);
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

    private void toast(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
