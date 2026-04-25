package com.example.seproject;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CreateAdhocEntry extends CreateVisitorEntry {

    public CreateAdhocEntry() {
        super();
    }

    // This overrides the "Approved" default in the base class
    @Override
    public String getRequestStatus() {
        return CreateVisitorEntryRules.statusForAdhocEntry();
    }

    // Adhoc entries shouldn't have a PassID until the faculty approves
    @Override
    protected String getPassId() {
        return "";
    }

    @Override
    protected boolean isAdhocEntry() {
        return true;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (btnSubmitRequest != null) {
            btnSubmitRequest.setText("Submit for Approval");
        }
    }
}