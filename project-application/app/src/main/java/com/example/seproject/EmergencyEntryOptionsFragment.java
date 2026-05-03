package com.example.seproject;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Full-screen emergency options UI with large grid actions.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class EmergencyEntryOptionsFragment extends Fragment {

    public static final String REQUEST_KEY_EMERGENCY_ACTION = "request_key_emergency_action";
    public static final String BUNDLE_EMERGENCY_TYPE = "bundle_emergency_type";
    public static final String BUNDLE_EMERGENCY_REASON = "bundle_emergency_reason";

    public EmergencyEntryOptionsFragment() {
        super(R.layout.fragment_emergency_entry_options);
    }

        /**
         * Default constructor binding the fragment to its layout resource.
         */

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        view.findViewById(R.id.card_ambulance).setOnClickListener(v ->
                dispatchEmergencyAction("Ambulance", ""));
        view.findViewById(R.id.card_police).setOnClickListener(v ->
                dispatchEmergencyAction("Police", ""));
        view.findViewById(R.id.card_fire).setOnClickListener(v ->
                dispatchEmergencyAction("Fire", ""));

        view.findViewById(R.id.card_other).setOnClickListener(v ->
                new EmergencyManualReasonDialogFragment()
                        .show(getParentFragmentManager(), "manualEmergencyReasonDialog"));

        getParentFragmentManager().setFragmentResultListener(EmergencyManualReasonDialogFragment.REQUEST_KEY,
                getViewLifecycleOwner(), (requestKey, bundle) -> {
                    String reason = bundle.getString(EmergencyManualReasonDialogFragment.BUNDLE_REASON, "");
                    dispatchEmergencyAction("Other / Manual Override", reason);
                });
    }

        /**
         * Handle emergency option selection and listen for manual-reason results when needed.
         *
         * @param view               fragment's root view
         * @param savedInstanceState optional saved state
         */

    private void dispatchEmergencyAction(@NonNull String emergencyType, @NonNull String reason) {
        Bundle result = new Bundle();
        result.putString(BUNDLE_EMERGENCY_TYPE, emergencyType);
        result.putString(BUNDLE_EMERGENCY_REASON, reason);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY_EMERGENCY_ACTION, result);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

        /**
         * Dispatch the chosen emergency type and reason back to the activity via FragmentResult API
         * and pop the fragment from the back stack.
         *
         * @param emergencyType emergency type label
         * @param reason        optional reason text
         */
}
