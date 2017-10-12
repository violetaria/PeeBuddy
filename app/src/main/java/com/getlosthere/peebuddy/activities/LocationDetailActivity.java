package com.getlosthere.peebuddy.activities;

import static com.getlosthere.peebuddy.rest_clients.SessionManager.KEY_API_TOKEN;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getlosthere.peebuddy.R;
import com.getlosthere.peebuddy.models.Location;
import com.getlosthere.peebuddy.models.Rating;
import com.getlosthere.peebuddy.rest_clients.PeeBuddyApiClient;
import com.getlosthere.peebuddy.rest_clients.PeeBuddyApiService;
import com.getlosthere.peebuddy.rest_clients.SessionManager;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationDetailActivity extends AppCompatActivity {
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.btnRate) Button btnRate;
    @BindView(R.id.rbCleanRating) RatingBar rbCleanRating;
    private static final String TAG = "LocationDetailActivity";
    Boolean hasRating = false;
    Location location;
    String markerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        ButterKnife.bind(this);

        location = Parcels.unwrap(getIntent().getParcelableExtra(HomeActivity.EXTRA_LOCATION));
        markerId = getIntent().getStringExtra(HomeActivity.EXTRA_MARKER_ID);

        tvName.setText(location.getName());

        if (location.getMyRating() != null) {
            hasRating = true;
            rbCleanRating.setRating(location.getMyRating().getRating());
        }

        if (hasRating) {
            btnRate.setText("Update Rating");
        } else {
            btnRate.setText("Add Rating");
        }
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                sessionManager.checkLogin();
                PeeBuddyApiService
                        peeBuddyApiService = PeeBuddyApiClient.getClient().create(PeeBuddyApiService.class);
                if(hasRating) {
                    // TODO - do some error handling
                    Rating rating = location.getMyRating();
                    rating.setRating(rbCleanRating.getRating());
                    Call<Location> call = peeBuddyApiService.updateRating(sessionManager.getUserDetails().get(KEY_API_TOKEN), location.getMyRating().getId(), rating.getRating());
                    call.enqueue(new Callback<Location>() {
                        @Override
                        public void onResponse(Call<Location> call, Response<Location> response) {
                            if (response.body() != null) {
                                Toast.makeText(LocationDetailActivity.this, "Rating Succeeded", Toast.LENGTH_LONG).show();
                                returnToMap(response.body(), markerId);
                            } else {
                                String error = response.raw().message();
                                Toast.makeText(LocationDetailActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Location> call, Throwable t) {
                            Log.e(TAG, t.toString());
                        }
                    });
                } else {
                    Call<Location> call = peeBuddyApiService.createRating(sessionManager.getUserDetails().get(KEY_API_TOKEN), location.getId(), rbCleanRating.getRating(), "cleanliness");
                    call.enqueue(new Callback<Location>() {
                        @Override
                        public void onResponse(Call<Location> call, Response<Location> response) {
                            if (response.body() != null) {
                                returnToMap(response.body(), markerId);
                                Toast.makeText(LocationDetailActivity.this, "Rating Succeeded", Toast.LENGTH_LONG).show();
                            } else {
                                String error = response.raw().message();
                                Toast.makeText(LocationDetailActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Location> call, Throwable t) {
                            Log.e(TAG, t.toString());
                        }
                    });
                }
            }
        });
    }

    private void returnToMap(Location updatedLocation, String markerId){
        Intent i = new Intent();
        i.putExtra(HomeActivity.EXTRA_LOCATION, Parcels.wrap(updatedLocation));
        i.putExtra(HomeActivity.EXTRA_MARKER_ID,markerId);
        setResult(HomeActivity.RESULT_OK,i);
        finish();
    }

}
