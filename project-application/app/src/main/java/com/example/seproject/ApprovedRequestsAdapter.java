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
 * Lists approved requests (pass id and visitor details).
 *
 * @author Abdullah Ahmad
 * @version 1.0
 */
public class ApprovedRequestsAdapter extends RecyclerView.Adapter<ApprovedRequestsAdapter.ViewHolder> {

    public static final String MODE_APPROVED = "Approved";

    public interface ActionListener {
        void onCheckOut(@NonNull Request request, @NonNull String documentId);
    }

    private final List<Request> items = new ArrayList<>();
    private final List<Request> allItems = new ArrayList<>();
    private final List<String> documentIds = new ArrayList<>();
    private final List<String> allDocumentIds = new ArrayList<>();
    private final ActionListener actionListener;

    /**
     * @param initialItems Starting rows; copied into an internal list
     * @param initialDocumentIds Firestore document ids corresponding to starting rows
     * @param actionListener Callback for user actions
     */
    public ApprovedRequestsAdapter(@NonNull List<Request> initialItems, @NonNull List<String> initialDocumentIds, @NonNull ActionListener actionListener) {
        this.actionListener = actionListener;

        items.addAll(initialItems);
        allItems.addAll(initialItems);
        documentIds.addAll(initialDocumentIds);
        allDocumentIds.addAll(initialDocumentIds);
    }

    /**
     * @param newItems Updated list
     * @param newDocumentIds Firestore document ids corresponding to the updated list
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

    /**
     * @param query search string; may or may not be empty
     */

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

    /**
     * @param parent Parent view group
     * @param viewType Type of the view
     * @return new ViewHolder
     */

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_approved_request_card_guard, parent, false);
        return new ViewHolder(view);
    }

    /**
     * @param holder Instance of ViewHolder
     * @param position Position of item
     */

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

        holder.btnCheckOut.setVisibility(View.VISIBLE);
        holder.btnCheckOut.setOnClickListener(v -> actionListener.onCheckOut(request, documentId));
    }

    /**
     * @return item count
     */

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder class holding references to UI components
     */

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvRequestId;
        final TextView tvVisitorName;
        final TextView tvVisitorCnic;
        final TextView tvVisitorPhone;
        final TextView tvVisitReason;
        final TextView tvInvitorType;
        final TextView tvInvitorName;
        final TextView tvVisitDate;
        final MaterialButton btnCheckOut;

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
            btnCheckOut = itemView.findViewById(R.id.btn_checkout);
        }
    }
}