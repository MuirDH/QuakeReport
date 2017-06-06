package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * QuakeReport Created by Muir on 06/06/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            // Use the findPreference() method to get the Preference object and setup the preference
            // using a helper method called bindPreferenceSummaryToValue()

            Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMagnitude);

        }


        /**
         * Define the bindPreferenceSummaryToValue() helper method to set the current
         * EarthquakePreferenceFragment instance as the listener on each preference. Read the
         * current value of the preference stored in the SharedPreferences on the device, and
         * display that in the preference summary (so that the user can see the current value of
         * the preference
         *
         * @param preference is the user's preferred value setting
         */
        private void bindPreferenceSummaryToValue(Preference preference) {

            preference.setOnPreferenceChangeListener(this);

            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            String preferenceString = preferences.getString(preference.getKey(), "");

            onPreferenceChange(preference, preferenceString);
        }

        /**
         * Updates the displayed preference summary after it has been changed
         *
         * @param preference is the user's preference
         * @param value      is the value of the preference
         * @return true
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String stringValue = value.toString();
            preference.setSummary(stringValue);

            return true;
        }

    }

}
