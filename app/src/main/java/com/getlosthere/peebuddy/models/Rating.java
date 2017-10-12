package com.getlosthere.peebuddy.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
//import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by violetaria on 7/23/17.
 */
@org.parceler.Parcel
public class Rating {

    @SerializedName("id")
    @Expose
    Integer id;
    @SerializedName("rating")
    @Expose
    Float rating;
    @SerializedName("rating_type")
    @Expose
    String ratingType;
    @SerializedName("created_at")
    @Expose
     String createdAt;
    @SerializedName("updated_at")
    @Expose
    String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getRatingType() {
        return ratingType;
    }

    public void setRatingType(String ratingType) {
        this.ratingType = ratingType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this);
//    }
}
