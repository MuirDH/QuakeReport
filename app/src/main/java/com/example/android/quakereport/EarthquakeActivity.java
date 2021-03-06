/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.quakereport.QueryUtils.LOG_TAG;

public class EarthquakeActivity extends AppCompatActivity
        implements LoaderCallbacks<List<Earthquake>> {

    private static final String USGS_REQUST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query";
    /*
     * constant value for the earthquake loader ID. We can choose any integer. This really only
     * comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;
    // Adapter for the list of earthquakes
    private EarthquakeAdapter Adapter;

    // TextView that is displayed when the list is empty
    private TextView EmptyStateTextView;

    private ProgressBar LoadingProgressSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "TEST: Earthquake Activity onCreate() called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        /**
         * Find a reference to the {@link ListView} in the layout
         */
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        EmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        LoadingProgressSpinner = (ProgressBar) findViewById(R.id.loading_spinner);

        // Create a new adapter that takes an empty list of earthquakes as input
        Adapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        /**
         * Set the adapter on the {@link ListView}
         so the list can be populated in the user interface
         */

        if (earthquakeListView != null) {
            earthquakeListView.setAdapter(Adapter);
        }

        /*
        * Set an item click listener on the ListView, which sends an intent to a web browser to
        * open a website with more information about the selected earthquake.
         */
        if (earthquakeListView != null) {
            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    // find the current earthquake that was clicked on
                    Earthquake currentEarthquake = Adapter.getItem(position);

                    // convert the String URL into a URI object (to pass into the Intent constructor)

                    Uri earthquakeUri = null;
                    if (currentEarthquake != null) {
                        earthquakeUri = Uri.parse(currentEarthquake.getUrl());
                    }

                    // Create a new intent to view the earthquake URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                }
            });
        }

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            LoaderManager loaderManager = getLoaderManager();
            // Get a reference to the LoaderManager, in order to interact with loaders.LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible

            LoadingProgressSpinner.setVisibility(View.GONE);

            // Update empty state with no connection error message
            EmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actions_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public android.content.Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        Log.i(LOG_TAG, "TEST: onCreateLoader() called...");

        // Read the user's latest preferences for the minimum magnitude
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPreferences.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        //Read from SharedPreferences and check for the value associated with the key:
        //getString(R.string.settings_order_by_key). When building the URI and appending query
        // parameters, instead of hardcoding the "orderby" parameter to be "time", we will use the
        // user's preference (stored in the orderBy variable
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUST_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(android.content.Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {

        Log.i(LOG_TAG, "TEST: onLoadFinished() called...");

        LoadingProgressSpinner.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
        Adapter.clear();

        /**
         * If there is a valid list of {@link Earthquake}s, then add them to the adapter's data
         * set. This will trigger the ListView to update. If there aren't any earthquakes to
         * display, then set empty state text to display "no earthquakes found"
         */
        if (earthquakes != null && !earthquakes.isEmpty()) {
            Adapter.addAll(earthquakes);
        } else EmptyStateTextView.setText(R.string.no_earthquakes_found);

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

        Log.i(LOG_TAG, "TEST: onLoaderReset() called...");

        // Loader reset, so we can clear out our existing data.
        Adapter.clear();

    }

}
