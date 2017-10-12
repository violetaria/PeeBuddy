package com.getlosthere.peebuddy.rest_clients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by violetaria on 6/25/17.
 */

public class PeeBuddyApiClient {
    private static final String BASE_URL = "http://peebuddyapi.herokuapp.com/";
    private static Retrofit retrofit = null;
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                                    .baseUrl(BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create(gson))
                                    .build();
        }
        return retrofit;
    }
}
