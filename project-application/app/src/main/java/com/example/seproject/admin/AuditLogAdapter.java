package com.example.seproject.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seproject.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter used to show audit log cards in the security audit report screen.
 * <p>
 * Each row displays the type of event, ID of event, name of visitor/type, information of actor, timestamp,
 * and any optional detail or reason text.
 * </p>
 *
 * @author Abdullah Ahmad
 * @version 1.0
 */

public class AuditLogAdapter extends RecyclerView.Adapter<AuditLogAdapter.AuditViewHolder> {

    private final List<AuditLogItem> items = new ArrayList<>();
    private final SimpleDateFormat dateFormatter =
            new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.ENGLISH);

    /**
     * Replaces current dataset and refreshes the list.
     *
     * @param newItems list of audit log items
     */
    public void setItems(@NonNull List<AuditLogItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    /**
     * Inflates the audit log item layout.
     */
    @NonNull
    @Override
    public AuditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_audit_log_card, parent, false);
        return new AuditViewHolder(v);
    }

    /**
     * Binds data to a specific list item.
     */
    @Override
    public void onBindViewHolder(@NonNull AuditViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    /**
     * Returns total number of items.
     */
    @Override
    public int getItemCount() { return items.size(); }

    /**
     * ViewHolder representing a single audit log card.
     */
    final class AuditViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTypeBadge;
        private final TextView tvEventId;
        private final TextView tvVisitorOrType;
        private final TextView tvActor;
        private final TextView tvTimestamp;
        private final TextView tvDetail;

        AuditViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTypeBadge     = itemView.findViewById(R.id.tv_audit_type_badge);
            tvEventId       = itemView.findViewById(R.id.tv_audit_event_id);
            tvVisitorOrType = itemView.findViewById(R.id.tv_audit_visitor_or_type);
            tvActor         = itemView.findViewById(R.id.tv_audit_actor);
            tvTimestamp     = itemView.findViewById(R.id.tv_audit_timestamp);
            tvDetail        = itemView.findViewById(R.id.tv_audit_detail);
        }

        /**
         * Binds an {@link AuditLogItem} to UI components.
         *
         * @param item audit log data
         */
        void bind(@NonNull AuditLogItem item) {
            Context ctx = itemView.getContext();

            tvTypeBadge.setText(item.getTypeLabel());
            switch (item.getType()) {
                case AuditLogItem.TYPE_ENTRY:
                    tvTypeBadge.setBackgroundResource(R.drawable.badge_approved);
                    tvTypeBadge.setTextColor(ctx.getResources().getColor(R.color.approved_text, null));
                    break;
                case AuditLogItem.TYPE_EXIT:
                    tvTypeBadge.setBackgroundResource(R.drawable.badge_rejected);
                    tvTypeBadge.setTextColor(ctx.getResources().getColor(R.color.rejected_text, null));
                    break;
                case AuditLogItem.TYPE_ADHOC_REQUEST:
                    tvTypeBadge.setBackgroundResource(R.drawable.badge_adhoc);
                    tvTypeBadge.setTextColor(ctx.getResources().getColor(R.color.pending_text, null));
                    break;
                case AuditLogItem.TYPE_EMERGENCY:
                    tvTypeBadge.setBackgroundResource(R.drawable.badge_emergency);
                    tvTypeBadge.setTextColor(ctx.getResources().getColor(R.color.emergency_text, null));
                    break;
                default:
                    tvTypeBadge.setBackgroundResource(R.drawable.badge_preapproved);
                    tvTypeBadge.setTextColor(ctx.getResources().getColor(R.color.preapproved_text, null));
            }

            String idPrefix;
            switch (item.getType()) {
                case AuditLogItem.TYPE_ADHOC_REQUEST:
                    idPrefix = ctx.getString(R.string.audit_label_request_id);
                    break;
                case AuditLogItem.TYPE_EMERGENCY:
                    idPrefix = ctx.getString(R.string.audit_label_emergency_id);
                    break;
                default:
                    idPrefix = ctx.getString(R.string.audit_label_pass_id);
            }
            tvEventId.setText(idPrefix + " " + dash(item.getEventId()));

            String namePrefix = (item.getType() == AuditLogItem.TYPE_EMERGENCY)
                    ? ctx.getString(R.string.audit_label_emergency_type)
                    : ctx.getString(R.string.audit_label_visitor);
            tvVisitorOrType.setText(namePrefix + " " + dash(item.getVisitorOrType()));

            String actorName = item.getActorName();
            String actorId   = item.getActorId();
            if (actorName != null && !actorName.isEmpty()) {
                tvActor.setText(ctx.getString(R.string.audit_label_action_by)
                        + " " + actorName + " (" + dash(actorId) + ")");
            } else {
                tvActor.setText(ctx.getString(R.string.audit_label_action_by)
                        + " " + dash(actorId));
            }

            tvTimestamp.setText(formatTime(item.getEventTimeMillis()));

            String detail = item.getExtraDetail();
            if (detail != null && !detail.trim().isEmpty()) {
                tvDetail.setText(ctx.getString(R.string.audit_label_reason) + " " + detail);
                tvDetail.setVisibility(View.VISIBLE);
            } else {
                tvDetail.setVisibility(View.GONE);
            }
        }

        /**
         * Returns "-" if value is null/empty.
         */
        private String dash(String v) {
            return (v == null || v.trim().isEmpty()) ? "-" : v;
        }

        /**
         * Formats timestamp into readable date-time string.
         */
        private String formatTime(long millis) {
            if (millis <= 0) return "-";
            return dateFormatter.format(new Date(millis));
        }
    }
}