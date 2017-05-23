package com.example.android.quakereport;

/**
 * QuakeReport Created by Muir on 23/05/2017.
 */

/**
 * An {@link Earthquake} object contains information related to a single earthquake
 */
public class Earthquake {

    // Magnitude of the earthquake
    private double Magnitude;

    // Location of the earthquake
    private String Location;

    // Date of the earthquake
    private long TimeInMilliseconds;

    // Website URL of the earthquake
    private  String Url;

    /**
     * Constructs a new {@link Earthquake} object.
     * @param magnitude is the size of the earthquake.
     * @param location is the city where the earthquake happened
     * @param timeInMilliseconds is when the earthquake happened.
     * @param url is the website URL to find more details about the earthquake
     */

    public Earthquake(double magnitude, String location, long timeInMilliseconds, String url){

        Magnitude = magnitude;
        Location = location;
        TimeInMilliseconds = timeInMilliseconds;
        Url = url;

    }

    // Getter methods to return the magnitude, location, and date of the earthquake.

    public double getMagnitude(){
        return Magnitude;
    }

    public String getLocation(){
        return Location;
    }

    public long getTimeInMilliseconds(){
        return TimeInMilliseconds;
    }

    public String getUrl(){
        return Url;
    }

}
