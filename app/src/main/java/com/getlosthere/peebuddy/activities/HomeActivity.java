package com.getlosthere.peebuddy.activities;

import static com.getlosthere.peebuddy.rest_clients.SessionManager.KEY_API_TOKEN;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.getlosthere.peebuddy.R;
import com.getlosthere.peebuddy.adapters.CustomInfoWindowAdapter;
import com.getlosthere.peebuddy.rest_clients.PeeBuddyApiClient;
import com.getlosthere.peebuddy.rest_clients.PeeBuddyApiService;
import com.getlosthere.peebuddy.rest_clients.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "HomeActivity";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    SessionManager sessionManager;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    @BindView(R.id.my_toolbar) Toolbar toolbar;
    @BindView(R.id.nvView) NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    // Google Maps
    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFragment;
    HashMap markerMap = new HashMap<String, com.getlosthere.peebuddy.models.Location>();
    public static final String EXTRA_MARKER_ID = "com.getlosthere.peebuddy.MARKER_ID";
    public static final String EXTRA_LOCATION = "com.getlosthere.peebuddy.LOCATION";
    public static final String EXTRA_MARKER_OPTION = "com.getlosthere.peebuddy.MARKER_OPTION";

    // Location Services
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;  /* 10 secs */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final long EXPIRATION_TIME_IN_MILLISECONDS = 10000; /* 10 secs */
    private final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private final static String LOCATION_KEY = "location-key";
    private final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    private static final int RESULT_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 11;
    private static final int RESULT_REQUEST_CHECK_SETTINGS = 10;
    private static final int RESULT_OPEN_LOCATION_DETAIL = 20;
    private static final int RESULT_LOGIN_REGISTRATION = 30;
    private static final int TWO_MINUTES_IN_MILLISECONDS = 1000 * 60 * 2;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    private Boolean mShouldMoveCamera;
    private String mLastUpdateTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(getApplicationContext());

        setSupportActionBar(toolbar);

        setupDrawerContent(nvDrawer);

        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mRequestingLocationUpdates = true;
        mShouldMoveCamera = true;
        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        setupUserMenu(navigationView);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void setupUserMenu(NavigationView navigationView){
        if(sessionManager.isLoggedIn()){
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        }
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_login:
                sessionManager.checkLogin();
                break;
            case R.id.nav_logout:
                //fragmentClass = FirstFragment.class;
                setupUserMenu(nvDrawer);
                break;
            default:
                break;
        }
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_add:
                try {
                    sessionManager.checkLogin();
                    if(sessionManager.isLoggedIn()) {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    }
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG, e.toString());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, e.toString());
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case PlaceAutocomplete.RESULT_ERROR:
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());
                break;
            case RESULT_CANCELED:
                break;
            case RESULT_OPEN_LOCATION_DETAIL:
                if (data != null){
                    String markerId = data.getStringExtra(EXTRA_MARKER_ID);
                    if (markerId != null) {
                        com.getlosthere.peebuddy.models.Location updatedLocation = Parcels.unwrap(
                                data.getParcelableExtra(EXTRA_LOCATION));
                        markerMap.remove(markerId);
                        placeMarker(updatedLocation);
                    }
                }
                break;
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                Place place = PlaceAutocomplete.getPlace(this, data);
                if(place == null) {
                    break;
                }
                PeeBuddyApiService
                        peeBuddyApiService = PeeBuddyApiClient.getClient().create(PeeBuddyApiService.class);

                Call<com.getlosthere.peebuddy.models.Location> call = peeBuddyApiService.createLocation(
                        sessionManager.getUserDetails().get(KEY_API_TOKEN),
                        place.getId(),
                        place.getName().toString(),
                        place.getLatLng().latitude,
                        place.getLatLng().longitude);
                call.enqueue(new Callback<com.getlosthere.peebuddy.models.Location>() {
                    @Override
                    public void onResponse(Call<com.getlosthere.peebuddy.models.Location> call, Response<com.getlosthere.peebuddy.models.Location> response) {
                        com.getlosthere.peebuddy.models.Location location = response.body();
                        if(location != null) {
                            Marker m = placeMarker(location);
                            Intent i = new Intent(HomeActivity.this, LocationDetailActivity.class);
                            i.putExtra(EXTRA_LOCATION, Parcels.wrap(location));
                            i.putExtra(EXTRA_MARKER_ID, m.getId());
                            startActivityForResult(i, RESULT_OPEN_LOCATION_DETAIL);
                        }
                    }

                    @Override
                    public void onFailure(Call<com.getlosthere.peebuddy.models.Location> call, Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });

                Log.i(TAG, "Place: " + place.getName());
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (mCurrentLocation != null){
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 12.0f));
        }
        mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(HomeActivity.this));
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
            @Override
            public void onInfoWindowClick(Marker marker) {
                com.getlosthere.peebuddy.models.Location location = (com.getlosthere.peebuddy.models.Location) markerMap.get(marker.getId());
                Intent i = new Intent(HomeActivity.this, LocationDetailActivity.class);
                i.putExtra(EXTRA_LOCATION, Parcels.wrap(location));
                i.putExtra(EXTRA_MARKER_ID, marker.getId());
                marker.remove();
                startActivityForResult(i, RESULT_OPEN_LOCATION_DETAIL);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showExplanationDialog("Hey there!", "We need your location to, like, show you where to pee man", Manifest.permission.ACCESS_FINE_LOCATION, RESULT_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        if (mCurrentLocation == null) {
            updateUI();
        }

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged");
        if(isBetterLocation(location, mCurrentLocation)) {
            mCurrentLocation = location;
            // TODO figure out if we want to update UI here or not
            updateUI();
            Log.d(TAG, "updateUI in onLocationChanged");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }


    private void clearUI() {
        mGoogleMap.clear();
        markerMap.clear();
    }

    private void updateUI() {
        Log.d(TAG, "updateUI called");
        if (mCurrentLocation == null) return;
        if (mGoogleMap == null) return;
        if (mShouldMoveCamera) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 12.0f));
            mShouldMoveCamera = false;
        }
        clearUI();
        populateLocations();
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {

        if (currentBestLocation == null) {
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES_IN_MILLISECONDS;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES_IN_MILLISECONDS;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        Log.d(TAG,"isBetterLocation - accuracyDelta = " + accuracyDelta);

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, HomeActivity.this);
                    mGoogleMap.setMyLocationEnabled(true);
                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(HomeActivity.this, RESULT_REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    protected void stopLocationUpdates() {
        Log.i(TAG, "stopLocationUpdates");
        if(mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RESULT_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        startLocationUpdates();
                    }
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        mRequestingLocationUpdates = false;
                        Toast.makeText(HomeActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    } else {
                        showExplanationDialog("Hey there!", "We need your location to show you where to pee man", Manifest.permission.ACCESS_FINE_LOCATION, RESULT_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                }
                break;
        }
    }

    private void showExplanationDialog(String title, String message, final String permission, final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permissionName}, permissionRequestCode);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setExpirationDuration(EXPIRATION_TIME_IN_MILLISECONDS);
    }

    private void populateLocations(){
        LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;

        PeeBuddyApiService
                peeBuddyApiService = PeeBuddyApiClient.getClient().create(PeeBuddyApiService.class);

        Call<List<com.getlosthere.peebuddy.models.Location>> call = peeBuddyApiService.getLocations(
                sessionManager.getUserDetails().get(KEY_API_TOKEN),
                bounds.northeast.latitude,
                bounds.northeast.longitude,
                bounds.southwest.latitude,
                bounds.southwest.longitude);
        call.enqueue(new Callback<List<com.getlosthere.peebuddy.models.Location>>() {
            @Override
            public void onResponse(Call<List<com.getlosthere.peebuddy.models.Location>> call, Response<List<com.getlosthere.peebuddy.models.Location>> response) {
                List<com.getlosthere.peebuddy.models.Location> locations = response.body();
                for (com.getlosthere.peebuddy.models.Location location : locations) {
                    placeMarker(location);
                }
                Log.d(TAG, "Number of locations received: " + locations.size());
            }

            @Override
            public void onFailure(Call<List<com.getlosthere.peebuddy.models.Location>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private Marker placeMarker(com.getlosthere.peebuddy.models.Location location){
        LatLng latLng = new LatLng(location.getLatlng().getLat(), location.getLatlng().getLng());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(location.getName());
        markerOptions.snippet("Rating: " + location.getAverageRating() + "(" + location.getRatingCount().toString() + ")");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        Marker m = mGoogleMap.addMarker(markerOptions);
        markerMap.put(m.getId(), location);
        return m;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPlayServicesAvailable(this);
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public static boolean isPlayServicesAvailable(Context context) {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog((Activity) context, resultCode, 2).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
            }
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }
}
