package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

// Utility class with methods to help perform the HTTP request and parse the response
public final class QueryUtils {

   // Tag for the log messages
   public static final String LOG_TAG = QueryUtils.class.getSimpleName();


    // Returns new URL object from the given string URL
    private static URL createUrl(String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            /*
             * If the request was successful (response code 200), then read the input stream and
             * parse the response.
             */
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null)
                inputStream.close();
        }

        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response from the
     * server.
     * @param inputStream
     * @return output.toString()
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Earthquake} objects that have been built up from
     * parsing a JSON response.
     */
    private static List<Earthquake> extractFeatureFromJson(String earthquakeJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON))
            return null;

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Earthquake> earthquakes = new ArrayList<>();

        /*
        * Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        * is formatted, a JSONException exception object will be thrown.
        * Catch the exception so the app doesn't crash, and print the error message to the logs.
        */
        try {

            // create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);

            /*
            * Extract the JSONArray associated with the key called "features", which represents
            * a list of features (or earthquakes)
             */
            JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");

            /**
             * for each earthquake in the earthquakeArray, create an {@link Earthquake} object.
             */
            for (int i = 0; i < earthquakeArray.length(); i++){

                //Get earthquake JSONObject at position i
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);

                /*
                * For a given earthquake, extract the JSONObject associated with the key called
                * "properties", which represents a list of all properties for that earthquake.
                 */
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                // Extract “mag” for magnitude
                double magnitude = properties.getDouble("mag");

                // Extract “place” for location
                String location = properties.getString("place");

                //  Extract “time” for time
                long time = properties.getLong("time");

                // Extract "url" to get the url
                String url = properties.getString("url");

                /**
                 * Create a new {@link Earthquake} object with the magnitude, location, time, and
                 * url from the JSON response.
                 */
                Earthquake earthquake = new Earthquake(magnitude, location, time, url);

                // Add earthquake to list of earthquakes
                earthquakes.add(earthquake);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

    /**
     * Query the USGS dataset and return a list of {@link Earthquake} objects.
     * @param requestUrl
     * @return earthquakes
     */
    public static List<Earthquake> fetchEarthquakeData(String requestUrl){

        Log.i(LOG_TAG, "TEST: fetchEarthquakeData() called...");
        
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back.
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e){
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        /**
         * Extract relevant fields from the JSON response and create a list of {@link Earthquake}s.
         */

        /**
         * Return the list of {@link Earthquake}s.
         */
        return extractFeatureFromJson(jsonResponse);
    }

}