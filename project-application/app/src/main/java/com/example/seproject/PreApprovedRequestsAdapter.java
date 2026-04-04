package com.example.seproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;

/**
 * Lists pre-approved requests (pass id and visitor details).
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class PreApprovedRequestsAdapter extends RecyclerView.Adapter<PreApprovedRequestsAdapter.ViewHolder> {

    public static final String MODE_PRE_APPROVED = "Pre-Approved";
    public static final String MODE_APPROVED = "Approved";
    public interface ActionListener {
        void onCheckIn(@NonNull Request request, @NonNull String documentId);
    }

    private final List<Request> items = new ArrayList<>();
    private final List<Request> allItems = new ArrayList<>();
    private final List<String> documentIds = new ArrayList<>();
    private final List<String> allDocumentIds = new ArrayList<>();
    private final ActionListener actionListener;
    private final String mode;

    /**
     * @param initialItems starting rows; copied into an internal list
     */

    public PreApprovedRequestsAdapter(@NonNull List<Request> initialItems, @NonNull List<String> initialDocumentIds, @NonNull ActionListener actionListener, @NonNull String mode) {
        this.actionListener = actionListener;
        this.mode = mode;

        items.clear();
        items.addAll(initialItems);
        allItems.clear();
        allItems.addAll(initialItems);

        documentIds.clear();
        documentIds.addAll(initialDocumentIds);

        allDocumentIds.clear();
        allDocumentIds.addAll(initialDocumentIds);
    }

    /**
     * @param newItems replaces all rows
     */
    public void setItems(@NonNull List<Request> newItems, @NonNull List<String> newDocumentIds) {
        items.clear();
        items.addAll(newItems);

        allItems.clear();
        allItems.addAll(newItems);

        documentIds.clear();
        documentIds.addAll(newDocumentIds);

        allDocumentIds.clear();
        allDocumentIds.addAll(newDocumentIds);

        notifyDataSetChanged();
    }

    public void filter(@NonNull String query) {
        items.clear();
        documentIds.clear();

        if (TextUtils.isEmpty(query)) {
            items.addAll(allItems);
            documentIds.addAll(allDocumentIds);
            notifyDataSetChanged();
            return;
        }

        String searchText = query.trim().toLowerCase();

        for (int i = 0; i < allItems.size(); i++) {
            Request request = allItems.get(i);
            String documentId = allDocumentIds.get(i);

            String passId = request.getPassId() == null ? "" : request.getPassId().toLowerCase();
            String visitorName = request.getVisitorName() == null ? "" : request.getVisitorName().toLowerCase();
            String visitorCnic = request.getVisitorCnic() == null ? "" : request.getVisitorCnic().toLowerCase();
            String invitorName = request.getInvitorName() == null ? "" : request.getInvitorName().toLowerCase();

            if (passId.contains(searchText)
                    || visitorName.contains(searchText)
                    || visitorCnic.contains(searchText)
                    || invitorName.contains(searchText)) {
                items.add(request);
                documentIds.add(documentId);
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pre_approved_list_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = items.get(position);
        String documentId = documentIds.get(position);

        holder.tvRequestId.setText(RequestDisplayFormatter.dashIfEmpty(request.getPassId()));
        holder.tvVisitorName.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitorName()));
        holder.tvVisitorCnic.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitorCnic()));
        holder.tvVisitorPhone.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitorMobileNumber()));
        holder.tvVisitReason.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitReason()));
        holder.tvInvitorType.setText(RequestDisplayFormatter.dashIfEmpty(request.getInvitorType()));
        holder.tvInvitorName.setText(RequestDisplayFormatter.dashIfEmpty(request.getInvitorName()));
        holder.tvVisitDate.setText(RequestDisplayFormatter.formatVisitDateTime(request));
        if (MODE_PRE_APPROVED.equals(mode)) {
            holder.tvStatusBadge.setText("Pre-Approved");
            holder.btnCheckIn.setVisibility(View.VISIBLE);
            holder.btnCheckIn.setText("Check In");
            holder.btnCheckIn.setOnClickListener(v -> actionListener.onCheckIn(request, documentId));
        } else if (MODE_APPROVED.equals(mode)) {
            holder.tvStatusBadge.setText("Approved");
            holder.btnCheckIn.setVisibility(View.GONE);
            holder.btnCheckIn.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvRequestId;
        final TextView tvVisitorName;
        final TextView tvVisitorCnic;
        final TextView tvVisitorPhone;
        final TextView tvVisitReason;
        final TextView tvInvitorType;
        final TextView tvInvitorName;
        final TextView tvVisitDate;
        final TextView tvStatusBadge;
        final MaterialButton btnCheckIn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRequestId = itemView.findViewById(R.id.tv_request_id);
            tvVisitorName = itemView.findViewById(R.id.tv_visitor_name);
            tvVisitorCnic = itemView.findViewById(R.id.tv_visitor_cnic);
            tvVisitorPhone = itemView.findViewById(R.id.tv_visitor_phone);
            tvVisitReason = itemView.findViewById(R.id.tv_visit_reason);
            tvInvitorType = itemView.findViewById(R.id.tv_invitor_type);
            tvInvitorName = itemView.findViewById(R.id.tv_invitor_name);
            tvVisitDate = itemView.findViewById(R.id.tv_visit_date);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            btnCheckIn = itemView.findViewById(R.id.btn_checkout);
        }
    }
}