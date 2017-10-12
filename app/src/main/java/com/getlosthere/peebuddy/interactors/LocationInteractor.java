package com.getlosthere.peebuddy.interactors;

import com.getlosthere.peebuddy.models.Location;

import java.util.List;

import retrofit2.Call;

/**
 * Created by violetaria on 10/1/17.
 */

public interface LocationInteractor {
    Call<List<Location>> getLocations(String token, double neLat, double neLng, double swLat, double swLng);
}
