package com.getlosthere.peebuddy.rest_clients;

import com.getlosthere.peebuddy.models.Location;
import com.getlosthere.peebuddy.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by violetaria on 6/25/17.
 */

public interface PeeBuddyApiService {
    @Headers({
            "Content-Type: application/json"
    })
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("locations")
    Call<List<Location>> getLocations(
            @Header("X-BSS-PeeBuddy") String token,
            @Query("ne_lat") double neLat,
            @Query("ne_lng") double neLng,
            @Query("sw_lat") double swLat,
            @Query("sw_lng") double swLng);

    @POST("users/register")
    @FormUrlEncoded
    Call<User> registerUser(
            @Field("user[email]") String email,
            @Field("user[password]") String password);

    @POST("users/login")
    @FormUrlEncoded
    Call<User> loginUser(
            @Field("email") String email,
            @Field("password") String password);

    @POST("locations")
    @FormUrlEncoded
    Call<Location> createLocation(
            @Header("X-BSS-PeeBuddy") String token,
            @Field("location[place_id]") String placeId,
            @Field("location[name]") String name,
            @Field("location[lat]") Double lat,
            @Field("location[lng]") Double lng);

    @POST("ratings")
    @FormUrlEncoded
    Call<Location> createRating(
            @Header("X-BSS-PeeBuddy") String token,
            @Field("rating[location_id]") Integer locationId,
            @Field("rating[rating]") Float rating,
            @Field("rating[rating_type]") String ratingType);

    @PUT("ratings/{id}")
    @FormUrlEncoded
    Call<Location> updateRating(
            @Header("X-BSS-PeeBuddy") String token,
            @Path("id") Integer id,
            @Field("rating[rating]") Float rating);
}
