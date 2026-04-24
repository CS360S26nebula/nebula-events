package com.example.seproject;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Guard home shell with navigation to scan, passes, profile, and request list shortcuts.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class GuardHomeActivity extends AppCompatActivity {

    private static final int COLOR_ACTIVE   = 0xFF27374D;
    private static final int COLOR_INACTIVE = 0xFF111111;

    private ImageView navHomeIcon, navScanIcon, navPassesIcon, navProfileIcon;
    private TextView  navHomeText, navScanText, navPassesText, navProfileText,tvPendingCount,tvRejectedCount, tvApprovedCount,tvPreApprovedCount,tvBlacklistedCount,tvEmergencyCount;
    private TextView tvRecentEmpty;
    private LinearLayout recentActivityContainer;

    /**
     * Sets up the guard dashboard screen, connects navigation views, and opens the correct request list when a dashboard status box is tapped.
     * This includes support for Pending, Pre-Approved, Approved, and Rejected request list navigation.
     *
     * @param savedInstanceState previous state if the activity is being recreated
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_home);

        final FrameLayout visitorEntryOverlay = findViewById(R.id.visitor_entry_overlay);
        FragmentManager fm = getSupportFragmentManager();
        OnBackPressedCallback visitorEntryBack = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                fm.popBackStack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, visitorEntryBack);
        fm.addOnBackStackChangedListener(() -> {
            int count = fm.getBackStackEntryCount();
            visitorEntryBack.setEnabled(count > 0);
            visitorEntryOverlay.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        });
        fm.setFragmentResultListener(EmergencyEntryOptionsFragment.REQUEST_KEY_EMERGENCY_ACTION, this,
                (requestKey, bundle) -> {
                    String emergencyType = bundle.getString(EmergencyEntryOptionsFragment.BUNDLE_EMERGENCY_TYPE, "");
                    String reason = bundle.getString(EmergencyEntryOptionsFragment.BUNDLE_EMERGENCY_REASON, "");
                    if (!emergencyType.isEmpty()) {
                        performEmergencyAction(emergencyType, reason);
                    }
                });
        fm.setFragmentResultListener(CheckOutConfirmationFragment.REQUEST_KEY_CHECKOUT_CONFIRM, this,
                (requestKey, bundle) -> {
                    String docId = bundle.getString(CheckOutConfirmationFragment.BUNDLE_DOCUMENT_ID);
                    if (docId != null && !docId.trim().isEmpty()) {
                        persistCheckOut(docId);
                    }
                });

        navHomeIcon = findViewById(R.id.navHomeIcon);
        navScanIcon = findViewById(R.id.navScanIcon);
        navPassesIcon = findViewById(R.id.navPassesIcon);
        navProfileIcon = findViewById(R.id.navProfileIcon);

        navHomeText = findViewById(R.id.navHomeText);
        navScanText = findViewById(R.id.navScanText);
        navPassesText = findViewById(R.id.navPassesText);
        navProfileText = findViewById(R.id.navProfileText);

        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navScan = findViewById(R.id.navScan);
        LinearLayout navCreateEntry = findViewById(R.id.navCreateEntry);
        LinearLayout navPasses = findViewById(R.id.navPasses);
        LinearLayout navProfile = findViewById(R.id.navProfile);
        View pendingBox = findViewById(R.id.pendingBox);
        View preapprovedBox = findViewById(R.id.preapprovedBox);
        View rejectedBox = findViewById(R.id.rejectedBox);
        View approvedBox = findViewById(R.id.approvedBox);
        View emergencyBox = findViewById(R.id.emergencyBox);

        pendingBox.setOnClickListener(v -> {
            Intent intent = new Intent(GuardHomeActivity.this, RequestListActivity.class);
            intent.putExtra(RequestListActivity.EXTRA_STATUS, "Pending");
            intent.putExtra(RequestListActivity.EXTRA_TITLE, getString(R.string.pending_requests_title));
            intent.putExtra(RequestListActivity.EXTRA_SUBTITLE, getString(R.string.pending_requests_subtitle));
            intent.putExtra(RequestListActivity.EXTRA_ROLE, "Guard");
            startActivity(intent);
        });

        approvedBox.setOnClickListener(v -> {
            Intent intent = new Intent(GuardHomeActivity.this, RequestListActivity.class);
            intent.putExtra(RequestListActivity.EXTRA_STATUS, "Approved");
            intent.putExtra(RequestListActivity.EXTRA_TITLE, "Approved Requests");
            intent.putExtra(RequestListActivity.EXTRA_SUBTITLE, "Manage your approved requests");
            intent.putExtra(RequestListActivity.EXTRA_ROLE, "Guard");
            startActivity(intent);
        });

        preapprovedBox.setOnClickListener(v -> {
            Intent intent = new Intent(GuardHomeActivity.this, RequestListActivity.class);
            intent.putExtra(RequestListActivity.EXTRA_STATUS, "Pre-Approved");
            intent.putExtra(RequestListActivity.EXTRA_TITLE, getString(R.string.preapproved_requests_title));
            intent.putExtra(RequestListActivity.EXTRA_SUBTITLE, getString(R.string.preapproved_requests_subtitle));
            intent.putExtra(RequestListActivity.EXTRA_ROLE, "Guard");
            startActivity(intent);
        });

        rejectedBox.setOnClickListener(v -> {
            Intent intent = new Intent(GuardHomeActivity.this, RequestListActivity.class);
            intent.putExtra(RequestListActivity.EXTRA_STATUS, "Rejected");
            intent.putExtra(RequestListActivity.EXTRA_TITLE, getString(R.string.rejected_requests_title));
            intent.putExtra(RequestListActivity.EXTRA_SUBTITLE, getString(R.string.rejected_requests_subtitle));
            intent.putExtra(RequestListActivity.EXTRA_ROLE, "Guard");
            startActivity(intent);
        });
        emergencyBox.setOnClickListener(v ->
                startActivity(new Intent(GuardHomeActivity.this, ActiveEmergencyEntriesActivity.class)));

        navHome.setOnClickListener(v -> activateTab(0));
        navScanIcon.setOnClickListener(v -> {
            activateTab(1);
            Intent intent = new Intent(GuardHomeActivity.this, VisitorScannerActivity.class);
            startActivity(intent);
        });
        navCreateEntry.setOnClickListener(v -> {
            activateTab(2);
            showCreateEntryBottomSheet();
        });
        navPasses.setOnClickListener(v -> activateTab(3));
        navProfile.setOnClickListener(v -> {
            activateTab(4);
            startActivity(new Intent(GuardHomeActivity.this, ProfileActivity.class));
        });

        activateTab(0);
        tvPendingCount = findViewById(R.id.tv_pending_count);
        tvApprovedCount = findViewById(R.id.tv_approved_count);
        tvRejectedCount = findViewById(R.id.tv_rejected_count);
        tvBlacklistedCount = findViewById(R.id.tv_blacklisted_count);
        tvPreApprovedCount = findViewById(R.id.tv_pre_approved_count);
        tvEmergencyCount = findViewById(R.id.tv_emergency_count);
        tvRecentEmpty = findViewById(R.id.tv_recent_empty);
        recentActivityContainer = findViewById(R.id.recent_activity_container);
//        int pending = 0;
//        int approved = 0;
//        int rejected = 0;

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
        refreshCounts();
        loadRecentActivity();
        activateTab(0);
    }
    /**
     * Queries the database to count number of pending, approved, rejected, PreApproved and blacklisted requests to update UI display.
     */
    private void refreshCounts() {

        FirebaseFirestore.getInstance().collection("requests")
                .get()
                .addOnSuccessListener(snapshots -> {
                    int pending = 0, approved = 0, rejected = 0, PreApproved =0, Blacklisted =0;
                    for (QueryDocumentSnapshot doc : snapshots) {

                        Request r = doc.toObject(Request.class);
                        String status = r.getRequestStatus();
                        if ("Pending".equals(status)) pending++;
                        else if ("Approved".equals(status)) approved++;
                        else if ("Rejected".equals(status)) rejected++;
                        else if ("Pre-Approved".equals(status)) PreApproved++;
                        if (doc.contains("isBlacklisted") && Boolean.TRUE.equals(doc.getBoolean("isBlacklisted"))) {
                            Blacklisted++;
                        }
                    }
                    tvPendingCount.setText(String.valueOf(pending));
                    tvApprovedCount.setText(String.valueOf(approved));
                    tvRejectedCount.setText(String.valueOf(rejected));
                    tvPreApprovedCount.setText(String.valueOf(PreApproved));
                    tvBlacklistedCount.setText(String.valueOf(Blacklisted));
                });
        FirebaseFirestore.getInstance().collection("activeEmergencies")
                .get()
                .addOnSuccessListener(snapshots -> tvEmergencyCount.setText(String.valueOf(snapshots.size())));
    }

    private void loadRecentActivity() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("requests")
                .whereEqualTo("requestStatus", "Approved")
                .get()
                .addOnSuccessListener(requestSnapshots ->
                        db.collection("activeEmergencies")
                                .get()
                                .addOnSuccessListener(emergencySnapshots -> {
                                    List<RecentActivityItem> merged = new ArrayList<>();

                                    for (QueryDocumentSnapshot doc : requestSnapshots) {
                                        Request request = doc.toObject(Request.class);
                                        if (request == null) {
                                            continue;
                                        }
                                        long eventTime = request.getCheckedInAtMillis() > 0
                                                ? request.getCheckedInAtMillis()
                                                : request.getCreatedAtMillis();
                                        merged.add(new RecentActivityItem(
                                                RecentActivityItem.TYPE_ACTIVE_VISITOR,
                                                doc.getId(),
                                                request.getPassId(),
                                                request.getVisitorName(),
                                                request.getVisitReason(),
                                                eventTime
                                        ));
                                    }

                                    for (QueryDocumentSnapshot doc : emergencySnapshots) {
                                        ActiveEmergencyEntry entry = doc.toObject(ActiveEmergencyEntry.class);
                                        if (entry == null) {
                                            continue;
                                        }
                                        merged.add(new RecentActivityItem(
                                                RecentActivityItem.TYPE_ACTIVE_EMERGENCY,
                                                doc.getId(),
                                                entry.getLogId(),
                                                entry.getEmergencyType(),
                                                entry.getReason(),
                                                entry.getTimestamp()
                                        ));
                                    }

                                    merged.sort(Comparator.comparingLong(RecentActivityItem::getEventTimeMillis).reversed());
                                    if (merged.size() > 10) {
                                        merged = new ArrayList<>(merged.subList(0, 10));
                                    }
                                    renderRecentActivity(merged);
                                    tvRecentEmpty.setVisibility(merged.isEmpty() ? View.VISIBLE : View.GONE);
                                }));
    }

    private void renderRecentActivity(@NonNull List<RecentActivityItem> items) {
        recentActivityContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (RecentActivityItem item : items) {
            View card = inflater.inflate(R.layout.item_recent_activity_card, recentActivityContainer, false);
            TextView tvIdLabel = card.findViewById(R.id.tv_id_label);
            TextView tvIdValue = card.findViewById(R.id.tv_id_value);
            TextView tvStatusBadge = card.findViewById(R.id.tv_status_badge);
            TextView tvDateValue = card.findViewById(R.id.tv_date_value);
            TextView tvNameLabel = card.findViewById(R.id.tv_name_label);
            TextView tvNameValue = card.findViewById(R.id.tv_name_value);
            com.google.android.material.button.MaterialButton btnAction = card.findViewById(R.id.btn_action);

            boolean visitor = item.getType() == RecentActivityItem.TYPE_ACTIVE_VISITOR;
            tvIdLabel.setText(visitor ? "Pass ID:" : "Emergency ID:");
            tvIdValue.setText(valueOrDash(item.getPrimaryId()));
            tvStatusBadge.setText(visitor ? "Approved" : "Emergency");
            tvStatusBadge.setBackgroundResource(visitor ? R.drawable.bage_active_visitor : R.drawable.badge_emergency);
            tvDateValue.setText(RequestDisplayFormatter.dashIfEmpty(formatTime(item.getEventTimeMillis())));
            tvNameLabel.setText(visitor ? "Guest Name" : "Emergency Type");
            tvNameValue.setText(valueOrDash(item.getTitleValue()));

            if (visitor) {
                btnAction.setText("Check Out");
                btnAction.setOnClickListener(v ->
                        CheckOutConfirmationFragment
                                .newInstance(item.getDocumentId(), valueOrDash(item.getTitleValue()))
                                .show(getSupportFragmentManager(), "checkout_confirm_dialog_from_home"));
            } else {
                btnAction.setText("End Emergency & Close Gate");
                btnAction.setOnClickListener(v -> endEmergency(item.getDocumentId()));
            }
            recentActivityContainer.addView(card);
        }
    }

    private String formatTime(long millis) {
        if (millis <= 0) {
            return "-";
        }
        return new java.text.SimpleDateFormat("dd MMM yyyy, h:mm a", java.util.Locale.ENGLISH)
                .format(new java.util.Date(millis));
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
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

        ImageView[] icons = {navHomeIcon, navScanIcon, navPassesIcon, navProfileIcon};
        TextView[] texts = {navHomeText, navScanText, navPassesText, navProfileText};

        int iconIndex = (tab < 2) ? tab : (tab > 2) ? tab - 1 : -1;

        for (int i = 0; i < icons.length; i++) {
            boolean isActive = (i == iconIndex);
            icons[i].setImageResource(isActive ? filledIcons[i] : outlineIcons[i]);
            icons[i].setColorFilter(
                    new PorterDuffColorFilter(isActive ? COLOR_ACTIVE : COLOR_INACTIVE, PorterDuff.Mode.SRC_IN)
            );
            texts[i].setTextColor(isActive ? COLOR_ACTIVE : COLOR_INACTIVE);
        }
    }

    private void showCreateEntryBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View content = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_create_entry_options, null);
        dialog.setContentView(content);

        content.findViewById(R.id.row_regular_entry).setOnClickListener(v -> {
            dialog.dismiss();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.visitor_entry_overlay, new CreateVisitorEntry())
                    .addToBackStack(null)
                    .commit();
        });

        content.findViewById(R.id.row_emergency_entry).setOnClickListener(v -> {
            dialog.dismiss();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.visitor_entry_overlay, new EmergencyEntryOptionsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        content.findViewById(R.id.row_ad_hoc_entry).setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(this, "add listener for row_ad_hoc_entry", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void performEmergencyAction(String emergencyType, String reason) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Unable to identify current guard.", Toast.LENGTH_SHORT).show();
            return;
        }

        EmergencyLog emergencyLog = reason == null || reason.trim().isEmpty()
                ? new EmergencyLog(currentUser.getUid(), emergencyType)
                : new EmergencyLog(currentUser.getUid(), emergencyType, reason.trim());

        emergencyLog.saveToDatabase(new EmergencyLog.SaveCallback() {
            @Override
            public void onSuccess() {
                emergencyLog.saveAsActiveEmergency(new EmergencyLog.SaveCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(GuardHomeActivity.this,
                                "Emergency Logged: " + emergencyType, Toast.LENGTH_SHORT).show();
                        refreshCounts();
                        loadRecentActivity();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(GuardHomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(GuardHomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
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

}