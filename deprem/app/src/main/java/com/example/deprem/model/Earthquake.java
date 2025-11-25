package com.example.deprem.model;

public class Earthquake {
    private String earthquake_id;
    private String title;
    private String date;
    private double mag;
    private double depth;
    private GeoJson geojson;
    private LocationProperties location_properties;
    private String date_time;
    private long created_at;

    public String getEarthquake_id() {
        return earthquake_id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public double getMag() {
        return mag;
    }

    public double getDepth() {
        return depth;
    }

    public GeoJson getGeojson() {
        return geojson;
    }

    public LocationProperties getLocation_properties() {
        return location_properties;
    }

}
