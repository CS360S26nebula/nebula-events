package com.example.seproject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that displays and manages active emergency entries, allowing guards to end emergencies.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class ActiveEmergencyEntriesActivity extends AppCompatActivity implements ActiveEmergencyEntriesAdapter.ActionListener {

    private ListenerRegistration registration;
    private TextView tvEmpty;
    private ActiveEmergencyEntriesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_emergency_entries);

        findViewById(R.id.btn_back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        tvEmpty = findViewById(R.id.tv_empty_state);
        RecyclerView rv = findViewById(R.id.rv_emergency_entries);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActiveEmergencyEntriesAdapter(this);
        rv.setAdapter(adapter);

        attachDatabaseListener();
    }

    /**
     * Attach a Firestore realtime listener to the "activeEmergencies" collection and
     * update the adapter when documents change.
     */
    private void attachDatabaseListener() {
        registration = FirebaseFirestore.getInstance()
                .collection("activeEmergencies")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Failed to load active emergencies.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (snapshots == null) {
                        return;
                    }

                    List<ActiveEmergencyEntry> list = new ArrayList<>();
                    List<String> ids = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        ActiveEmergencyEntry entry = doc.toObject(ActiveEmergencyEntry.class);
                        if (entry == null) {
                            continue;
                        }
                        list.add(entry);
                        ids.add(doc.getId());
                    }
                    adapter.setItems(list, ids);
                    tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    /**
     * Called when the user requests to end an active emergency. Removes the active entry
     * document and marks the corresponding emergency log as inactive.
     *
     * @param entry      the active emergency entry being ended
     * @param documentId Firestore document id of the active emergency
     */
    @Override
    public void onEndEmergency(@NonNull ActiveEmergencyEntry entry, @NonNull String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("activeEmergencies")
                .document(documentId)
                .delete()
                .addOnSuccessListener(unused -> {
                    db.collection("emergencyLogs")
                            .document(documentId)
                            .update("isActive", false, "endedAtMillis", System.currentTimeMillis());

                    db.collection("activeEmergencies")
                            .get()
                            .addOnSuccessListener(result -> {
                                if (result.isEmpty()) {
                                    Toast.makeText(this, "Emergency ended. Gate closed.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Emergency ended. Other emergencies are still active.", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to end emergency.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Lifecycle cleanup to remove the Firestore listener when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registration != null) {
            registration.remove();
        }
    }
}
