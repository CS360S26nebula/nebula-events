package com.example.seproject.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.seproject.R;

import java.util.Calendar;

/**
 * Admin UI to configure restricted entry time window (placeholder data binding).
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class RestrictEntryAdminFragment extends Fragment {

    public RestrictEntryAdminFragment() {
        super(R.layout.restrict_entry_admin_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        bindSpinner((Spinner) view.findViewById(R.id.sp_role), new String[]{"Select user role", "Faculty", "Guard"});
        String[] date = buildRange("Date", 1, 31);
        String[] month = new String[]{"Month", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] year = buildRange("Year", 2024, currentYear);
        bindSpinner((Spinner) view.findViewById(R.id.sp_start_date), date);
        bindSpinner((Spinner) view.findViewById(R.id.sp_start_month), month);
        bindSpinner((Spinner) view.findViewById(R.id.sp_start_year), year);
        bindSpinner((Spinner) view.findViewById(R.id.sp_end_date), date);
        bindSpinner((Spinner) view.findViewById(R.id.sp_end_month), month);
        bindSpinner((Spinner) view.findViewById(R.id.sp_end_year), year);
        String[] times = new String[]{"Time", "12:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00"};
        String[] meridiem = new String[]{"AM/PM", "AM", "PM"};
        bindSpinner((Spinner) view.findViewById(R.id.sp_start_time), times);
        bindSpinner((Spinner) view.findViewById(R.id.sp_start_ampm), meridiem);
        bindSpinner((Spinner) view.findViewById(R.id.sp_end_time), times);
        bindSpinner((Spinner) view.findViewById(R.id.sp_end_ampm), meridiem);
    }

    private void bindSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_admin, items) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView text = v.findViewById(android.R.id.text1);
                text.setTextColor(position == 0 ? 0xFF999999 : 0xFF1E1E1E);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                TextView text = v.findViewById(android.R.id.text1);
                text.setTextColor(position == 0 ? 0xFF999999 : 0xFF1E1E1E);
                return v;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_admin);
        spinner.setAdapter(adapter);
    }

    private String[] buildRange(String head, int start, int end) {
        String[] values = new String[(end - start + 2)];
        values[0] = head;
        for (int i = start; i <= end; i++) values[i - start + 1] = String.valueOf(i);
        return values;
    }
}
