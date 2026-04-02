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
 * Dialog to capture a rejection reason and deliver it via
 * {@link #REQUEST_KEY_REJECT_CONFIRM} to the parent activity.
 *
 * <p>Happy path: valid {@link #ARG_DOCUMENT_ID} in arguments, user enters reason and confirms;
 * fragment posts a result bundle and dismisses. Missing or empty document id dismisses without
 * result.</p>
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class RejectRequestConfirmationFragment extends DialogFragment {

    public static final String REQUEST_KEY_REJECT_CONFIRM = "request_key_reject_confirm";
    public static final String ARG_DOCUMENT_ID = "arg_document_id";
    public static final String BUNDLE_DOCUMENT_ID = "bundle_document_id";
    public static final String BUNDLE_REJECTION_REASON = "bundle_rejection_reason";

    public RejectRequestConfirmationFragment() {
    }

    /**
     * @param documentId Firestore document id to reject; must not be null
     * @return configured dialog fragment
     */
    @NonNull
    public static RejectRequestConfirmationFragment newInstance(@NonNull String documentId) {
        RejectRequestConfirmationFragment f = new RejectRequestConfirmationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DOCUMENT_ID, documentId);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reject_user_confirmation_popup_fragment, container, false);
    }

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) {
            dismiss();
            return;
        }
        String documentId = args.getString(ARG_DOCUMENT_ID);
        if (TextUtils.isEmpty(documentId)) {
            dismiss();
            return;
        }

        EditText etReason = view.findViewById(R.id.tv_dialog_message);

        View.OnClickListener close = v -> dismiss();
        view.findViewById(R.id.btn_close_dialog).setOnClickListener(close);
        view.findViewById(R.id.btn_cancel).setOnClickListener(close);

        view.findViewById(R.id.btn_reject).setOnClickListener(v -> {
            String reason = etReason.getText() != null ? etReason.getText().toString().trim() : "";
            if (TextUtils.isEmpty(reason)) {
                Toast.makeText(requireContext(), R.string.rejection_reason_required, Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle result = new Bundle();
            result.putString(BUNDLE_DOCUMENT_ID, documentId);
            result.putString(BUNDLE_REJECTION_REASON, reason);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY_REJECT_CONFIRM, result);
            dismiss();
        });
    }
}
