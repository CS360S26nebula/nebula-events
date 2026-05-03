package com.example.seproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Blacklisted individuals list screen with search and remove support.
 *
 * @author Yahya Ali
 * @version 1.0
 */

public class BlacklistedIndividualsActivity extends AppCompatActivity
        implements BlacklistedIndividualsAdapter.ActionListener {

    private ListenerRegistration registration;
    private BlacklistedIndividualsAdapter adapter;
    private TextView tvEmpty;
    private EditText etSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        /**
         * Sets up the blacklisted individuals list screen, search box, back button,
         * RecyclerView, and database listener.
         *
         * @param savedInstanceState previous state if the activity is being recreated
         */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvSubtitle = findViewById(R.id.tv_subtitle);
        RecyclerView rv = findViewById(R.id.rv_requests);

        tvTitle.setText("Black-listed");
        tvSubtitle.setText("Search and manage black-listed individuals");

        findViewById(R.id.btn_back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        tvEmpty = findViewById(R.id.tv_empty_state);
        etSearch = findViewById(R.id.et_search);

        adapter = new BlacklistedIndividualsAdapter(new ArrayList<>(), new ArrayList<>(), this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                adapter.filter(text.toString());
                tvEmpty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable text) {
            }
        });

        attachDatabaseListener();
    }

    private void attachDatabaseListener() {
        /**
         * Loads active blacklist records from Firestore and displays them in the list.
         */

        registration = FirebaseFirestore.getInstance()
                .collection("blacklistedIndividuals")
                .whereEqualTo("isActive", true)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Failed to load blacklisted individuals.", Toast.LENGTH_SHORT).show();
                        tvEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    if (snapshots == null) {
                        return;
                    }

                    List<Map<String, Object>> items = new ArrayList<>();
                    List<String> documentIds = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : snapshots) {
                        items.add(doc.getData());
                        documentIds.add(doc.getId());
                    }

                    adapter.setItems(items, documentIds);
                    tvEmpty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                });
    }

    @Override
    public void onRemove(@NonNull String documentId) {
        /**
         * Marks a blacklist record as inactive while keeping it in Firestore as history.
         *
         * @param documentId Firestore document id of the blacklist record
         */

        FirebaseFirestore.getInstance()
                .collection("blacklistedIndividuals")
                .document(documentId)
                .update(
                        "isActive", false,
                        "removedAt", System.currentTimeMillis()
                )
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "CNIC removed from blacklist successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(error ->
                        Toast.makeText(this, "Could not remove CNIC from blacklist.", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        /**
         * Removes the Firestore listener when the activity is destroyed.
         */

        super.onDestroy();

        if (registration != null) {
            registration.remove();
        }
    }
}