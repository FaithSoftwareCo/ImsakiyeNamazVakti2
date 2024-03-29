package com.faith.namazvakti.activities.preferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.faith.namazvakti.R;
import com.faith.namazvakti.fragments.preferences.PreferencesFragment;

public class PreferencesActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        PreferencesFragment preferencesFragment = new PreferencesFragment();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_preferencesContainer, preferencesFragment)
                .commit();
    }
}
