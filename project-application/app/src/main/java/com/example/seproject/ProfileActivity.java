package com.example.seproject;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Hosts {@link ProfileFragment} in {@code R.id.profile_container}.
 *
 * @author Moiz Imran
 * @version 1.0
 */
public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profile_container, new ProfileFragment())
                    .commit();
        }
    }
}
