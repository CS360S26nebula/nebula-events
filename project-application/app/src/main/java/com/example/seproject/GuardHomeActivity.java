package com.example.seproject;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

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
    private TextView navHomeText, navScanText, navPassesText, navProfileText;

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

        navHome.setOnClickListener(v -> activateTab(0));
        navScan.setOnClickListener(v -> {
            activateTab(1);
            startActivity(new Intent(GuardHomeActivity.this, VisitorScannerActivity.class));
        });
        navCreateEntry.setOnClickListener(v -> activateTab(2));
        navPasses.setOnClickListener(v -> activateTab(3));
        navProfile.setOnClickListener(v -> {
            activateTab(4);
            startActivity(new Intent(GuardHomeActivity.this, ProfileActivity.class));
        });

        activateTab(0);
    }

    /**
     * Resets the bottom navigation highlight when the guard returns to this dashboard screen.
     */

    @Override
    protected void onResume() {
        super.onResume();
        // Reset to dashboard tab when coming back from ProfileActivity.
        activateTab(0);
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
}