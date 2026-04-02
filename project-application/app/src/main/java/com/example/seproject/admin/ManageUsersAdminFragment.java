package com.example.seproject.admin;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.seproject.R;

/**
 * Entry point to add or remove users (child fragments).
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class ManageUsersAdminFragment extends Fragment {

    public ManageUsersAdminFragment() {
        super(R.layout.manage_users_admin_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
        view.findViewById(R.id.card_add_user).setOnClickListener(v ->
                ((AdminMenuHostActivity) requireActivity()).navigateTo(new AddUserAdminFragment(), true));
        view.findViewById(R.id.card_remove_user).setOnClickListener(v ->
                ((AdminMenuHostActivity) requireActivity()).navigateTo(new RemoveUserAdminFragment(), true));
        view.findViewById(R.id.card_black_list_user).setOnClickListener(v ->
                ((AdminMenuHostActivity) requireActivity()).navigateTo(new BlackListUserAdminFragment(), true));
    }
}
