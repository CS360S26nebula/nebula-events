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
 * Lists pre-approved requests (pass id and visitor details).
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class PreApprovedRequestsAdapter extends RecyclerView.Adapter<PreApprovedRequestsAdapter.ViewHolder> {

    private final List<Request> items = new ArrayList<>();

    /**
     * @param initialItems starting rows; copied into an internal list
     */
    public PreApprovedRequestsAdapter(@NonNull List<Request> initialItems) {
        items.clear();
        items.addAll(initialItems);
    }

    /**
     * @param newItems replaces all rows
     */
    public void setItems(@NonNull List<Request> newItems) {
        items.clear();
        items.addAll(newItems);
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

        holder.tvRequestId.setText(RequestDisplayFormatter.dashIfEmpty(request.getPassId()));
        holder.tvVisitorName.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitorName()));
        holder.tvVisitorCnic.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitorCnic()));
        holder.tvVisitorPhone.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitorMobileNumber()));
        holder.tvVisitReason.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitReason()));
        holder.tvInvitorType.setText(RequestDisplayFormatter.dashIfEmpty(request.getInvitorType()));
        holder.tvInvitorName.setText(RequestDisplayFormatter.dashIfEmpty(request.getInvitorName()));
        holder.tvVisitDate.setText(RequestDisplayFormatter.formatVisitDateTime(request));
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
        }
    }
}