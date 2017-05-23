package com.example.android.quakereport;

/**
 * QuakeReport Created by Muir on 23/05/2017.
 */

/**
 * An {@link Earthquake} object contains information related to a single earthquake
 */
public class Earthquake {

    // Magnitude of the earthquake
    private String Magnitude;

    // Location of the earthquake
    private String Location;

    // Date of the earthquake
    private String Date;

    /**
     * Constructs a new {@link Earthquake} object.
     * @param magnitude is the size of the earthquake.
     * @param location is the city where the earthquake happened
     * @param date is when the earthquake happened.
     */

    public Earthquake(String magnitude, String location, String date){

        Magnitude = magnitude;
        Location = location;
        Date = date;

    }

    // Getter methods to return the magnitude, location, and date of the earthquake.

    public String getMagnitude(){
        return Magnitude;
    }

    public String getLocation(){
        return Location;
    }

    public String getDate(){
        return Date;
    }

}
