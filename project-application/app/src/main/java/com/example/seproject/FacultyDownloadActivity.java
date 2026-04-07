package com.example.seproject;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * activity for Faculty to view and download approved visitor passes.
 * Reuses the layout frame from activity_request_list.
 *
 * @author Ali Azhar
 * @version 1.0
 */
public class FacultyDownloadActivity extends AppCompatActivity {

    private List<Request> list = new ArrayList<>();
    private DownloadAdapter adapter;
    private ListenerRegistration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);


        ((TextView) findViewById(R.id.tv_title)).setText("Download Passes");
        ((TextView) findViewById(R.id.tv_subtitle)).setText("Save your approved visitor QR codes");
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rv_requests);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DownloadAdapter();
        rv.setAdapter(adapter);

        attachDatabaseListener();
    }

    private void attachDatabaseListener() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        Query query = FirebaseFirestore.getInstance().collection("requests")
                .whereEqualTo("requesterUid", uid).whereEqualTo("requestStatus", "Pre-Approved");

        registration = query.addSnapshotListener((snapshots, e) -> {
            if (snapshots == null || e != null) return;

            list.clear();
            for (com.google.firebase.firestore.QueryDocumentSnapshot doc : snapshots) {
                Request r = doc.toObject(Request.class);
                if (r.getPassId() != null) {
                    list.add(r);
                }
            }

            findViewById(R.id.tv_empty_state).setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            adapter.notifyDataSetChanged();
        });
    }

    private class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.VH> {
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_download_request_card_faculty, p, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int p) {
            Request r = list.get(p);
            h.id.setText(r.getPassId());
            h.name.setText(r.getVisitorName());
            h.date.setText(r.getVisitDate());
            h.phoneNumber.setText((r.getVisitorMobileNumber()));
            h.purposeOfVisit.setText(r.getVisitReason());
            h.invitorType.setText((r.getInvitorType()));
            h.invitorName.setText(r.getInvitorName());
            h.cnic.setText(r.getVisitorCnic());
            h.btn.setOnClickListener(v -> downloadQR(r.getPassId(), r.getVisitorName()));
        }

        @Override public int getItemCount() { return list.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView id, name, date, cnic, phoneNumber, purposeOfVisit, invitorType, invitorName; View btn;
            VH(View v) { super(v);
                id = v.findViewById(R.id.tv_request_id);
                name = v.findViewById(R.id.tv_visitor_name);
                date = v.findViewById(R.id.tv_visit_date);
                btn = v.findViewById(R.id.btn_download_pass);
                cnic = v.findViewById(R.id.tv_visitor_cnic);
                phoneNumber = v.findViewById(R.id.tv_visitor_phone);
                purposeOfVisit = v.findViewById(R.id.tv_visit_reason);
                invitorType = v.findViewById(R.id.tv_invitor_type);
                invitorName = v.findViewById(R.id.tv_invitor_name);
            }
        }
    }

    private void downloadQR(String passId, String visitorName) {
        try {
            Bitmap bitmap = new BarcodeEncoder().encodeBitmap(passId, com.google.zxing.BarcodeFormat.QR_CODE, 512, 512);

            android.content.ContentValues cv = new android.content.ContentValues();
            cv.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "Pass_" + visitorName.replace(" ", "_"));
            cv.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png");
            cv.put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SafeCore");

            android.net.Uri uri = getContentResolver().insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
            if (uri != null) {
                try (java.io.OutputStream out = getContentResolver().openOutputStream(uri)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    Snackbar.make(findViewById(android.R.id.content), "Pass saved to Gallery", 2000)
                            .setBackgroundTint(Color.parseColor("#2E7D32")).show();
                }
            }
        } catch (Exception e) {
            Snackbar.make(findViewById(android.R.id.content), "Download failed", 2000).setBackgroundTint(Color.RED).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registration != null) registration.remove();
    }
}