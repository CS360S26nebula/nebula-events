package com.example.seproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Lists visitor {@link Request} rows for a chosen status tab, backed by Firestore.
 *
 * <p>Happy path (staff): query filters by {@code requestStatus}; guard/admin can approve or reject
 * pending rows. Happy path (faculty): query filters by {@code requesterUid}; tabs filter client-side
 * via {@link FacultyRequestStatusMatcher}.</p>
 *
 * <p>Intent extras {@link #EXTRA_TITLE}, {@link #EXTRA_SUBTITLE}, {@link #EXTRA_STATUS}, and
 * {@link #EXTRA_ROLE} may be null or absent; empty {@code EXTRA_STATUS} defaults to Pending.</p>
 *
 * @author Moiz Imran
 * @version 2.0
 */
public class RequestListActivity extends AppCompatActivity implements PendingRequestsAdapter.ActionListener, PreApprovedRequestsAdapter.ActionListener, ApprovedRequestsAdapter.ActionListener {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_SUBTITLE = "extra_subtitle";
    public static final String EXTRA_STATUS = "EXTRA_STATUS";
    public static final String EXTRA_ROLE = "EXTRA_ROLE";
    private static final String STATUS_PRE_APPROVED = "Pre-Approved";
    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_APPROVED = "Approved";
    private static final String STATUS_REJECTED = "Rejected";

    private ListenerRegistration registration;

    private RecyclerView.Adapter adapter;
    private String targetStatus;
    private String role;
    private TextView tvEmpty;
    private EditText etSearch;

    /**
     * Reads intent extras, registers the reject-confirmation result listener, builds the list
     * adapter, and attaches the Firestore snapshot listener.
     *
     * @param savedInstanceState restored activity state; may be null on cold start
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        targetStatus = getIntent().getStringExtra(EXTRA_STATUS);
        if (TextUtils.isEmpty(targetStatus)) {
            targetStatus = STATUS_PENDING;
        }

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String subtitle = getIntent().getStringExtra(EXTRA_SUBTITLE);
        role = getIntent().getStringExtra(EXTRA_ROLE);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvSubtitle = findViewById(R.id.tv_subtitle);
        tvTitle.setText(title != null ? title : targetStatus + " Requests");
        tvSubtitle.setText(subtitle != null ? subtitle : "Manage your " + targetStatus + " requests");

        findViewById(R.id.btn_back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        tvEmpty = findViewById(R.id.tv_empty_state);
        etSearch = findViewById(R.id.et_search);

        getSupportFragmentManager().setFragmentResultListener(
                RejectRequestConfirmationFragment.REQUEST_KEY_REJECT_CONFIRM,
                this,
                (requestKey, bundle) -> {
                    String docId = bundle.getString(RejectRequestConfirmationFragment.BUNDLE_DOCUMENT_ID);
                    String reason = bundle.getString(RejectRequestConfirmationFragment.BUNDLE_REJECTION_REASON);
                    if (TextUtils.isEmpty(docId) || TextUtils.isEmpty(reason)) {
                        return;
                    }
                    persistRejection(docId, reason);
                });

        getSupportFragmentManager().setFragmentResultListener(
                CheckOutConfirmationFragment.REQUEST_KEY_CHECKOUT_CONFIRM,
                this,
                (requestKey, bundle) -> {
                    String docId = bundle.getString(CheckOutConfirmationFragment.BUNDLE_DOCUMENT_ID);
                    if (!TextUtils.isEmpty(docId)) {
                        persistCheckOut(docId);
                    }
                });

        setupRecyclerView();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (STATUS_APPROVED.equals(targetStatus) && adapter instanceof ApprovedRequestsAdapter) {
                    ((ApprovedRequestsAdapter) adapter).filter(s.toString());
                    tvEmpty.setVisibility(((ApprovedRequestsAdapter) adapter).getItemCount() == 0 ? View.VISIBLE : View.GONE);
                }
                else if (STATUS_PRE_APPROVED.equals(targetStatus) && adapter instanceof PreApprovedRequestsAdapter) {
                    ((PreApprovedRequestsAdapter) adapter).filter(s.toString());
                    tvEmpty.setVisibility(((PreApprovedRequestsAdapter) adapter).getItemCount() == 0 ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        attachDatabaseListener();
    }

    /**
     * Sets up the RecyclerView and assigns the suitable adapter
     * on the basis of role of the user as well as the request status
     */

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.rv_requests);
        rv.setLayoutManager(new LinearLayoutManager(this));

        boolean staff = "Guard".equals(role) || "Admin".equals(role);
        boolean faculty = "Faculty".equals(role);

        switch (targetStatus) {
            case STATUS_PENDING:
                if (staff) {
                    adapter = new PendingRequestsAdapter(this);
                } else if (faculty) {
                    adapter = new FacultyPendingRequestsAdapter(new ArrayList<>());
                } else {
                    adapter = new PendingRequestsAdapter(this);
                }
                break;
            case STATUS_PRE_APPROVED:
                if (staff) {
                    adapter = new PreApprovedRequestsAdapter(new ArrayList<>(), new ArrayList<>(), this, PreApprovedRequestsAdapter.MODE_PRE_APPROVED);
                } else if (faculty) {
                    adapter = new FacultyApprovedRequestsAdapter(new ArrayList<>());
                } else {
                    adapter = new PreApprovedRequestsAdapter(new ArrayList<>(), new ArrayList<>(), this, PreApprovedRequestsAdapter.MODE_PRE_APPROVED);
                }
                break;
            case STATUS_APPROVED:
                if (staff) {
                    adapter = new ApprovedRequestsAdapter(new ArrayList<>(), new ArrayList<>(), this);
                } else if (faculty) {
                    adapter = new FacultyApprovedRequestsAdapter(new ArrayList<>());
                } else {
                    adapter = new ApprovedRequestsAdapter(new ArrayList<>(), new ArrayList<>(), this);
                }
                break;
            case STATUS_REJECTED:
                if (staff) {
                    adapter = new RejectedRequestsAdapter(new ArrayList<>());
                } else if (faculty) {
                    adapter = new FacultyRejectedRequestsAdapter(new ArrayList<>());
                } else {
                    adapter = new RejectedRequestsAdapter(new ArrayList<>());
                }
                break;
            default:
                adapter = new PendingRequestsAdapter(this);
                break;
        }

        rv.setAdapter(adapter);
    }

    /**
     * Attaches a Firestore snapshot listener for request data retrieval in real-time
     */

    private void attachDatabaseListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean faculty = "Faculty".equals(role);

        if (faculty && user == null) {
            Toast.makeText(this, "Sign in to view your requests.", Toast.LENGTH_SHORT).show();
            tvEmpty.setVisibility(View.VISIBLE);
            pushEmptyToAdapter();
            return;
        }

        Query query;
        if (faculty) {
            query = db.collection("requests").whereEqualTo("requesterUid", user.getUid());
        } else {
            query = db.collection("requests").whereEqualTo("requestStatus", targetStatus);
        }

        registration = query.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Toast.makeText(this, "Failed to load requests.", Toast.LENGTH_SHORT).show();
                tvEmpty.setVisibility(View.VISIBLE);
                return;
            }

            if (snapshots == null) return;

            List<Request> list = new ArrayList<>();
            List<String> ids = new ArrayList<>();

            for (QueryDocumentSnapshot doc : snapshots) {
                Request r = doc.toObject(Request.class);
                if (r == null) {
                    r = new Request();
                }
                if (TextUtils.isEmpty(r.getRequestId())) {
                    r.setRequestId(doc.getId());
                }

                if (faculty && !matchesFacultyTargetStatus(r.getRequestStatus())) {
                    continue;
                }

                list.add(r);
                ids.add(doc.getId());
            }

            if (adapter instanceof PendingRequestsAdapter) {
                ((PendingRequestsAdapter) adapter).setItems(list, ids);
            } else if (adapter instanceof ApprovedRequestsAdapter) {
                ((ApprovedRequestsAdapter) adapter).setItems(list, ids);
                if (etSearch != null) {
                    ((ApprovedRequestsAdapter) adapter).filter(etSearch.getText().toString());
                }
            } else if (adapter instanceof PreApprovedRequestsAdapter) {
                ((PreApprovedRequestsAdapter) adapter).setItems(list, ids);
                if (etSearch != null && STATUS_PRE_APPROVED.equals(targetStatus)) {
                    ((PreApprovedRequestsAdapter) adapter).filter(etSearch.getText().toString());
                }
            } else if (adapter instanceof FacultyApprovedRequestsAdapter) {
                ((FacultyApprovedRequestsAdapter) adapter).setItems(list);
            } else if (adapter instanceof FacultyPendingRequestsAdapter) {
                ((FacultyPendingRequestsAdapter) adapter).setItems(list);
            } else if (adapter instanceof RejectedRequestsAdapter) {
                ((RejectedRequestsAdapter) adapter).setItems(list);
            } else if (adapter instanceof FacultyRejectedRequestsAdapter) {
                ((FacultyRejectedRequestsAdapter) adapter).setItems(list);
            }

            if (adapter instanceof ApprovedRequestsAdapter) {
                tvEmpty.setVisibility(((ApprovedRequestsAdapter) adapter).getItemCount() == 0 ? View.VISIBLE : View.GONE);
            } else if (adapter instanceof PreApprovedRequestsAdapter
                    && STATUS_PRE_APPROVED.equals(targetStatus)) {
                tvEmpty.setVisibility(((PreApprovedRequestsAdapter) adapter).getItemCount() == 0 ? View.VISIBLE : View.GONE);
            } else {
                tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Delegates to {@link FacultyRequestStatusMatcher#matches(String, String)} using this
     * activity's {@code targetStatus}.
     *
     * @param status request document status; may be null
     * @return whether the row should appear for the current faculty tab
     */
    private boolean matchesFacultyTargetStatus(String status) {
        return FacultyRequestStatusMatcher.matches(targetStatus, status);
    }

    /**
     * Clears adapter data and displays empty state.
     */

    private void pushEmptyToAdapter() {
        if (adapter instanceof PendingRequestsAdapter) {
            ((PendingRequestsAdapter) adapter).setItems(new ArrayList<>(), new ArrayList<>());
        } else if (adapter instanceof ApprovedRequestsAdapter) {
            ((ApprovedRequestsAdapter) adapter).setItems(new ArrayList<>(), new ArrayList<>());
        } else if (adapter instanceof PreApprovedRequestsAdapter) {
            ((PreApprovedRequestsAdapter) adapter).setItems(new ArrayList<>(), new ArrayList<>());
        } else if (adapter instanceof FacultyApprovedRequestsAdapter) {
            ((FacultyApprovedRequestsAdapter) adapter).setItems(new ArrayList<>());
        } else if (adapter instanceof FacultyPendingRequestsAdapter) {
            ((FacultyPendingRequestsAdapter) adapter).setItems(new ArrayList<>());
        } else if (adapter instanceof RejectedRequestsAdapter) {
            ((RejectedRequestsAdapter) adapter).setItems(new ArrayList<>());
        } else if (adapter instanceof FacultyRejectedRequestsAdapter) {
            ((FacultyRejectedRequestsAdapter) adapter).setItems(new ArrayList<>());
        }
    }

    /**
     * Removes the Firestore registration to avoid leaks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registration != null) {
            registration.remove();
        }
    }

    /**
     * Pre-approves a pending request (staff only): sets status to Pre-Approved and assigns a pass id.
     *
     * @param request    row being approved
     * @param documentId Firestore document id
     */
    @Override
    public void onApprove(@NonNull Request request, @NonNull String documentId) {
        boolean staff = "Guard".equals(role) || "Admin".equals(role);
        if (!staff || !STATUS_PENDING.equals(targetStatus)) {
            return;
        }
        String generatedPassId = "PASS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        FirebaseFirestore.getInstance()
                .collection("requests")
                .document(documentId)
                .update(
                        "requestStatus", STATUS_PRE_APPROVED,
                        "passId", generatedPassId
                )
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, R.string.request_pre_approved, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to approve", Toast.LENGTH_SHORT).show());
    }

    /**
     * Opens the rejection dialog for staff on the pending list.
     *
     * @param request    row being rejected
     * @param documentId Firestore document id
     */
    @Override
    public void onReject(@NonNull Request request, @NonNull String documentId) {
        boolean staff = "Guard".equals(role) || "Admin".equals(role);
        if (!staff || !STATUS_PENDING.equals(targetStatus)) {
            return;
        }
        RejectRequestConfirmationFragment.newInstance(documentId)
                .show(getSupportFragmentManager(), "reject_request_dialog");
    }

    /**
     * Marks a visitor as checked in.
     *
     * @param request Row being checked in
     * @param documentId Firestore document id
     */

    @Override
    public void onCheckIn(@NonNull Request request, @NonNull String documentId) {
        boolean staff = "Guard".equals(role) || "Admin".equals(role);
        if (!staff || !STATUS_PRE_APPROVED.equals(targetStatus)) {
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("requests")
                .document(documentId)
                .update(
                        "requestStatus", STATUS_APPROVED,
                        "checkedInAtMillis", System.currentTimeMillis()
                )
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Visitor checked in successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to check in visitor.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Opens checkout confirmation dialog.
     *
     * @param request Row being checked out
     * @param documentId Firestore document id
     */
    @Override
    public void onCheckOut(@NonNull Request request, @NonNull String documentId) {
        boolean staff = "Guard".equals(role) || "Admin".equals(role);
        if (!staff || !STATUS_APPROVED.equals(targetStatus)) {
            return;
        }
        CheckOutConfirmationFragment
                .newInstance(documentId, request.getVisitorName())
                .show(getSupportFragmentManager(), "checkout_confirm_dialog");
    }

    /**
     * Writes rejection fields to Firestore for the given document.
     *
     * @param documentId target document id
     * @param reason     user-provided rejection reason
     */
    private void persistRejection(@NonNull String documentId, @NonNull String reason) {
        FirebaseFirestore.getInstance()
                .collection("requests")
                .document(documentId)
                .update(
                        "requestStatus", STATUS_REJECTED,
                        "rejectionReason", reason,
                        "rejectedAtMillis", System.currentTimeMillis()
                )
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, R.string.request_rejected, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, R.string.request_reject_failed, Toast.LENGTH_SHORT).show());
    }

    /**
     * Writes check out fields to Firestore for the given document.
     *
     * @param documentId target document id
     */
    private void persistCheckOut(@NonNull String documentId) {
        FirebaseFirestore.getInstance()
                .collection("requests")
                .document(documentId)
                .update(
                        "requestStatus",      "Expired",
                        "checkedOutAtMillis", System.currentTimeMillis()
                )
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, R.string.visitor_checked_out, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, R.string.checkout_failed, Toast.LENGTH_SHORT).show());
    }
}