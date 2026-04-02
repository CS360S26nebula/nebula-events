package com.example.seproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Faculty-facing rejected requests list.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class FacultyRejectedRequestsAdapter extends RecyclerView.Adapter<FacultyRejectedRequestsAdapter.ViewHolder> {

    private final List<Request> items = new ArrayList<>();

    /**
     * @param initialItems starting rows
     */
    public FacultyRejectedRequestsAdapter(@NonNull List<Request> initialItems) {
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
                .inflate(R.layout.item_rejected_list_card_faculty, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = items.get(position);

        holder.tvRejectionReason.setText(RequestDisplayFormatter.dashIfEmpty(request.getRejectionReason()));
        holder.tvVisitorName.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitorName()));
        holder.tvVisitorCnic.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitorCnic()));
        holder.tvVisitorPhone.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitorMobileNumber()));
        holder.tvVisitReason.setText(RequestDisplayFormatter.dashIfEmpty(request.getVisitReason()));
        holder.tvInvitorType.setText(RequestDisplayFormatter.dashIfEmpty(request.getInvitorType()));
        holder.tvInvitorName.setText(RequestDisplayFormatter.dashIfEmpty(request.getInvitorName()));
        holder.tvRejectedDate.setText(RequestDisplayFormatter.formatRejectedOn(request));
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
        }
    }
}
