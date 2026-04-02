package com.example.seproject;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
    private Button loginButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        emailInput = findViewById(R.id.emailEditText);
        passwordInput = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);


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