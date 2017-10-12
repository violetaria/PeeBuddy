package com.getlosthere.peebuddy.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by violetaria on 6/27/17.
 */

@org.parceler.Parcel
public class LatLng{

    @SerializedName("lat")
    @Expose
    Double lat;
    @SerializedName("lng")
    @Expose
    Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}