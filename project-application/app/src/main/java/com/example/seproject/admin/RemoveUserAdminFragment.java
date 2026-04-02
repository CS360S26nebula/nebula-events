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
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Admin remove-user search and batch delete from Firestore.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class RemoveUserAdminFragment extends Fragment {
    private static final String REQUEST_KEY_REMOVE_USER_CONFIRM = "request_key_remove_user_confirm";
    private static final String BUNDLE_KEY_CONFIRMED = "bundle_key_confirmed";

    private EditText editName;
    private EditText editCnic;
    private Spinner spRole;
    private MaterialButton btnRemoveUser;
    private FirebaseFirestore firestore;

    public RemoveUserAdminFragment() {
        super(R.layout.remove_user_admin_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
        firestore = FirebaseFirestore.getInstance();
        initViews(view);
        btnRemoveUser.setOnClickListener(v -> openConfirmationPopup());
        getChildFragmentManager().setFragmentResultListener(
                REQUEST_KEY_REMOVE_USER_CONFIRM,
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    if (bundle.getBoolean(BUNDLE_KEY_CONFIRMED, false)) {
                        submitRemoveUser();
                    }
                });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_admin,
                new String[]{"Select user role", "Admin", "Faculty", "Guard"}) {
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
        spRole.setAdapter(adapter);
    }

    private void initViews(@NonNull View view) {
        editName = view.findViewById(R.id.et_user_name);
        editCnic = view.findViewById(R.id.et_cnic);
        spRole = view.findViewById(R.id.sp_user_role);
        btnRemoveUser = view.findViewById(R.id.btn_remove);
    }

    private boolean validateInput(String fullName, String cnic, String role) {
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(cnic)) {
            toast("Please fill all required fields.");
            return false;
        }
        if ("Select user role".equals(role)) {
            toast("Please select a user role.");
            return false;
        }
        return true;
    }

    private void openConfirmationPopup() {
        String fullName = editName.getText().toString().trim();
        String cnic = editCnic.getText().toString().trim();
        String role = selectedValue(spRole);

        if (!validateInput(fullName, cnic, role)) {
            return;
        }

        RemoveUserConfirmationAdminFragment dialog = new RemoveUserConfirmationAdminFragment();
        dialog.show(getChildFragmentManager(), "remove_user_confirmation");
    }

    private void submitRemoveUser() {
        String fullName = editName.getText().toString().trim();
        String cnic = editCnic.getText().toString().trim();
        String role = selectedValue(spRole);
        if (!validateInput(fullName, cnic, role)) {
            return;
        }

        btnRemoveUser.setEnabled(false);
        btnRemoveUser.setText("Removing...");
        deleteUserByDetails(fullName, cnic, role);
    }

    private void deleteUserByDetails(String fullName, String cnic, String role) {
        firestore.collection("users")
                .whereEqualTo("cnicNumber", cnic)
                .whereEqualTo("role", role)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        setRemoveLoading(false);
                        toast("No matching user found.");
                        return;
                    }

                    WriteBatch batch = firestore.batch();
                    int docsToDelete = 0;
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String storedName = document.getString("fullName");
                        if (storedName != null && storedName.equalsIgnoreCase(fullName)) {
                            batch.delete(document.getReference());
                            docsToDelete++;
                        }
                    }

                    if (docsToDelete == 0) {
                        setRemoveLoading(false);
                        toast("No matching user found.");
                        return;
                    }

                    batch.commit()
                            .addOnSuccessListener(unused -> {
                                setRemoveLoading(false);
                                toast("User removed successfully.");
                            })
                            .addOnFailureListener(error -> {
                                setRemoveLoading(false);
                                String message = error.getMessage();
                                toast(message == null ? "Could not remove user." : message);
                            });
                })
                .addOnFailureListener(error -> {
                    setRemoveLoading(false);
                    String message = error.getMessage();
                    toast(message == null ? "Could not remove user." : message);
                });
    }

    private void setRemoveLoading(boolean loading) {
        btnRemoveUser.setEnabled(!loading);
        btnRemoveUser.setText(loading ? "Removing..." : "Remove");
    }

    private String selectedValue(Spinner spinner) {
        Object selected = spinner.getSelectedItem();
        return selected == null ? "" : selected.toString();
    }

    private void toast(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            if ("User removed successfully.".equals(message)) {
                clearInputs();
            }
        }
    }

    private void clearInputs() {
        editName.setText("");
        editCnic.setText("");
        spRole.setSelection(0);
    }

}
