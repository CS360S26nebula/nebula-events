package com.example.seproject.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.seproject.R;

/**
 * Root admin menu tiles (manage users, blacklist, restrict entry).
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class AdminMenuFragment extends Fragment {

    public AdminMenuFragment() {
        super(R.layout.admin_menu_fragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_back).setOnClickListener(v -> requireActivity().finish());
        view.findViewById(R.id.card_manage_users).setOnClickListener(v ->
                ((AdminMenuHostActivity) requireActivity()).navigateTo(new ManageUsersAdminFragment(), true));
        view.findViewById(R.id.card_restrict_visitor).setOnClickListener(v ->
                ((AdminMenuHostActivity) requireActivity()).navigateTo(new RestrictEntryAdminFragment(), true));
    }
}
