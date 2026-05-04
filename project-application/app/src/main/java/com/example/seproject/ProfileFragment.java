package com.example.seproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private TextView tvProfileName;
    private TextView tvAssignedId;
    private TextView tvShiftDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView darkToggle = view.findViewById(R.id.toggle_dark_mode);
        View backButton = view.findViewById(R.id.btn_back);
        View callAdminButton = view.findViewById(R.id.btn_call_admin);
        View logoutButton = view.findViewById(R.id.btn_logout);
        tvProfileName = view.findViewById(R.id.rl7asvxhvmst);
        tvAssignedId = view.findViewById(R.id.rm2ll5tptppq);
        tvShiftDetails = view.findViewById(R.id.r61hqqp5ho2);

        bindCurrentUserDetails();

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


        if (darkToggle != null) {
            // Check current night mode state to set initial toggle image
            int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                darkToggle.setTag("on");
                darkToggle.setImageResource(R.drawable.toggleright);
            } else {
                darkToggle.setTag("off");
                darkToggle.setImageResource(R.drawable.toggleleft);
            }

            darkToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView iv = (ImageView) v;
                    Object tag = iv.getTag();
                    if (tag != null && tag.equals("on")) {
                        iv.setImageResource(R.drawable.toggleleft);
                        iv.setTag("off");
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else {
                        iv.setImageResource(R.drawable.toggleright);
                        iv.setTag("on");
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    requireActivity().recreate();
                }
            });
        }
    }

    private void bindCurrentUserDetails() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            setProfileFallbackValues();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(this::applyProfileDetails)
                .addOnFailureListener(e -> setProfileFallbackValues());
    }

    private void applyProfileDetails(DocumentSnapshot snapshot) {
        if (!snapshot.exists()) {
            setProfileFallbackValues();
            return;
        }

        User user = snapshot.toObject(User.class);
        String fullName = user != null ? user.getFullName() : null;
        String role = user != null ? user.getRole() : null;
        String assignedId = user != null ? user.getUserId() : null;
        if (assignedId == null || assignedId.trim().isEmpty()) {
            assignedId = snapshot.getString("userId");
        }
        assignedId = normalizeUserId(assignedId);

        if (tvProfileName != null) {
            tvProfileName.setText(valueOrDefault(fullName, "Unknown User"));
        }
        if (tvAssignedId != null) {
            tvAssignedId.setText(valueOrDefault(assignedId, "-"));
        }
        if (tvShiftDetails != null) {
            tvShiftDetails.setText(role == null || role.trim().isEmpty() ? "-" : role);
        }
    }

    private void setProfileFallbackValues() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (tvProfileName != null && user != null && user.getDisplayName() != null) {
            tvProfileName.setText(user.getDisplayName());
        } else if (tvProfileName != null) {
            tvProfileName.setText("Unknown User");
        }
        if (tvAssignedId != null) {
            tvAssignedId.setText("-");
        }
        if (tvShiftDetails != null) {
            tvShiftDetails.setText("-");
        }
    }

    private String valueOrDefault(String value, String fallback) {
        return (value == null || value.trim().isEmpty()) ? fallback : value;
    }

    private String normalizeUserId(String rawUserId) {
        if (rawUserId == null) {
            return null;
        }
        String cleaned = rawUserId.trim().toUpperCase();
        if (cleaned.startsWith("USR-") && cleaned.length() >= 12) {
            return cleaned.substring(0, 12);
        }
        String alnum = cleaned.replaceAll("[^A-Z0-9]", "");
        if (alnum.length() >= 8) {
            return "USR-" + alnum.substring(0, 8);
        }
        return null;
    }
}