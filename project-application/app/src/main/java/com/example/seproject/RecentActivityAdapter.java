package com.example.seproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ViewHolder> {

    public interface ActionListener {
        void onCheckOut(@NonNull String requestDocumentId, @NonNull String visitorName);
        void onEndEmergency(@NonNull String emergencyDocumentId);
    }

    private final List<RecentActivityItem> items = new ArrayList<>();
    private final ActionListener actionListener;

    public RecentActivityAdapter(@NonNull ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setItems(@NonNull List<RecentActivityItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_activity_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentActivityItem item = items.get(position);

        boolean visitor = item.getType() == RecentActivityItem.TYPE_ACTIVE_VISITOR;
        holder.tvIdLabel.setText(visitor ? "Pass ID:" : "Emergency ID:");
        holder.tvIdValue.setText(valueOrDash(item.getPrimaryId()));
        holder.tvBadge.setText(visitor ? "Approved" : "Emergency");
        holder.tvBadge.setBackgroundResource(visitor ? R.drawable.bage_active_visitor : R.drawable.badge_emergency);
        holder.tvBadge.setTextColor(0xFF111111);

        holder.tvDateValue.setText(formatTime(item.getEventTimeMillis()));
        holder.tvNameLabel.setText(visitor ? "Guest Name" : "Emergency Type");
        holder.tvNameValue.setText(valueOrDash(item.getTitleValue()));

        if (visitor) {
            holder.btnAction.setText("Check Out");
            holder.btnAction.setOnClickListener(v ->
                    actionListener.onCheckOut(item.getDocumentId(), valueOrDash(item.getTitleValue())));
        } else {
            holder.btnAction.setText("End Emergency & Close Gate");
            holder.btnAction.setOnClickListener(v ->
                    actionListener.onEndEmergency(item.getDocumentId()));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatTime(long millis) {
        if (millis <= 0) {
            return "-";
        }
        return new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.ENGLISH).format(new Date(millis));
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvIdLabel;
        final TextView tvIdValue;
        final TextView tvBadge;
        final TextView tvDateValue;
        final TextView tvNameLabel;
        final TextView tvNameValue;
        final MaterialButton btnAction;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdLabel = itemView.findViewById(R.id.tv_id_label);
            tvIdValue = itemView.findViewById(R.id.tv_id_value);
            tvBadge = itemView.findViewById(R.id.tv_status_badge);
            tvDateValue = itemView.findViewById(R.id.tv_date_value);
            tvNameLabel = itemView.findViewById(R.id.tv_name_label);
            tvNameValue = itemView.findViewById(R.id.tv_name_value);
            btnAction = itemView.findViewById(R.id.btn_action);
        }
    }
}
