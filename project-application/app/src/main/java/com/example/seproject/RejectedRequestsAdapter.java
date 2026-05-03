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

/**
 * Lists rejected requests with reason and formatted rejection time.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class RejectedRequestsAdapter extends RecyclerView.Adapter<RejectedRequestsAdapter.ViewHolder> {

    private final List<Request> items = new ArrayList<>();
    private final List<Request> allItems = new ArrayList<>();
    private final List<String> documentIds = new ArrayList<>();
    private final List<String> allDocumentIds = new ArrayList<>();
    private final ActionListener actionListener;

    public interface ActionListener {
        void onReissue(@NonNull Request request, @NonNull String documentId);
    }

    /**
     * @param initialItems starting rows
     */
    public RejectedRequestsAdapter(@NonNull List<Request> initialItems,
                                   @NonNull List<String> initialDocumentIds,
                                   @NonNull ActionListener actionListener) {
        this.actionListener = actionListener;
        items.addAll(initialItems);
        allItems.addAll(initialItems);
        documentIds.addAll(initialDocumentIds);
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

        if (query == null || query.trim().isEmpty()) {
            items.addAll(allItems);
            documentIds.addAll(allDocumentIds);
            notifyDataSetChanged();
            return;
        }

        String searchText = query.trim().toLowerCase();

        for (int i = 0; i < allItems.size(); i++) {
            Request r = allItems.get(i);
            String docId = allDocumentIds.get(i);

            String visitorName = r.getVisitorName() == null ? "" : r.getVisitorName().toLowerCase();
            String visitorCnic = r.getVisitorCnic() == null ? "" : r.getVisitorCnic().toLowerCase();
            String invitorName = r.getInvitorName() == null ? "" : r.getInvitorName().toLowerCase();

            if (visitorName.contains(searchText)
                    || visitorCnic.contains(searchText)
                    || invitorName.contains(searchText)) {
                items.add(r);
                documentIds.add(docId);
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rejected_list_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = items.get(position);
        String documentId = documentIds.get(position);

        holder.tvRejectionReason.setText(RequestDisplayFormatter.dashIfEmpty(request.getRejectionReason()));
        holder.tvVisitorName.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitorName()));
        holder.tvVisitorCnic.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitorCnic()));
        holder.tvVisitorPhone.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitorMobileNumber()));
        holder.tvVisitReason.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitReason()));
        holder.tvInvitorType.setText(RequestDisplayFormatter.dashIfEmpty(request.getInvitorType()));
        holder.tvInvitorName.setText(RequestDisplayFormatter.dashIfEmpty(request.getInvitorName()));
        holder.tvRejectedDate.setText(RequestDisplayFormatter.formatRejectedOn(request));

        holder.btnReissue.setVisibility(View.VISIBLE);
        holder.btnReissue.setOnClickListener(v -> actionListener.onReissue(request, documentId));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvRejectionReason;
        final TextView tvVisitorName;
        final TextView tvVisitorCnic;
        final TextView tvVisitorPhone;
        final TextView tvVisitReason;
        final TextView tvInvitorType;
        final TextView tvInvitorName;
        final TextView tvRejectedDate;
        final MaterialButton btnReissue;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRejectionReason = itemView.findViewById(R.id.tv_rejection_reason);
            tvVisitorName = itemView.findViewById(R.id.tv_visitor_name);
            tvVisitorCnic = itemView.findViewById(R.id.tv_visitor_cnic);
            tvVisitorPhone = itemView.findViewById(R.id.tv_visitor_phone);
            tvVisitReason = itemView.findViewById(R.id.tv_visit_reason);
            tvInvitorType = itemView.findViewById(R.id.tv_invitor_type);
            tvInvitorName = itemView.findViewById(R.id.tv_invitor_name);
            tvRejectedDate = itemView.findViewById(R.id.tv_rejected_date);
            btnReissue = itemView.findViewById(R.id.btn_reissue);
        }
    }
}
