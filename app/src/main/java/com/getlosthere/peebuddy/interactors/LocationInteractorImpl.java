package com.getlosthere.peebuddy.interactors;

import com.getlosthere.peebuddy.models.Location;
import com.getlosthere.peebuddy.rest_clients.PeeBuddyApiClient;
import com.getlosthere.peebuddy.rest_clients.PeeBuddyApiService;

import java.util.List;

import retrofit2.Call;

/**
 * Created by violetaria on 10/1/17.
 */

public class LocationInteractorImpl implements LocationInteractor {
    private PeeBuddyApiService peeBuddyApiService;

    public LocationInteractorImpl(){
        peeBuddyApiService = PeeBuddyApiClient.getClient().create(PeeBuddyApiService.class);
    }

    @Override
    public Call<List<Location>> getLocations(String token, double neLat, double neLng, double swLat, double swLng){
        return peeBuddyApiService.getLocations(token, neLat, neLng, swLat, swLng);
    }
}
