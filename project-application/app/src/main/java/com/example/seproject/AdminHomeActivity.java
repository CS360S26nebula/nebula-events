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
import com.example.seproject.admin.AdminMenuHostActivity;

/**
 * Admin home shell with bottom navigation and shortcuts into request lists and admin tools.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class AdminHomeActivity extends AppCompatActivity {

    private static final int COLOR_ACTIVE   = 0xFF27374D;
    private static final int COLOR_INACTIVE = 0xFF111111;

    private ImageView navHomeIcon, navScanIcon, navPassesIcon, navProfileIcon;
    private TextView  navHomeText, navScanText, navPassesText, navProfileText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        pendingBox.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, RequestListActivity.class);
            intent.putExtra(RequestListActivity.EXTRA_STATUS, "Pending");
            intent.putExtra(RequestListActivity.EXTRA_TITLE, getString(R.string.pending_requests_title));
            intent.putExtra(RequestListActivity.EXTRA_SUBTITLE, getString(R.string.pending_requests_subtitle));
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

        navHome.setOnClickListener(v      -> activateTab(0));
        navScan.setOnClickListener(v      -> activateTab(1));
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset to dashboard tab when coming back from ProfileActivity.
        activateTab(0);
    }

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
                new PorterDuffColorFilter(isActive ? COLOR_ACTIVE : COLOR_INACTIVE,
                    PorterDuff.Mode.SRC_IN));
            texts[i].setTextColor(isActive ? COLOR_ACTIVE : COLOR_INACTIVE);
        }
    }
}
