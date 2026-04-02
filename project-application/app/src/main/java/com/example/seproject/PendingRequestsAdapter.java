package com.example.seproject;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for pending requests with approve/reject actions (guard/admin).
 *
 * <p>Happy path: {@link #setItems(List, List)} receives aligned requests and Firestore ids;
 * binding shows {@link RequestDisplayFormatter} output and wires buttons to
 * {@link ActionListener}.</p>
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class PendingRequestsAdapter extends RecyclerView.Adapter<PendingRequestsAdapter.Holder> {

    /**
     * Staff actions on a pending row.
     */
    public interface ActionListener {
        /**
         * @param request    non-null row model
         * @param documentId Firestore document id
         */
        void onApprove(@NonNull Request request, @NonNull String documentId);

        /**
         * @param request    non-null row model
         * @param documentId Firestore document id
         */
        void onReject(@NonNull Request request, @NonNull String documentId);
    }

    private final List<Request> requests = new ArrayList<>();
    private final List<String> documentIds = new ArrayList<>();
    private final ActionListener actionListener;

    /**
     * @param actionListener callback for approve/reject; must not be null
     */
    public PendingRequestsAdapter(@NonNull ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    /**
     * Replaces backing lists and refreshes the view.
     *
     * @param newRequests parallel list of models
     * @param newDocIds   parallel list of document ids (same size as {@code newRequests} expected)
     */
    public void setItems(@NonNull List<Request> newRequests, @NonNull List<String> newDocIds) {
        requests.clear();
        documentIds.clear();
        requests.addAll(newRequests);
        documentIds.addAll(newDocIds);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request_list_card, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        Request r = requests.get(position);
        String docId = documentIds.get(position);

        String idText = !TextUtils.isEmpty(r.getRequestId()) ? r.getRequestId() : docId;
        h.tvRequestId.setText(idText);
        h.tvVisitorName.setText(RequestDisplayFormatter.dashIfEmpty(r.getVisitorName()));
        h.tvVisitorCnic.setText(RequestDisplayFormatter.dashIfEmpty(r.getVisitorCnic()));
        h.tvVisitorPhone.setText(RequestDisplayFormatter.dashIfEmpty(r.getVisitorMobileNumber()));
        h.tvVisitReason.setText(RequestDisplayFormatter.dashIfEmpty(r.getVisitReason()));
        h.tvInvitorType.setText(RequestDisplayFormatter.dashIfEmpty(r.getInvitorType()));
        h.tvInvitorName.setText(RequestDisplayFormatter.dashIfEmpty(r.getInvitorName()));
        h.tvVisitDate.setText(RequestDisplayFormatter.formatVisitDateTime(r));

        h.btnApprove.setOnClickListener(v -> actionListener.onApprove(r, docId));
        h.btnReject.setOnClickListener(v -> actionListener.onReject(r, docId));
        h.btnReject.setEnabled(true);
        h.btnReject.setAlpha(1f);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final TextView tvRequestId;
        final TextView tvVisitorName;
        final TextView tvVisitorCnic;
        final TextView tvVisitorPhone;
        final TextView tvVisitReason;
        final TextView tvInvitorType;
        final TextView tvInvitorName;
        final TextView tvVisitDate;
        final MaterialButton btnApprove;
        final MaterialButton btnReject;

        Holder(@NonNull View itemView) {
            super(itemView);
            tvRequestId = itemView.findViewById(R.id.tv_request_id);
            tvVisitorName = itemView.findViewById(R.id.tv_visitor_name);
            tvVisitorCnic = itemView.findViewById(R.id.tv_visitor_cnic);
            tvVisitorPhone = itemView.findViewById(R.id.tv_visitor_phone);
            tvVisitReason = itemView.findViewById(R.id.tv_visit_reason);
            tvInvitorType = itemView.findViewById(R.id.tv_invitor_type);
            tvInvitorName = itemView.findViewById(R.id.tv_invitor_name);
            tvVisitDate = itemView.findViewById(R.id.tv_visit_date);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}
