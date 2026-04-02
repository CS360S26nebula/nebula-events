package com.example.seproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Profile screen with toggles, call admin, and logout.
 *
 * <p>Happy path: user taps logout → Firebase sign-out and redirect to {@link LoginActivity}.
 * Toggle views switch tag/image between on/off states.</p>
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView notifToggle = view.findViewById(R.id.toggle_notifications);
        ImageView darkToggle = view.findViewById(R.id.toggle_dark_mode);
        View backButton = view.findViewById(R.id.btn_back);
        View callAdminButton = view.findViewById(R.id.btn_call_admin);
        View logoutButton = view.findViewById(R.id.btn_logout);

        if (backButton != null) {
            backButton.setOnClickListener(v -> requireActivity().finish());
        }

        if (callAdminButton != null) {
            callAdminButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+923102275000"));
                startActivity(intent);
            });
        }

        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(requireContext(), "Logged out successfully.", Toast.LENGTH_SHORT).show();
            });
        }

        if (notifToggle != null) {
            notifToggle.setTag("off");
            notifToggle.setImageResource(R.drawable.toggleleft);
            notifToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView iv = (ImageView) v;
                    Object tag = iv.getTag();
                    if (tag != null && tag.equals("on")) {
                        iv.setImageResource(R.drawable.toggleleft);
                        iv.setTag("off");
                    } else {
                        iv.setImageResource(R.drawable.toggleright);
                        iv.setTag("on");
                    }
                }
            });
        }

        if (darkToggle != null) {
            darkToggle.setTag("off");
            darkToggle.setImageResource(R.drawable.toggleleft);
            darkToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView iv = (ImageView) v;
                    Object tag = iv.getTag();
                    if (tag != null && tag.equals("on")) {
                        iv.setImageResource(R.drawable.toggleleft);
                        iv.setTag("off");
                    } else {
                        iv.setImageResource(R.drawable.toggleright);
                        iv.setTag("on");
                    }
                }
            });
        }
    }
}