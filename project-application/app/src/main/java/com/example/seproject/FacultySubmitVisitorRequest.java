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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

/**
 * Faculty form to submit a visitor request to Firestore {@code requests} collection.
 *
 * <p>Happy path: required fields and spinner selections pass
 * {@link FacultyVisitorRequestValidator}; a new {@link Request} map is written with status Pending.
 * Invalid input shows toasts and does not write.</p>
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class FacultySubmitVisitorRequest extends Fragment {
    private EditText editGuestName;
    private EditText editInvitorName;
    private EditText editPhone;
    private EditText editCnic;
    private EditText editVisitDate;
    private Spinner spVisitTime;
    private Spinner spVisitorType;
    private EditText editVisitReason;
    private MaterialButton btnSubmitRequest;
    private FirebaseFirestore firestore;

    public FacultySubmitVisitorRequest() {
        super(R.layout.submit_visitor_request_faculty_fragment);
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
                (view, year, month, dayOfMonth) -> {
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

    private boolean validateInput(String visitorName, String invitorName, String visitorPhoneNumber,
                                  String visitorCnic, String visitReason, String visitorType,
                                  String visitDate, String visitTime) {
        if (!FacultyVisitorRequestValidator.hasRequiredFields(visitorName, invitorName,
                visitorPhoneNumber, visitorCnic, visitReason, visitDate)) {
            toast("Please fill all required fields.");
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

    private void createVisitorRequest(String visitorName, String invitorName, String visitorPhoneNumber,
                                      String visitorCnic, String visitReason, String visitorType,
                                      String visitDate, String visitTime) {
        String requestId = "ID-" + System.currentTimeMillis();
        String passId = "";
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
                "Pending",
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
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            requestMap.put("requesterUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            requestMap.put("requesterEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }

        firestore.collection("requests").document(requestId).set(requestMap)
                .addOnSuccessListener(unused -> {
                    toast("Visitor request submitted.");
                    clearForm();
                })
                .addOnFailureListener(error -> {
                    String message = error.getMessage();
                    toast(message == null ? "Failed to submit request." : message);
                });
    }

    private void submitRequest() {
        String visitorName = editGuestName.getText().toString().trim();
        String invitorName = editInvitorName.getText().toString().trim();
        String visitorPhoneNumber = editPhone.getText().toString().trim();
        String visitorCnic = editCnic.getText().toString().trim();
        String visitReason = editVisitReason.getText().toString().trim();
        String visitorType = selectedValue(spVisitorType);
        String visitDate = editVisitDate.getText().toString().trim();
        String visitTime = selectedValue(spVisitTime);
        if (!validateInput(visitorName, invitorName, visitorPhoneNumber, visitorCnic, visitReason, visitorType, visitDate, visitTime)) {
            return;
        }
        createVisitorRequest(visitorName, invitorName, visitorPhoneNumber, visitorCnic, visitReason, visitorType, visitDate, visitTime);
    }

    private void clearForm() {
        editGuestName.setText("");
        editInvitorName.setText("");
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
