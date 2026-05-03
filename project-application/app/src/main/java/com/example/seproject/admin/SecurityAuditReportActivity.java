package com.example.seproject.admin;

import com.example.seproject.Request;
import com.example.seproject.User;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seproject.R;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SecurityAuditReportActivity extends AppCompatActivity {

    /**
     * Optional Intent extra (String).
     * When present, the report is scoped to only show records belonging to that faculty member
     * (matched via the {@code requesterUid} field on requests).
     * Emergency logs are excluded in faculty-scoped mode as they are not faculty-specific.
     * Omit this extra (or pass null) for the full admin/guard view.
     */
    public static final String EXTRA_FACULTY_UID = "EXTRA_FACULTY_UID";

    /** Non-null when the screen was opened in faculty-scoped mode. */
    private String facultyUid = null;

    // Views
    private RecyclerView  rvAuditLog;
    private TextView      tvEmptyState;
    private TextView      tvResultCount;
    private EditText      etSearch;
    private ChipGroup     chipGroupFilter;
    private View          progressBar;
    private TextView      tvDateFrom;
    private TextView      tvDateTo;
    private ImageView     btnClearDateFrom;
    private ImageView     btnClearDateTo;

    // Data
    private final AuditLogAdapter     adapter       = new AuditLogAdapter();
    private final List<AuditLogItem>  allItems      = new ArrayList<>();
    private final Map<String, String> userNameByUid = new HashMap<>();
    private final Map<String, String> userIdByUid   = new HashMap<>();

    // Date range state (0 = not set)
    private long filterFromMs = 0L;
    private long filterToMs   = 0L;

    private static final SimpleDateFormat DATE_DISPLAY_FMT =
            new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_audit_report);

        bindViews();
        setupRecyclerView();
        setupChips();
        setupSearch();
        setupDatePickers();

        facultyUid = getIntent().getStringExtra(EXTRA_FACULTY_UID);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        loadData();
    }

    private void bindViews() {
        rvAuditLog       = findViewById(R.id.rv_audit_log);
        tvEmptyState     = findViewById(R.id.tv_audit_empty);
        tvResultCount    = findViewById(R.id.tv_audit_result_count);
        etSearch         = findViewById(R.id.et_audit_search);
        chipGroupFilter  = findViewById(R.id.chip_group_filter);
        progressBar      = findViewById(R.id.audit_progress_bar);
        tvDateFrom       = findViewById(R.id.tv_date_from);
        tvDateTo         = findViewById(R.id.tv_date_to);
        btnClearDateFrom = findViewById(R.id.btn_clear_date_from);
        btnClearDateTo   = findViewById(R.id.btn_clear_date_to);
    }

    private void setupRecyclerView() {
        rvAuditLog.setLayoutManager(new LinearLayoutManager(this));
        rvAuditLog.setAdapter(adapter);
    }

    private void setupChips() {
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> applyFilters());
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { applyFilters(); }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void setupDatePickers() {
        tvDateFrom.setOnClickListener(v -> showDatePicker(true));
        tvDateTo.setOnClickListener(v -> showDatePicker(false));

        btnClearDateFrom.setOnClickListener(v -> {
            filterFromMs = 0L;
            tvDateFrom.setText(getString(R.string.audit_date_from_hint));
            btnClearDateFrom.setVisibility(View.GONE);
            applyFilters();
        });

        btnClearDateTo.setOnClickListener(v -> {
            filterToMs = 0L;
            tvDateTo.setText(getString(R.string.audit_date_to_hint));
            btnClearDateTo.setVisibility(View.GONE);
            applyFilters();
        });

        btnClearDateFrom.setVisibility(View.GONE);
        btnClearDateTo.setVisibility(View.GONE);
    }

    private void showDatePicker(boolean isFrom) {
        Calendar cal = Calendar.getInstance();
        if (isFrom && filterFromMs > 0) cal.setTimeInMillis(filterFromMs);
        if (!isFrom && filterToMs > 0)  cal.setTimeInMillis(filterToMs);

        new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, day, 0, 0, 0);
            selected.set(Calendar.MILLISECOND, 0);

            if (isFrom) {
                filterFromMs = selected.getTimeInMillis();
                tvDateFrom.setText(DATE_DISPLAY_FMT.format(selected.getTime()));
                btnClearDateFrom.setVisibility(View.VISIBLE);
            } else {
                // End of day → include records created at any time on that date
                selected.set(Calendar.HOUR_OF_DAY, 23);
                selected.set(Calendar.MINUTE, 59);
                selected.set(Calendar.SECOND, 59);
                filterToMs = selected.getTimeInMillis();
                tvDateTo.setText(DATE_DISPLAY_FMT.format(selected.getTime()));
                btnClearDateTo.setVisibility(View.VISIBLE);
            }
            applyFilters();
        },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadData() {
        showLoading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").get()
                .addOnSuccessListener(userSnaps -> {
                    userNameByUid.clear();
                    userIdByUid.clear();
                    for (QueryDocumentSnapshot d : userSnaps) {
                        User u = d.toObject(User.class);
                        if (u == null) continue;
                        userNameByUid.put(d.getId(),
                                u.getFullName() != null ? u.getFullName() : "");
                        userIdByUid.put(d.getId(),
                                u.getUserId() != null ? u.getUserId() : "");
                    }

                    final List<AuditLogItem> requestItems   = new ArrayList<>();
                    final List<AuditLogItem> emergencyItems = new ArrayList<>();
                    final int[] done = {0};

                    // Always fetch ALL requests (same as admin/guard), then client-side filter
                    // by requesterUid in faculty mode. This guarantees count consistency:
                    // the faculty sees exactly the subset of the full admin/guard list that
                    // belongs to them, with no Firestore index or field-mismatch surprises.
                    final int totalTasks = (facultyUid != null) ? 1 : 2;

                    db.collection("requests").get()
                            .addOnSuccessListener(reqSnaps -> {
                                for (QueryDocumentSnapshot d : reqSnaps) {
                                    Request r = d.toObject(Request.class);
                                    if (r == null) continue;

                                    // FACULTY SCOPE FILTER: skip requests that do not belong
                                    // to the logged-in faculty. This mirrors exactly how the
                                    // admin/guard full list is built, then narrows it client-side
                                    // to only Ali Sajjad's requests — guaranteeing the count
                                    // Ali sees is a consistent subset of the admin/guard count.
                                    String facultyOwnerUid = d.getString("requesterUid");
                                    boolean isFacultyMode  = (facultyUid != null);
                                    if (isFacultyMode && !facultyUid.equals(facultyOwnerUid)) {
                                        continue;
                                    }

                                    if (r.getIsAdhoc()) {
                                        String uid = isFacultyMode
                                                ? facultyOwnerUid
                                                : d.getString("createdByUid");
                                        requestItems.add(AuditLogItem.adhocRequest(
                                                nvl(r.getRequestId()),
                                                nvl(r.getVisitorName()),
                                                uid,
                                                resolvedName(uid),
                                                r.getCreatedAtMillis(),
                                                r.getVisitReason(),
                                                nvl(r.getRequestStatus())));
                                    }

                                    if (r.getCheckedInAtMillis() > 0) {
                                        String uid;
                                        if (isFacultyMode) {
                                            uid = facultyOwnerUid;
                                        } else {
                                            uid = d.getString("approvedByUid");
                                            if (uid == null || uid.trim().isEmpty())
                                                uid = d.getString("createdByUid");
                                        }
                                        requestItems.add(AuditLogItem.entry(
                                                nvl(r.getPassId()),
                                                nvl(r.getVisitorName()),
                                                uid,
                                                resolvedName(uid),
                                                r.getCheckedInAtMillis(),
                                                r.getVisitReason()));
                                    }

                                    if (r.getCheckedOutAtMillis() > 0) {
                                        String uid;
                                        if (isFacultyMode) {
                                            uid = facultyOwnerUid;
                                        } else {
                                            uid = d.getString("approvedByUid");
                                            if (uid == null || uid.trim().isEmpty())
                                                uid = d.getString("createdByUid");
                                        }
                                        requestItems.add(AuditLogItem.exit(
                                                nvl(r.getPassId()),
                                                nvl(r.getVisitorName()),
                                                uid,
                                                resolvedName(uid),
                                                r.getCheckedOutAtMillis(),
                                                r.getVisitReason()));
                                    }
                                }
                                done[0]++;
                                if (done[0] == totalTasks) mergeAndDisplay(requestItems, emergencyItems);
                            })
                            .addOnFailureListener(e -> {
                                showLoading(false);
                                Toast.makeText(this,
                                        "Failed to load visitor logs: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });

                    // Emergency logs are campus-wide, not linked to any faculty — skip in faculty mode
                    if (facultyUid != null) {
                        // Faculty mode: no emergency logs to fetch, already counted above
                    } else {
                        db.collection("emergencyLogs").get()
                                .addOnSuccessListener(emSnaps -> {
                                    for (QueryDocumentSnapshot d : emSnaps) {
                                        String logId   = d.getString("logId");
                                        String emType  = d.getString("emergencyType");
                                        String guardId = d.getString("guardId");
                                        String reason  = d.getString("reason");
                                        long ts = d.contains("timestamp")
                                                ? d.getLong("timestamp") : 0L;

                                        if (logId  == null) logId  = d.getId();
                                        if (emType == null) emType = "Emergency";

                                        emergencyItems.add(AuditLogItem.emergency(
                                                logId, emType,
                                                guardId, resolvedName(guardId),
                                                ts, reason));
                                    }
                                    done[0]++;
                                    if (done[0] == totalTasks) mergeAndDisplay(requestItems, emergencyItems);
                                })
                                .addOnFailureListener(e -> {
                                    showLoading(false);
                                    Toast.makeText(this,
                                            "Failed to load emergency logs: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(this,
                            "Failed to load user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void mergeAndDisplay(@NonNull List<AuditLogItem> requests,
                                 @NonNull List<AuditLogItem> emergencies) {
        allItems.clear();
        allItems.addAll(requests);
        allItems.addAll(emergencies);
        allItems.sort(Comparator.comparingLong(AuditLogItem::getEventTimeMillis).reversed());
        showLoading(false);
        applyFilters();
    }

    void applyFilters() {
        int checkedId = chipGroupFilter.getCheckedChipId();
        Integer typeFilter = null;

        if      (checkedId == R.id.chip_filter_entry)     typeFilter = AuditLogItem.TYPE_ENTRY;
        else if (checkedId == R.id.chip_filter_exit)      typeFilter = AuditLogItem.TYPE_EXIT;
        else if (checkedId == R.id.chip_filter_adhoc)     typeFilter = AuditLogItem.TYPE_ADHOC_REQUEST;
        else if (checkedId == R.id.chip_filter_emergency) typeFilter = AuditLogItem.TYPE_EMERGENCY;

        String query = etSearch.getText().toString().trim().toLowerCase(Locale.ENGLISH);

        List<AuditLogItem> filtered = new ArrayList<>();
        for (AuditLogItem item : allItems) {
            if (typeFilter != null && item.getType() != typeFilter) continue;
            if (filterFromMs > 0 && item.getEventTimeMillis() < filterFromMs) continue;
            if (filterToMs   > 0 && item.getEventTimeMillis() > filterToMs)   continue;
            if (!query.isEmpty()) {
                boolean match =
                        contains(item.getEventId(),       query)
                                || contains(item.getVisitorOrType(), query)
                                || contains(item.getActorName(),     query)
                                || contains(item.getActorId(),       query)
                                || contains(item.getExtraDetail(),   query)
                                || contains(item.getTypeLabel(),     query);
                if (!match) continue;
            }
            filtered.add(item);
        }

        adapter.setItems(filtered);
        tvResultCount.setText(filtered.size() + " record" + (filtered.size() == 1 ? "" : "s"));
        tvEmptyState.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private String resolvedName(String uid) {
        if (uid == null || uid.trim().isEmpty()) return null;
        String name = userNameByUid.get(uid);
        return (name != null && !name.isEmpty()) ? name : null;
    }

    private boolean contains(String source, String query) {
        return source != null && source.toLowerCase(Locale.ENGLISH).contains(query);
    }

    private String nvl(String v) { return v != null ? v : ""; }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        rvAuditLog.setVisibility(loading ? View.GONE : View.VISIBLE);
    }
}