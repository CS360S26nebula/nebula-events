package com.example.seproject;

import android.text.TextUtils;
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
import java.util.Map;

/**
 * RecyclerView adapter for displaying blacklisted individual records.
 *
 * @author Yahya Ali
 * @version 1.0
 */

public class BlacklistedIndividualsAdapter extends RecyclerView.Adapter<BlacklistedIndividualsAdapter.ViewHolder> {

    public interface ActionListener {
        void onRemove(@NonNull String documentId);
    }

    private final List<Map<String, Object>> items = new ArrayList<>();
    private final List<Map<String, Object>> allItems = new ArrayList<>();
    private final List<String> documentIds = new ArrayList<>();
    private final List<String> allDocumentIds = new ArrayList<>();
    private final ActionListener actionListener;

    public BlacklistedIndividualsAdapter(@NonNull List<Map<String, Object>> initialItems,
                                         @NonNull List<String> initialDocumentIds,
                                         @NonNull ActionListener actionListener) {
        /**
         * Creates the adapter with initial blacklist records and a remove action listener.
         *
         * @param initialItems       starting blacklist records
         * @param initialDocumentIds Firestore document ids for the records
         * @param actionListener     listener used when remove is pressed
         */

        this.actionListener = actionListener;

        items.addAll(initialItems);
        allItems.addAll(initialItems);
        documentIds.addAll(initialDocumentIds);
        allDocumentIds.addAll(initialDocumentIds);
    }

    public void setItems(@NonNull List<Map<String, Object>> newItems,
                         @NonNull List<String> newDocumentIds) {
        /**
         * Replaces the current blacklist records and refreshes the list.
         *
         * @param newItems       new blacklist records
         * @param newDocumentIds Firestore document ids for the new records
         */

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
        /**
         * Filters blacklist records by name, CNIC, normalized CNIC, person type, or reason.
         *
         * @param query search text entered by the user
         */

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
            Map<String, Object> item = allItems.get(i);
            String documentId = allDocumentIds.get(i);

            String fullName = value(item, "fullName").toLowerCase();
            String cnicNumber = value(item, "cnicNumber").toLowerCase();
            String normalizedCnic = value(item, "normalizedCnic").toLowerCase();
            String personType = value(item, "personType").toLowerCase();
            String reason = value(item, "blacklistReason").toLowerCase();

            if (fullName.contains(searchText)
                    || cnicNumber.contains(searchText)
                    || normalizedCnic.contains(searchText)
                    || personType.contains(searchText)
                    || reason.contains(searchText)) {
                items.add(item);
                documentIds.add(documentId);
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /**
         * Creates a card view for one blacklisted individual.
         *
         * @param parent   parent RecyclerView
         * @param viewType item view type
         * @return new view holder for the blacklist card
         */

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blacklisted_individual_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /**
         * Binds one blacklist record to the card view.
         *
         * @param holder   card view holder
         * @param position position of the record in the list
         */

        Map<String, Object> item = items.get(position);
        String documentId = documentIds.get(position);

        holder.tvName.setText(dashIfEmpty(value(item, "fullName")));
        holder.tvCnic.setText(dashIfEmpty(value(item, "cnicNumber")));
        holder.tvPersonType.setText(dashIfEmpty(value(item, "personType")));
        holder.tvReason.setText(dashIfEmpty(value(item, "blacklistReason")));
        holder.tvStartTime.setText(formatMillis(item.get("blacklistStartTimeMilliseconds")));
        holder.tvEndTime.setText(formatMillis(item.get("blacklistEndTimeMilliseconds")));

        holder.btnRemove.setOnClickListener(v -> actionListener.onRemove(documentId));
    }

    @Override
    public int getItemCount() {
        /**
         * @return number of blacklist records currently shown
         */

        return items.size();
    }

    private static String value(Map<String, Object> item, String key) {
        /**
         * Safely reads a string value from a blacklist record map.
         *
         * @param item blacklist record map
         * @param key  field name to read
         * @return field value as string, or empty string if missing
         */

        Object value = item.get(key);

        if (value == null) {
            return "";
        }

        return String.valueOf(value);
    }

    private static String dashIfEmpty(String value) {
        /**
         * Converts empty text into a dash for display.
         *
         * @param value text to check
         * @return original text or dash if empty
         */

        if (value == null || value.trim().isEmpty()) {
            return "-";
        }

        return value;
    }

    private static String formatMillis(Object value) {
        /**
         * Formats a millisecond timestamp for display.
         *
         * @param value timestamp object from Firestore
         * @return formatted date/time or dash if invalid
         */

        if (!(value instanceof Number)) {
            return "-";
        }

        long millis = ((Number) value).longValue();

        if (millis <= 0) {
            return "-";
        }

        return new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.ENGLISH)
                .format(new Date(millis));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvCnic;
        final TextView tvPersonType;
        final TextView tvReason;
        final TextView tvStartTime;
        final TextView tvEndTime;
        final MaterialButton btnRemove;

        ViewHolder(@NonNull View itemView) {
            /**
             * Holds references to views inside one blacklist card.
             *
             * @param itemView blacklist card view
             */

            super(itemView);

            tvName = itemView.findViewById(R.id.tv_blacklisted_name);
            tvCnic = itemView.findViewById(R.id.tv_blacklisted_cnic);
            tvPersonType = itemView.findViewById(R.id.tv_blacklisted_person_type);
            tvReason = itemView.findViewById(R.id.tv_blacklisted_reason);
            tvStartTime = itemView.findViewById(R.id.tv_blacklisted_start_time);
            tvEndTime = itemView.findViewById(R.id.tv_blacklisted_end_time);
            btnRemove = itemView.findViewById(R.id.btn_remove_blacklisted);
        }
    }
}