package com.example.seproject;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Email/password sign-in and role-based redirect after Firestore profile load.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private ImageView passwordToggle;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        emailInput = findViewById(R.id.emailEditText);
        passwordInput = findViewById(R.id.passwordEditText);
        passwordToggle = findViewById(R.id.passwordToggle);
        loginButton = findViewById(R.id.loginButton);
        setupPasswordToggle();


        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }


            loginUser(email, password);
        });
    }

    /**
     * Set up the password visibility toggle button. Toggles between visible and masked
     * password input and updates the toggle icon accordingly.
     */
    private void setupPasswordToggle() {
        isPasswordVisible = false;
        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordToggle.setImageResource(R.drawable.ic_visibility_off);
        passwordToggle.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordToggle.setImageResource(R.drawable.ic_visibility_on);
            } else {
                passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordToggle.setImageResource(R.drawable.ic_visibility_off);
            }
            passwordInput.setSelection(passwordInput.getText().length());
        });
    }

    /**
     * Authenticate the user with Firebase Auth using email and password.
     * On success, the user's Firestore profile will be loaded and used to redirect
     * to the appropriate home screen.
     *
     * @param email    user's email address
     * @param password user's password
     */
    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        fetchUserRoleAndRedirect(userId);
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Load the Firestore user document for {@code userId} and redirect to a role-specific
     * home activity. Also checks blacklist status and clears expired blacklists.
     *
     * @param userId Firestore user document id (UID)
     */
    private void fetchUserRoleAndRedirect(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Toast.makeText(this, "User data not found in database.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isUserCurrentlyBlocked(documentSnapshot)) {
                        mAuth.signOut();
                        Toast.makeText(
                                this,
                                "Your account has been blocked by the admin. Contact admin support.",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }
                    clearExpiredBlacklistIfNeeded(userId, documentSnapshot);

                    User user = documentSnapshot.toObject(User.class);
                    if (user == null) {
                        Toast.makeText(this, "User data not found in database.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String role = user.getRole();
                    if ("Guard".equals(role)) {
                        startActivity(new Intent(LoginActivity.this, GuardHomeActivity.class));
                    } else if ("Admin".equals(role)) {
                        startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                    } else if ("Faculty".equals(role)) {
                        startActivity(new Intent(LoginActivity.this, FacultyHomeActivity.class));
                    }
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show());
    }

    /**
     * Check whether the Firestore snapshot represents a currently active blacklist period.
     *
     * @param snapshot user document snapshot
     * @return true if the user is currently blocked, false otherwise
     */
    private boolean isUserCurrentlyBlocked(DocumentSnapshot snapshot) {
        Boolean isBlacklisted = snapshot.getBoolean("isBlacklisted");
        Long startMillis = snapshot.getLong("blacklistStartTimeMiliseconds");
        Long endMillis = snapshot.getLong("blacklistEndTimeMilliseconds");
        if (isBlacklisted == null || !isBlacklisted || startMillis == null || endMillis == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if(now >= startMillis && now <= endMillis){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * If the user's blacklist has expired, clear blacklist-related fields in Firestore.
     * This method is a no-op when the user is not blacklisted or the expiry time is missing.
     *
     * @param userId   Firestore document id for the user
     * @param snapshot snapshot of the user document used to read blacklist fields
     */
    private void clearExpiredBlacklistIfNeeded(String userId, DocumentSnapshot snapshot) {
        Boolean isBlacklisted = snapshot.getBoolean("isBlacklisted");
        Long endMillis = snapshot.getLong("blacklistEndTimeMilliseconds");
        if (isBlacklisted == null || !isBlacklisted || endMillis == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now <= endMillis) {
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("isBlacklisted", false);
        updates.put("blacklistReason", FieldValue.delete());
        updates.put("blacklistedByAdmin", false);
        updates.put("blacklistStartTimeMiliseconds", FieldValue.delete());
        updates.put("blacklistEndTimeMilliseconds", FieldValue.delete());
        updates.put("blacklistUpdatedAt", now);
        db.collection("users").document(userId).update(updates);
    }
}