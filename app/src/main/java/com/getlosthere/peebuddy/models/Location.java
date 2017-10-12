package com.getlosthere.peebuddy.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by violetaria on 6/20/17.
 */

@org.parceler.Parcel
public class Location {
    @SerializedName("id")
    @Expose
    Integer id;
    @SerializedName("place_id")
    @Expose
    String placeId;
    @SerializedName("latlng")
    @Expose
    LatLng latLng;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("average_rating")
    @Expose
    Double averageRating;
    @SerializedName("created_at")
    @Expose
    String createdAt;
    @SerializedName("created_by")
    @Expose
    String createdBy;
    @SerializedName("updated_at")
    @Expose
    String updatedAt;
    @SerializedName("rating_count")
    @Expose
    Integer ratingCount;
    @SerializedName("my_rating")
    @Expose
    Rating myRating;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public LatLng getLatlng() {
        return latLng;
    }

    public void setLatlng(LatLng latlng) {
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Rating getMyRating() {
        return myRating;
    }
    public void setMyRating(Rating rating) {
        this.myRating = rating;
    }
}
