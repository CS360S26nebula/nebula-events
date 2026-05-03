package com.example.seproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/**
 * Faculty dashboard with navigation, submit-request overlay, and request list shortcuts.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class FacultyHomeActivity extends AppCompatActivity {

    private ImageView navHomeIcon, navDownloadIcon, navPassesIcon, navProfileIcon;
    private TextView  navHomeText, navDownloadText, navPassesText, navProfileText,tvPendingCount,tvRejectedCount, tvApprovedCount, tvadhocRequests;
    private FrameLayout facultyFragmentOverlay;
    private LinearLayout recentActivityContainer;
    private TextView tvRecentEmpty;
    private android.widget.EditText searchBox;
    private List<Request> allRecentItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_home);

        facultyFragmentOverlay = findViewById(R.id.faculty_fragment_overlay);
        FragmentManager fm = getSupportFragmentManager();
        OnBackPressedCallback submitScreenBack = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                fm.popBackStack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, submitScreenBack);
        fm.addOnBackStackChangedListener(() -> {
            int count = fm.getBackStackEntryCount();
            submitScreenBack.setEnabled(count > 0);
            facultyFragmentOverlay.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        });

        navHomeIcon     = findViewById(R.id.navHomeIcon);
        navDownloadIcon = findViewById(R.id.navDownloadIcon);
        navPassesIcon   = findViewById(R.id.navPassesIcon);
        navProfileIcon  = findViewById(R.id.navProfileIcon);

        navHomeText     = findViewById(R.id.navHomeText);
        navDownloadText = findViewById(R.id.navDownloadText);
        navPassesText   = findViewById(R.id.navPassesText);
        navProfileText  = findViewById(R.id.navProfileText);

        tvadhocRequests = findViewById(R.id.viewAdHocBtn);
        tvPendingCount = findViewById(R.id.tv_pending_count);
        tvApprovedCount = findViewById(R.id.tv_approved_count);
        tvRejectedCount = findViewById(R.id.tv_rejected_count);
        LinearLayout navHome          = findViewById(R.id.navHome);
        LinearLayout navDownload      = findViewById(R.id.navDownload);
        LinearLayout navSubmitRequest = findViewById(R.id.navSubmitRequest);
        LinearLayout navPasses        = findViewById(R.id.navPasses);
        LinearLayout navProfile       = findViewById(R.id.navProfile);
        View approvedBox = findViewById(R.id.approvedBox);
        View rejectedBox = findViewById(R.id.rejectedBox);
        View pendingBox = findViewById(R.id.pendingBox);

        tvadhocRequests.setOnClickListener((v ->{
                Intent intent = new Intent(FacultyHomeActivity.this, RequestListActivity.class);
                intent.putExtra(RequestListActivity.EXTRA_STATUS, "Pending");
                intent.putExtra(RequestListActivity.EXTRA_IS_ADHOC, true);
                intent.putExtra(RequestListActivity.EXTRA_TITLE, getString(R.string.adhoc_requests_title));
                intent.putExtra(RequestListActivity.EXTRA_SUBTITLE, "View Adhoc Requests From your guests");
                intent.putExtra(RequestListActivity.EXTRA_ROLE, "Faculty");
                startActivity(intent);
        }));
        pendingBox.setOnClickListener(v -> {
            Intent intent = new Intent(FacultyHomeActivity.this, RequestListActivity.class);
            intent.putExtra(RequestListActivity.EXTRA_STATUS, "Pending");
            intent.putExtra(RequestListActivity.EXTRA_TITLE, getString(R.string.pending_requests_title));
            intent.putExtra(RequestListActivity.EXTRA_SUBTITLE, "View Pending Requests");
            intent.putExtra(RequestListActivity.EXTRA_ROLE, "Faculty");
            startActivity(intent);
        });
        approvedBox.setOnClickListener(v -> {
            Intent intent = new Intent(FacultyHomeActivity.this, RequestListActivity.class);
            intent.putExtra(RequestListActivity.EXTRA_STATUS, "Approved");
            intent.putExtra(RequestListActivity.EXTRA_TITLE, "Approved Requests");
            intent.putExtra(RequestListActivity.EXTRA_SUBTITLE, "View Approved Requests");
            intent.putExtra(RequestListActivity.EXTRA_ROLE, "Faculty");
            startActivity(intent);
        });

        rejectedBox.setOnClickListener(v -> {
            Intent intent = new Intent(FacultyHomeActivity.this, RequestListActivity.class);
            intent.putExtra(RequestListActivity.EXTRA_STATUS, "Rejected");
            intent.putExtra(RequestListActivity.EXTRA_TITLE, getString(R.string.rejected_requests_title));
            intent.putExtra(RequestListActivity.EXTRA_SUBTITLE, getString(R.string.rejected_requests_subtitle));
            intent.putExtra(RequestListActivity.EXTRA_ROLE, "Faculty");
            startActivity(intent);
        });

        navSubmitRequest.setOnClickListener(v -> {
            activateTab(2);
            fm.beginTransaction()
                    .replace(R.id.faculty_fragment_overlay, new FacultySubmitVisitorRequest())
                    .addToBackStack(null)
                    .commit();
        });

        navDownload.setOnClickListener(v->
        {
            activateTab((1));
            startActivity(new Intent(FacultyHomeActivity.this, FacultyDownloadActivity.class));
        });

        navPasses.setOnClickListener(v        -> activateTab(3));
        navProfile.setOnClickListener(v -> {
            activateTab(4);
            startActivity(new Intent(FacultyHomeActivity.this, ProfileActivity.class));
        });

        activateTab(0);
        int pending = 0;
        int approved = 0;
        int rejected = 0;

        recentActivityContainer = findViewById(R.id.recent_activity_container);
        tvRecentEmpty = findViewById(R.id.tv_recent_empty);
        searchBox = findViewById(R.id.searchBox);

        searchBox.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecentActivity(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        refreshCounts();
        loadRecentActivity();
    }
    /**
     * Resets the bottom navigation highlight and reloads count of all requests when the admin returns to this dashboard screen.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Reset to dashboard tab when coming back from ProfileActivity.
        activateTab(0);
        refreshCounts();
    }
    /**
     * Queries the database to count number of pending, approved, rejected, PreApproved and blacklisted requests to update UI display.
     */
    private void refreshCounts() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance().collection("requests")
                .whereEqualTo("requesterUid", uid)
                .get()
                .addOnSuccessListener(snapshots -> {
                    int pending = 0, approved = 0, rejected = 0;
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Request r = doc.toObject(Request.class);
                        String status = r.getRequestStatus();
                        if ("Pending".equals(status) && !r.getIsAdhoc()) pending++;
                        else if ("Approved".equals(status)) approved++;
                        else if ("Rejected".equals(status)) rejected++;
                    }
                    tvPendingCount.setText(String.valueOf(pending));
                    tvApprovedCount.setText(String.valueOf(approved));
                    tvRejectedCount.setText(String.valueOf(rejected));
                });
    }

    private void loadRecentActivity() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance().collection("requests")
                .whereEqualTo("requesterUid", uid)
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<Request> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Request r = doc.toObject(Request.class);
                        if (r != null && "Approved".equals(r.getRequestStatus())) {
                            list.add(r);
                        }
                    }
                    // Sort by time (checked-in or created)
                    list.sort((a, b) -> {
                        long tA = a.getCheckedInAtMillis() > 0 ? a.getCheckedInAtMillis() : a.getCreatedAtMillis();
                        long tB = b.getCheckedInAtMillis() > 0 ? b.getCheckedInAtMillis() : b.getCreatedAtMillis();
                        return Long.compare(tB, tA);
                    });

                    if (list.size() > 10) {
                        list = list.subList(0, 10);
                    }

                    allRecentItems = new ArrayList<>(list);
                    renderRecentActivity(allRecentItems);
                    tvRecentEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void filterRecentActivity(String query) {
        if (query == null || query.trim().isEmpty()) {
            renderRecentActivity(allRecentItems);
            tvRecentEmpty.setVisibility(allRecentItems.isEmpty() ? View.VISIBLE : View.GONE);
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<Request> filtered = new ArrayList<>();
        for (Request r : allRecentItems) {
            boolean match = (r.getPassId() != null && r.getPassId().toLowerCase().contains(lowerQuery))
                    || (r.getVisitorName() != null && r.getVisitorName().toLowerCase().contains(lowerQuery));
            if (match) {
                filtered.add(r);
            }
        }
        renderRecentActivity(filtered);
        tvRecentEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void renderRecentActivity(List<Request> items) {
        recentActivityContainer.removeAllViews();
        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
        for (Request r : items) {
            View card = inflater.inflate(R.layout.item_recent_activity_card, recentActivityContainer, false);
            TextView tvIdValue = card.findViewById(R.id.tv_id_value);
            TextView tvStatusBadge = card.findViewById(R.id.tv_status_badge);
            TextView tvDateValue = card.findViewById(R.id.tv_date_value);
            TextView tvNameValue = card.findViewById(R.id.tv_name_value);
            com.google.android.material.button.MaterialButton btnAction = card.findViewById(R.id.btn_action);

            tvIdValue.setText(r.getPassId() == null ? "-" : r.getPassId());
            tvStatusBadge.setText("Approved");
            tvStatusBadge.setBackgroundResource(R.drawable.bage_active_visitor);
            
            long millis = r.getCheckedInAtMillis() > 0 ? r.getCheckedInAtMillis() : r.getCreatedAtMillis();
            tvDateValue.setText(formatTime(millis));
            tvNameValue.setText(r.getVisitorName() == null ? "-" : r.getVisitorName());

            btnAction.setText("View Details");
            btnAction.setOnClickListener(v -> {
                // For now, just show a message or navigate if there's a details screen
                android.widget.Toast.makeText(this, "Details for: " + r.getVisitorName(), android.widget.Toast.LENGTH_SHORT).show();
            });
            
            recentActivityContainer.addView(card);
        }
    }

    private String formatTime(long millis) {
        if (millis <= 0) return "-";
        return new java.text.SimpleDateFormat("dd MMM yyyy, h:mm a", java.util.Locale.ENGLISH)
                .format(new java.util.Date(millis));
    }
    private void activateTab(int tab) {
        int[] outlineIcons = {
            R.drawable.home,
            R.drawable.ic_download,
            R.drawable.file,
            R.drawable.ic_profile
        };
        int[] filledIcons = {
            R.drawable.home_filled,
            R.drawable.ic_download_filled,
            R.drawable.file_filled,
            R.drawable.ic_profile_filled
        };

        ImageView[] icons = { navHomeIcon, navDownloadIcon, navPassesIcon, navProfileIcon };
        TextView[]  texts = { navHomeText, navDownloadText, navPassesText, navProfileText };

        int iconIndex = (tab < 2) ? tab : (tab > 2) ? tab - 1 : -1;

        for (int i = 0; i < icons.length; i++) {
            boolean isActive = (i == iconIndex);
            icons[i].setImageResource(isActive ? filledIcons[i] : outlineIcons[i]);
            icons[i].setColorFilter(
                new PorterDuffColorFilter(isActive ? ContextCompat.getColor(this, R.color.bottom_nav_active) : ContextCompat.getColor(this, R.color.bottom_nav_inactive),
                    PorterDuff.Mode.SRC_IN));
            texts[i].setTextColor(isActive ? ContextCompat.getColor(this, R.color.bottom_nav_active) : ContextCompat.getColor(this, R.color.bottom_nav_inactive));
        }
    }
}
