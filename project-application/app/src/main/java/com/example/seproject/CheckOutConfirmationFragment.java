package com.example.seproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;

/**
 * Dialog to confirm visitor check-out and deliver the result via
 * {@link #REQUEST_KEY_CHECKOUT_CONFIRM} to the parent activity.
 *
 * <p>Happy path: valid {@link #ARG_DOCUMENT_ID} in arguments, user confirms check-out;
 * fragment posts a result bundle and dismisses. Missing or invalid document id dismisses
 * without result.</p>
 *
 * @author Abdullah Ahmad
 * @version 1.0
 */

public class CheckOutConfirmationFragment extends DialogFragment {
    public static final String REQUEST_KEY_CHECKOUT_CONFIRM = "request_key_checkout_confirm";
    public static final String ARG_DOCUMENT_ID    = "arg_checkout_document_id";
    public static final String ARG_VISITOR_NAME   = "arg_checkout_visitor_name";
    public static final String BUNDLE_DOCUMENT_ID = "bundle_checkout_document_id";

    /**
     * @param documentId Firestore document id to check out
     * @param visitorName Name of the visitor
     * @return configured dialog fragment
     */
    public static CheckOutConfirmationFragment newInstance(String documentId, String visitorName) {
        CheckOutConfirmationFragment f = new CheckOutConfirmationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DOCUMENT_ID, documentId);
        args.putString(ARG_VISITOR_NAME, visitorName);
        f.setArguments(args);
        return f;
    }

    /**
     * @param inflater LayoutInflater object for checkout
     * @param container Parent view for checkout
     * @param savedInstanceState State that was saved previously
     * @return inflated view for the dialog
     */

    @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.checkout_confirmation_fragment, container, false);
    }

    /**
     * Configures dialog window properties
     */

    @Override public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    /**
     * @param view Fragment root view
     * @param savedInstanceState State that was saved previously
     */

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        String documentId = getArguments().getString(ARG_DOCUMENT_ID);
        String visitorName = getArguments().getString(ARG_VISITOR_NAME);

        ((TextView) view.findViewById(R.id.tv_checkout_visitor_name)).setText(
                TextUtils.isEmpty(visitorName) ? "Unknown Visitor" : visitorName);

        View.OnClickListener close = v -> dismiss();
        view.findViewById(R.id.btn_close_checkout_dialog).setOnClickListener(close);
        view.findViewById(R.id.btn_cancel_checkout).setOnClickListener(close);

        view.findViewById(R.id.btn_confirm_checkout).setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString(BUNDLE_DOCUMENT_ID, documentId);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY_CHECKOUT_CONFIRM, result);
            dismiss();
        });
    }
}
