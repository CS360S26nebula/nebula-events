package com.example.seproject;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.seproject.admin.AdminMenuHostActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Admin home shell with bottom navigation and shortcuts into request lists and admin tools.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class AdminHomeActivity extends AppCompatActivity {

    private ImageView navHomeIcon, navScanIcon, navPassesIcon, navProfileIcon;
    private TextView  navHomeText, navScanText, navPassesText, navProfileText, tvPendingCount,tvRejectedCount, tvApprovedCount,tvPreApprovedCount,tvBlacklistedCount,tvEmergencyCount,tvRecentEmpty;
    private LinearLayout recentActivityContainer;
    private final Map<String, String> userNameByUid = new HashMap<>();
    private final Map<String, String> userIdByUid = new HashMap<>();
    private EditText searchBox;
    private List<AdminRecentItem> allRecentItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Sets up the admin dashboard screen, connects navigation views, and opens the correct request list when a dashboard status is tapped.
         * This includes support for Pending, Pre-Approved, Approved, and Rejected request list navigation.
         *
         * @param savedInstanceState previous state if the activity is being recreated
         */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        navHomeIcon    = findViewById(R.id.navHomeIcon);
        navScanIcon    = findViewById(R.id.navScanIcon);
        navPassesIcon  = findViewById(R.id.navPassesIcon);
        navProfileIcon = findViewById(R.id.navProfileIcon);

        navHomeText    = findViewById(R.id.navHomeText);
        navScanText    = findViewById(R.id.navScanText);
        navPassesText  = findViewById(R.id.navPassesText);
        navProfileText = findViewById(R.id.navProfileText);

        LinearLayout navHome      = findViewById(R.id.navHome);
        LinearLayout navScan      = findViewById(R.id.navScan);
        LinearLayout navAdminMenu = findViewById(R.id.navAdminMenu);
        LinearLayout navPasses    = findViewById(R.id.navPasses);
        LinearLayout navProfile   = findViewById(R.id.navProfile);
        View approvedBox = findViewById(R.id.approvedBox);
        View rejectedBox = findViewById(R.id.rejectedBox);
        View preapprovedBox = findViewById(R.id.preapprovedBox);
        View blacklistedBox = findViewById(R.id.blacklistedBox);
        View pendingBox = findViewById(R.id.pendingBox);
        View emergencyBox = findViewById(R.id.emergencyBox);

        pendingBox.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, RequestListActivity.class);
            intent.putExtra(RequestListActivity.EXTRA_STATUS, "Pending");
            intent.putExtra(RequestListActivity.EXTRA_TITLE, getString(R.string.pending_requests_title));
            intent.putExtra(RequestListActivity.EXTRA_SUBTITLE, getString(R.string.pending_requests_subtitle));
            intent.putExtra(RequestListActivity.EXTRA_ROLE, "Admin");
            startActivity(intent);
        });

        approvedBox.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, RequestListActivity.class);
            intent.putExtra(RequestListActivity.EXTRA_STATUS, "Approved");
            intent.putExtra(RequestListActivity.EXTRA_TITLE, "Approved Requests");
            intent.putExtra(RequestListActivity.EXTRA_SUBTITLE, "Manage your approved requests");
            intent.putExtra(RequestListActivity.EXTRA_ROLE, "Admin");
            startActivity(intent);
        });

        preapprovedBox.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, RequestListActivity.class);
            intent.putExtra(RequestListActivity.EXTRA_STATUS, "Pre-Approved");
            intent.putExtra(RequestListActivity.EXTRA_TITLE, getString(R.string.preapproved_requests_title));
            intent.putExtra(RequestListActivity.EXTRA_SUBTITLE, getString(R.string.preapproved_requests_subtitle));
            intent.putExtra(RequestListActivity.EXTRA_ROLE, "Admin");
            startActivity(intent);
        });

        rejectedBox.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, RequestListActivity.class);
            intent.putExtra(RequestListActivity.EXTRA_STATUS, "Rejected");
            intent.putExtra(RequestListActivity.EXTRA_TITLE, getString(R.string.rejected_requests_title));
            intent.putExtra(RequestListActivity.EXTRA_SUBTITLE, getString(R.string.rejected_requests_subtitle));
            intent.putExtra(RequestListActivity.EXTRA_ROLE, "Admin");
            startActivity(intent);
        });

        blacklistedBox.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, BlacklistedIndividualsActivity.class);
            startActivity(intent);
        });

        emergencyBox.setOnClickListener(v ->
                startActivity(new Intent(AdminHomeActivity.this, ActiveEmergencyEntriesActivity.class)));

        navHome.setOnClickListener(v      -> activateTab(0));
        navScanIcon.setOnClickListener(v -> {
            activateTab(1);
            Intent intent = new Intent(AdminHomeActivity.this, VisitorScannerActivity.class);
            startActivity(intent);
        });
        navAdminMenu.setOnClickListener(v -> {
            activateTab(2);
            startActivity(new Intent(AdminHomeActivity.this, AdminMenuHostActivity.class));
        });
        navPasses.setOnClickListener(v    -> activateTab(3));
        navProfile.setOnClickListener(v -> {
            activateTab(4);
            startActivity(new Intent(AdminHomeActivity.this, ProfileActivity.class));
        });

        activateTab(0);
        tvPendingCount = findViewById(R.id.tv_pending_count);
        tvApprovedCount = findViewById(R.id.tv_approved_count);
        tvRejectedCount = findViewById(R.id.tv_rejected_count);
        tvBlacklistedCount = findViewById(R.id.tv_blacklisted_count);
        tvPreApprovedCount = findViewById(R.id.tv_preapproved_count);
        tvEmergencyCount = findViewById(R.id.tv_emergency_count);
        tvRecentEmpty = findViewById(R.id.tv_recent_empty);
        recentActivityContainer = findViewById(R.id.recent_activity_container);
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

        getSupportFragmentManager().setFragmentResultListener(
                CheckOutConfirmationFragment.REQUEST_KEY_CHECKOUT_CONFIRM,
                this,
                (requestKey, bundle) -> {
                    String docId = bundle.getString(CheckOutConfirmationFragment.BUNDLE_DOCUMENT_ID);
                    if (docId != null && !docId.trim().isEmpty()) {
                        persistCheckOut(docId);
                    }
                });

        refreshCounts();
        loadRecentActivity();
    }

    @Override
    protected void onResume() {
        /**
         * Resets the bottom navigation highlight and reloads count of all requests when the admin returns to this dashboard screen.
         */

        super.onResume();
        // Reset to dashboard tab when coming back from ProfileActivity.
        activateTab(0);
        refreshCounts();
        loadRecentActivity();
    }

    private void refreshCounts() {
        /**
         * Counts request statuses, active emergencies, and active blacklist records
         * to update the dashboard summary cards.
         */

        FirebaseFirestore.getInstance().collection("requests")
                .get()
                .addOnSuccessListener(snapshots -> {
                    int pending = 0, approved = 0, rejected = 0, PreApproved = 0;
                    for (QueryDocumentSnapshot doc : snapshots) {

                        Request r = doc.toObject(Request.class);
                        String status = r.getRequestStatus();
                        if ("Pending".equals(status)) pending++;
                        else if ("Approved".equals(status)) approved++;
                        else if ("Rejected".equals(status)) rejected++;
                        else if ("Pre-Approved".equals(status)) PreApproved++;
                    }
                    tvPendingCount.setText(String.valueOf(pending));
                    tvApprovedCount.setText(String.valueOf(approved));
                    tvRejectedCount.setText(String.valueOf(rejected));
                    tvPreApprovedCount.setText(String.valueOf(PreApproved));
                });
        FirebaseFirestore.getInstance().collection("activeEmergencies")
                .get()
                .addOnSuccessListener(snapshots -> tvEmergencyCount.setText(String.valueOf(snapshots.size())));

        FirebaseFirestore.getInstance().collection("blacklistedIndividuals")
                .whereEqualTo("isActive", true)
                .get()
                .addOnSuccessListener(snapshots ->
                        tvBlacklistedCount.setText(String.valueOf(snapshots.size())));
    }

    private void loadRecentActivity() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnSuccessListener(userSnapshots -> {
            userNameByUid.clear();
            userIdByUid.clear();
            for (QueryDocumentSnapshot userDoc : userSnapshots) {
                User user = userDoc.toObject(User.class);
                if (user == null) {
                    continue;
                }
                userNameByUid.put(userDoc.getId(), user.getFullName() == null ? "Unknown" : user.getFullName());
                userIdByUid.put(userDoc.getId(), user.getUserId() == null ? "-" : user.getUserId());
            }

            db.collection("requests")
                    .whereEqualTo("requestStatus", "Approved")
                    .get()
                    .addOnSuccessListener(requestSnapshots ->
                            db.collection("activeEmergencies")
                                    .get()
                                    .addOnSuccessListener(emergencySnapshots -> {
                                        List<AdminRecentItem> merged = new ArrayList<>();

                                        for (QueryDocumentSnapshot doc : requestSnapshots) {
                                            Request request = doc.toObject(Request.class);
                                            if (request == null) {
                                                continue;
                                            }
                                            long eventTime = request.getCheckedInAtMillis() > 0
                                                    ? request.getCheckedInAtMillis()
                                                    : request.getCreatedAtMillis();
                                            String actionByUid = doc.getString("approvedByUid");
                                            if (actionByUid == null || actionByUid.trim().isEmpty()) {
                                                actionByUid = doc.getString("createdByUid");
                                            }
                                            merged.add(AdminRecentItem.visitor(
                                                    doc.getId(),
                                                    request.getPassId(),
                                                    request.getVisitorName(),
                                                    eventTime,
                                                    actionByUid
                                            ));
                                        }

                                        for (QueryDocumentSnapshot doc : emergencySnapshots) {
                                            ActiveEmergencyEntry entry = doc.toObject(ActiveEmergencyEntry.class);
                                            if (entry == null) {
                                                continue;
                                            }
                                            merged.add(AdminRecentItem.emergency(
                                                    doc.getId(),
                                                    entry.getLogId(),
                                                    entry.getEmergencyType(),
                                                    entry.getTimestamp(),
                                                    entry.getGuardId()
                                            ));
                                        }

                                        merged.sort(Comparator.comparingLong(AdminRecentItem::getEventTimeMillis).reversed());
                                        if (merged.size() > 10) {
                                            merged = new ArrayList<>(merged.subList(0, 10));
                                        }
                                        allRecentItems = new ArrayList<>(merged);
                                        renderRecentActivity(allRecentItems);
                                        tvRecentEmpty.setVisibility(merged.isEmpty() ? View.VISIBLE : View.GONE);
                                    }));
        });
    }

    private void filterRecentActivity(String query) {
        if (query == null || query.trim().isEmpty()) {
            renderRecentActivity(allRecentItems);
            tvRecentEmpty.setVisibility(allRecentItems.isEmpty() ? View.VISIBLE : View.GONE);
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<AdminRecentItem> filtered = new ArrayList<>();
        for (AdminRecentItem item : allRecentItems) {
            boolean match = (item.getPrimaryId() != null && item.getPrimaryId().toLowerCase().contains(lowerQuery))
                    || (item.getDisplayValue() != null && item.getDisplayValue().toLowerCase().contains(lowerQuery));
            if (match) {
                filtered.add(item);
            }
        }
        renderRecentActivity(filtered);
        tvRecentEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void renderRecentActivity(@NonNull List<AdminRecentItem> items) {
        recentActivityContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (AdminRecentItem item : items) {
            View card = inflater.inflate(R.layout.item_recent_activity_card_admin, recentActivityContainer, false);
            TextView tvIdLabel = card.findViewById(R.id.tv_id_label);
            TextView tvIdValue = card.findViewById(R.id.tv_id_value);
            TextView tvStatusBadge = card.findViewById(R.id.tv_status_badge);
            TextView tvActionBy = card.findViewById(R.id.tv_action_by);
            TextView tvDateValue = card.findViewById(R.id.tv_date_value);
            TextView tvNameValue = card.findViewById(R.id.tv_name_value);
            com.google.android.material.button.MaterialButton btnAction = card.findViewById(R.id.btn_action);

            boolean visitor = item.getType() == 1;
            tvIdLabel.setText(visitor ? "Pass ID:" : "Emergency ID:");
            tvIdValue.setText(valueOrDash(item.getPrimaryId()));
            tvStatusBadge.setText(visitor ? "Approved" : "Emergency");
            tvStatusBadge.setBackgroundResource(visitor ? R.drawable.bage_active_visitor : R.drawable.badge_emergency);
            tvDateValue.setText(formatTime(item.getEventTimeMillis()));
            tvNameValue.setText(visitor
                    ? "Guest: " + valueOrDash(item.getDisplayValue())
                    : "Emergency: " + valueOrDash(item.getDisplayValue()));

            String actionByName = userNameByUid.get(item.getActionByUid());
            String actionById = userIdByUid.get(item.getActionByUid());
            tvActionBy.setText("Action By: " + valueOrDash(actionByName) + " (" + valueOrDash(actionById) + ")");

            if (visitor) {
                btnAction.setText("Check Out");
                btnAction.setOnClickListener(v ->
                        CheckOutConfirmationFragment
                                .newInstance(item.getDocumentId(), valueOrDash(item.getDisplayValue()))
                                .show(getSupportFragmentManager(), "checkout_confirm_dialog_admin_home"));
            } else {
                btnAction.setText("End Emergency & Close Gate");
                btnAction.setOnClickListener(v -> endEmergency(item.getDocumentId()));
            }
            recentActivityContainer.addView(card);
        }
    }

    private void persistCheckOut(String documentId) {
        FirebaseFirestore.getInstance()
                .collection("requests")
                .document(documentId)
                .update(
                        "requestStatus", "Expired",
                        "checkedOutAtMillis", System.currentTimeMillis()
                )
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, R.string.visitor_checked_out, Toast.LENGTH_SHORT).show();
                    loadRecentActivity();
                    refreshCounts();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, R.string.checkout_failed, Toast.LENGTH_SHORT).show());
    }

    private void endEmergency(String emergencyDocumentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("activeEmergencies")
                .document(emergencyDocumentId)
                .delete()
                .addOnSuccessListener(unused -> {
                    db.collection("emergencyLogs")
                            .document(emergencyDocumentId)
                            .update("isActive", false, "endedAtMillis", System.currentTimeMillis());
                    Toast.makeText(this, "Emergency ended. Gate closed.", Toast.LENGTH_SHORT).show();
                    refreshCounts();
                    loadRecentActivity();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to end emergency.", Toast.LENGTH_SHORT).show());
    }

    private String formatTime(long millis) {
        if (millis <= 0) {
            return "-";
        }
        return new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.ENGLISH).format(new Date(millis));
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private static class AdminRecentItem {
        private final int type; // 1 visitor, 2 emergency
        private final String documentId;
        private final String primaryId;
        private final String displayValue;
        private final long eventTimeMillis;
        private final String actionByUid;

        private AdminRecentItem(int type, String documentId, String primaryId, String displayValue,
                                long eventTimeMillis, String actionByUid) {
            this.type = type;
            this.documentId = documentId;
            this.primaryId = primaryId;
            this.displayValue = displayValue;
            this.eventTimeMillis = eventTimeMillis;
            this.actionByUid = actionByUid;
        }

        static AdminRecentItem visitor(String documentId, String passId, String visitorName,
                                       long eventTimeMillis, String actionByUid) {
            return new AdminRecentItem(1, documentId, passId, visitorName, eventTimeMillis, actionByUid);
        }

        static AdminRecentItem emergency(String documentId, String emergencyId, String emergencyType,
                                         long eventTimeMillis, String actionByUid) {
            return new AdminRecentItem(2, documentId, emergencyId, emergencyType, eventTimeMillis, actionByUid);
        }

        int getType() {
            return type;
        }

        String getDocumentId() {
            return documentId;
        }

        String getPrimaryId() {
            return primaryId;
        }

        String getDisplayValue() {
            return displayValue;
        }

        long getEventTimeMillis() {
            return eventTimeMillis;
        }

        String getActionByUid() {
            return actionByUid;
        }
    }

    /**
     * Updates the bottom navigation icons and text colors so the selected tab appears active.
     *
     * @param tab index of the tab to highlight
     */

    private void activateTab(int tab) {
        int[] outlineIcons = {
            R.drawable.home,
            R.drawable.qr_code_scanner,
            R.drawable.file,
            R.drawable.ic_profile
        };
        int[] filledIcons = {
            R.drawable.home_filled,
            R.drawable.qr_code_scanner,
            R.drawable.file_filled,
            R.drawable.ic_profile_filled
        };

        ImageView[] icons = { navHomeIcon, navScanIcon, navPassesIcon, navProfileIcon };
        TextView[]  texts = { navHomeText, navScanText, navPassesText, navProfileText };

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