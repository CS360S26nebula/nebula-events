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

public class ActiveEmergencyEntriesAdapter extends RecyclerView.Adapter<ActiveEmergencyEntriesAdapter.ViewHolder> {

    public interface ActionListener {
        void onEndEmergency(@NonNull ActiveEmergencyEntry entry, @NonNull String documentId);
    }

    private final List<ActiveEmergencyEntry> items = new ArrayList<>();
    private final List<String> documentIds = new ArrayList<>();
    private final ActionListener actionListener;

    public ActiveEmergencyEntriesAdapter(@NonNull ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setItems(@NonNull List<ActiveEmergencyEntry> newItems, @NonNull List<String> newIds) {
        items.clear();
        documentIds.clear();
        items.addAll(newItems);
        documentIds.addAll(newIds);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_emergency_entry_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActiveEmergencyEntry entry = items.get(position);
        String docId = documentIds.get(position);

        holder.tvEmergencyId.setText(valueOrDash(entry.getLogId()));
        holder.tvType.setText(valueOrDash(entry.getEmergencyType()));
        holder.tvGuardId.setText(valueOrDash(entry.getGuardId()));
        holder.tvReason.setText(valueOrDash(entry.getReason()));
        holder.tvTime.setText(formatTime(entry.getTimestamp()));
        holder.btnEndEmergency.setOnClickListener(v -> actionListener.onEndEmergency(entry, docId));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private String formatTime(long millis) {
        if (millis <= 0) {
            return "-";
        }
        return new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.ENGLISH).format(new Date(millis));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvEmergencyId;
        final TextView tvType;
        final TextView tvGuardId;
        final TextView tvReason;
        final TextView tvTime;
        final MaterialButton btnEndEmergency;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmergencyId = itemView.findViewById(R.id.tv_emergency_id_value);
            tvType = itemView.findViewById(R.id.tv_emergency_type_value);
            tvGuardId = itemView.findViewById(R.id.tv_guard_id_value);
            tvReason = itemView.findViewById(R.id.tv_reason_value);
            tvTime = itemView.findViewById(R.id.tv_time_value);
            btnEndEmergency = itemView.findViewById(R.id.btn_end_emergency);
        }
    }
}
