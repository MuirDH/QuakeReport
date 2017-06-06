package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
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

            // Add additional logic so that the EarthquakePreferenceFragment is aware of the new
            // ListPreference. Find the "order by" Preference object according to its key. Call
            // the bindPreferenceSummaryToValue() helper method on this object, which will set this
            // fragment as the OnPreferenceChangeListener and update the summary so that it displays
            // the current value stored in SharedPreferences
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

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

            //Update the onPreferenceChange() method to properly update the summary of a
            //ListPreference (using the label, instead of the key).
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);

                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }

            return true;
        }

    }

}
