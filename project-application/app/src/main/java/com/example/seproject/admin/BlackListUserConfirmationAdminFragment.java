package com.example.seproject.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.seproject.R;

/**
 * Confirmation dialog for blacklist action.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class BlackListUserConfirmationAdminFragment extends DialogFragment {
    private static final String REQUEST_KEY_BLACKLIST_CONFIRM = "request_key_blacklist_confirm";
    private static final String BUNDLE_KEY_CONFIRMED = "bundle_key_confirmed";

    public BlackListUserConfirmationAdminFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.black_list_user_confirmation_admin_fragment, container, false);
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
        View.OnClickListener close = v -> dismiss();
        view.findViewById(R.id.btn_close_dialog).setOnClickListener(close);
        view.findViewById(R.id.btn_no).setOnClickListener(close);
        view.findViewById(R.id.btn_yes).setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putBoolean(BUNDLE_KEY_CONFIRMED, true);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY_BLACKLIST_CONFIRM, result);
            dismiss();
        });
    }
}
