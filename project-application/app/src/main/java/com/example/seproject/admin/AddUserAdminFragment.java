package com.example.seproject.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.example.seproject.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Admin flow to register a new user via secondary Firebase app and Firestore profile.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class AddUserAdminFragment extends Fragment {

    private static final String SECONDARY_APP_NAME = "admin_create_user_app";
    private static final String REQUEST_KEY_ADD_USER_CONFIRM = "request_key_add_user_confirm";
    private static final String BUNDLE_KEY_CONFIRMED = "bundle_key_confirmed";

    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private EditText editPhone;
    private EditText editCnic;
    private Spinner spRole;
    private Spinner spDate;
    private Spinner spMonth;
    private Spinner spYear;
    private MaterialButton btnAddUser;

    private FirebaseFirestore firestore;
    private FirebaseAuth createUserAuth;

    public AddUserAdminFragment() {
        super(R.layout.add_user_admin_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        firestore = FirebaseFirestore.getInstance();
        createUserAuth = buildSecondaryAuth();

        initViews(view);
        bindSpinner(spRole, new String[]{"Select user role", "Admin", "Faculty", "Guard"});
        bindSpinner(spDate, buildRange("Date", 1, 31));
        bindSpinner(spMonth, new String[]{"Month", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        bindSpinner(spYear, buildRange("Year", 1980, currentYear));

        btnAddUser.setOnClickListener(v -> openConfirmationPopup());
        getChildFragmentManager().setFragmentResultListener(
                REQUEST_KEY_ADD_USER_CONFIRM,
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    if (bundle.getBoolean(BUNDLE_KEY_CONFIRMED, false)) {
                        submitAddUser();
                    }
                });
    }

    private void initViews(@NonNull View view) {
        editName = view.findViewById(R.id.et_guest_name);
        editEmail = view.findViewById(R.id.et_email);
        editPassword = view.findViewById(R.id.et_password);
        editConfirmPassword = view.findViewById(R.id.et_confirm_password);
        editPhone = view.findViewById(R.id.et_phone);
        editCnic = view.findViewById(R.id.et_cnic);
        spRole = view.findViewById(R.id.sp_role);
        spDate = view.findViewById(R.id.sp_date);
        spMonth = view.findViewById(R.id.sp_month);
        spYear = view.findViewById(R.id.sp_year);
        btnAddUser = view.findViewById(R.id.btn_add_user);
    }

    private void openConfirmationPopup() {
        String fullName = editName.getText().toString().trim();
        String role = selectedValue(spRole);
        String date = selectedValue(spDate);
        String month = selectedValue(spMonth);
        String year = selectedValue(spYear);
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();
        String phone = editPhone.getText().toString().trim();
        String cnic = editCnic.getText().toString().trim();

        if (!validateInput(fullName, role, date, month, year, email, password, confirmPassword, phone, cnic)) {
            return;
        }

        AddUserConfirmationAdminFragment dialog = new AddUserConfirmationAdminFragment();
        dialog.show(getChildFragmentManager(), "add_user_confirmation");
    }

    private void submitAddUser() {
        String fullName = editName.getText().toString().trim();
        String role = selectedValue(spRole);
        String date = selectedValue(spDate);
        String month = selectedValue(spMonth);
        String year = selectedValue(spYear);
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();
        String phone = editPhone.getText().toString().trim();
        String cnic = editCnic.getText().toString().trim();

        if (!validateInput(fullName, role, date, month, year, email, password, confirmPassword, phone, cnic)) {
            return;
        }

        String dobString = date + " " + month + " " + year;
        setLoading(true);
        createAuthAndWriteUser(fullName, role, dobString, email, password, phone, cnic);
    }

    private boolean validateInput(String fullName, String role, String date, String month, String year,
                                  String email, String password, String confirmPassword,
                                  String phone, String cnic) {
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(cnic)) {
            toast("Please fill all required fields.");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Please enter a valid email.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            toast("Password and Confirm Password do not match.");
            return false;
        }
        if (password.length() < 6) {
            toast("Password must be at least 6 characters.");
            return false;
        }
        if ("Select user role".equals(role) || "Date".equals(date) || "Month".equals(month) || "Year".equals(year)) {
            toast("Please complete role and date of birth fields.");
            return false;
        }
        return true;
    }

    private void createAuthAndWriteUser(String fullName, String role, String dobString, String email,
                                        String password, String phone, String cnic) {
        createUserAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser createdUser = authResult.getUser();
                    if (createdUser == null) {
                        setLoading(false);
                        toast("User creation failed. Please try again.");
                        return;
                    }
                    writeUserDocument(createdUser.getUid(), fullName, role, dobString, email, phone, cnic);
                })
                .addOnFailureListener(error -> {
                    setLoading(false);
                    String message = error.getMessage();
                    toast(message == null ? "Could not create auth account." : message);
                });
    }

    private void writeUserDocument(String uid, String fullName, String role, String dobString, String email,
                                   String phone, String cnic) {
        User newUser = new User(fullName, role, dobString, email, phone, cnic);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", newUser.getFullName());
        userMap.put("role", newUser.getRole());
        userMap.put("dateOfBirth", newUser.getDateOfBirth());
        userMap.put("email", newUser.getEmail());
        userMap.put("phoneNumber", newUser.getPhoneNumber());
        userMap.put("cnicNumber", newUser.getCnicNumber());
        userMap.put("createdAt", com.google.firebase.Timestamp.now());

        firestore.collection("users").document(uid).set(userMap)
                .addOnSuccessListener(unused -> {
                    setLoading(false);
                    toast("User added successfully.");
                })
                .addOnFailureListener(error -> {
                    cleanupAuthUser(uid);
                    setLoading(false);
                    String message = error.getMessage();
                    toast(message == null ? "Could not save user profile." : message);
                });
    }

    private void cleanupAuthUser(String uid) {
        FirebaseUser created = createUserAuth.getCurrentUser();
        if (created != null && uid.equals(created.getUid())) {
            created.delete();
        }
    }

    private void setLoading(boolean loading) {
        btnAddUser.setEnabled(!loading);
        btnAddUser.setText(loading ? "Adding..." : "Add");
    }

    private String selectedValue(Spinner spinner) {
        Object selected = spinner.getSelectedItem();
        return selected == null ? "" : selected.toString();
    }

    private void toast(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private FirebaseAuth buildSecondaryAuth() {
        FirebaseApp secondaryApp;
        try {
            secondaryApp = FirebaseApp.getInstance(SECONDARY_APP_NAME);
        } catch (IllegalStateException e) {
            FirebaseOptions options = FirebaseApp.getInstance().getOptions();
            secondaryApp = FirebaseApp.initializeApp(requireContext(), options, SECONDARY_APP_NAME);
        }
        return FirebaseAuth.getInstance(secondaryApp);
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
        for (int i = start; i <= end; i++) {
            values[i - start + 1] = String.valueOf(i);
        }
        return values;
    }
}
