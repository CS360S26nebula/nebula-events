package com.example.seproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

public class visitorScannerFragment extends Fragment {

    // UI Variables
    private ImageView btnBack;
    private EditText etVisitorCode;
    private AppCompatButton btnValidateCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.acitivty_scan_qr_guard, container, false);
        btnBack = view.findViewById(R.id.btn_back);
        etVisitorCode = view.findViewById(R.id.et_visitor_code);
        btnValidateCode = view.findViewById(R.id.btn_validate_code);

        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnValidateCode.setOnClickListener(v -> {
            String inputCode = etVisitorCode.getText().toString().trim();

            if (inputCode.isEmpty()) {
                etVisitorCode.setError("Please enter a visitor code");
                etVisitorCode.requestFocus();
            } else {
                validateVisitorCode(inputCode);
            }
        });

        return view;
    }
    private void validateVisitorCode(String code) {
        Toast.makeText(requireContext(), "Checking code: " + code, Toast.LENGTH_SHORT).show();
        etVisitorCode.setText("");
    }
}