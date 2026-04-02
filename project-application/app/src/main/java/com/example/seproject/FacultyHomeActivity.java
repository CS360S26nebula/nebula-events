package com.example.seproject;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

/**
 * Faculty dashboard with navigation, submit-request overlay, and request list shortcuts.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class FacultyHomeActivity extends AppCompatActivity {

    private static final int COLOR_ACTIVE   = 0xFF27374D;
    private static final int COLOR_INACTIVE = 0xFF111111;

    private ImageView navHomeIcon, navDownloadIcon, navPassesIcon, navProfileIcon;
    private TextView  navHomeText, navDownloadText, navPassesText, navProfileText;
    private FrameLayout facultyFragmentOverlay;

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

        LinearLayout navHome          = findViewById(R.id.navHome);
        LinearLayout navDownload      = findViewById(R.id.navDownload);
        LinearLayout navSubmitRequest = findViewById(R.id.navSubmitRequest);
        LinearLayout navPasses        = findViewById(R.id.navPasses);
        LinearLayout navProfile       = findViewById(R.id.navProfile);
        View approvedBox = findViewById(R.id.approvedBox);
        View rejectedBox = findViewById(R.id.rejectedBox);
        View pendingBox = findViewById(R.id.pendingBox);

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

        navPasses.setOnClickListener(v        -> activateTab(3));
        navProfile.setOnClickListener(v -> {
            activateTab(4);
            startActivity(new Intent(FacultyHomeActivity.this, ProfileActivity.class));
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
                new PorterDuffColorFilter(isActive ? COLOR_ACTIVE : COLOR_INACTIVE,
                    PorterDuff.Mode.SRC_IN));
            texts[i].setTextColor(isActive ? COLOR_ACTIVE : COLOR_INACTIVE);
        }
    }
}
