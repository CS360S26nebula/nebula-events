package com.example.seproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.seproject.CreateVisitorEntry;
import com.example.seproject.R;

/**
 * Root admin menu tiles (manage users, user settings, gate timings, restrict entry,
 * create visitor, security audit).
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
                ((AdminMenuHostActivity) requireActivity()).navigateTo(
                        new ManageUsersAdminFragment(), true));

        view.findViewById(R.id.card_restrict_visitor).setOnClickListener(v ->
                ((AdminMenuHostActivity) requireActivity()).navigateTo(
                        new RestrictEntryAdminFragment(), true));

        view.findViewById(R.id.card_create_visitor_entry).setOnClickListener(v ->
                ((AdminMenuHostActivity) requireActivity()).navigateTo(
                        new CreateVisitorEntry(), true));

        // Umer: Added navigation for Gate Timings
        view.findViewById(R.id.card_gate_timings).setOnClickListener(v ->
                ((AdminMenuHostActivity) requireActivity()).navigateTo(
                        new AdminGateTimingsFragment(), true));
    }
}