package com.example.seproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Dialog used to capture a required reason for emergency manual override.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class EmergencyManualReasonDialogFragment extends DialogFragment {

    public static final String REQUEST_KEY = "request_key_emergency_manual_reason";
    public static final String BUNDLE_REASON = "bundle_emergency_manual_reason";

    public EmergencyManualReasonDialogFragment() {
        super();
    }
    /**
     * Default constructor required for DialogFragment instantiation.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.emergency_manual_reason_popup_fragment, container, false);
    }

    /**
     * Inflate the dialog's view hierarchy.
     *
     * @param inflater           layout inflater
     * @param container          optional parent view
     * @param savedInstanceState optional saved state
     * @return the inflated view
     */

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }
    /**
     * Configure full-screen window parameters when the dialog becomes visible.
     */

    /**
     * Initialize view bindings and handle confirm/cancel interactions.
     * The confirm action validates that a non-empty reason is provided and
     * returns it to the parent via FragmentResult API.
     *
     * @param view               inflated view
     * @param savedInstanceState optional saved state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText reasonInput = view.findViewById(R.id.et_emergency_reason);
        View.OnClickListener dismissListener = v -> dismiss();
        view.findViewById(R.id.btn_close_dialog).setOnClickListener(dismissListener);
        view.findViewById(R.id.btn_cancel).setOnClickListener(dismissListener);

        view.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            String reason = reasonInput.getText() == null ? "" : reasonInput.getText().toString().trim();
            if (TextUtils.isEmpty(reason)) {
                Toast.makeText(requireContext(), "Emergency reason is required.", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle result = new Bundle();
            result.putString(BUNDLE_REASON, reason);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            dismiss();
        });
    }
}
