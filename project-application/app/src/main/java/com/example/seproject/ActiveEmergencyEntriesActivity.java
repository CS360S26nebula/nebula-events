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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registration != null) {
            registration.remove();
        }
    }
}
