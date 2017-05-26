package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * QuakeReport Created by Muir on 26/05/2017.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    // Tag for log messages
    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    // Query URL
    private String Url;

    /**
     * Constructs a new {@link EarthquakeLoader}.
     * @param context of the activity
     * @param url to load data from
     */
    public EarthquakeLoader(Context context, String url){
        super(context);
        Url = url;
    }

    @Override
    protected void onStartLoading(){
        Log.i(LOG_TAG, "TEST: onStartLoading() called...");
        forceLoad();
    }

    // This is on a background thread.
    @Override
    public List<Earthquake> loadInBackground(){

        Log.i(LOG_TAG, "TEST: loadInBackground() called...");

        if (Url == null)
            return null;

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(Url);
        return earthquakes;
    }

}
