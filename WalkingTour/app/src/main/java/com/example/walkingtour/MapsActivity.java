package com.example.walkingtour;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.walkingtour.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private TextView addressLabel;
    private CheckBox addressCheck;
    private CheckBox geoCheck;
    private CheckBox travelCheck;
    private CheckBox tourCheck;

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;

    private static final int LOC_COMBO_REQUEST = 111;
    private static final int LOC_ONLY_PERM_REQUEST = 222;
    private static final int BGLOC_ONLY_PERM_REQUEST = 333;
    private static final int ACCURACY_REQUEST = 444;

    private final ArrayList<LatLng> latLonHistory = new ArrayList<>();
    private final ArrayList<LatLng> latLonTour = new ArrayList<>();

    private Polyline llHistoryPolyline;
    private Polyline llTourPolyline;

    private boolean zooming = false;
    private float oldZoom;

    private Marker personMarker;

    private LocationManager locationManager;
    private LocationListener locationListener;

    public static int screenHeight;
    public static int screenWidth;

    private FenceMgr fenceMgr;
    private final List<PatternItem> pattern = Collections.singletonList(new Dot());

    private Boolean travelIsChecked = true;
    private Boolean tourIsChecked = true;

    private Geocoder geocoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        addressLabel = findViewById(R.id.addressLabel);
        addressCheck = findViewById(R.id.addressesBox);
        geoCheck = findViewById(R.id.geofencesBox);
        travelCheck = findViewById(R.id.travelPathBox);
        tourCheck = findViewById(R.id.tourPathBox);
        geocoder = new Geocoder(this);
        addressOnCheck();
        geofenceOnCheck();
        travelPathOnCheck();
        tourPathOnCheck();
        getScreenDimensions();
        checkLocationAccuracy();
    }

    public void initMap() {

        fenceMgr = new FenceMgr(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        zooming = true;

        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        if (checkPermission()) {
            setupLocationListener();
            setupZoomListener();
//            fenceMgr.makeFences();
        }
    }

    public void addressOnCheck()
    {
        addressCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if(isChecked)
                    {
                        addressLabel.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        addressLabel.setVisibility(View.GONE);
                    }
               }
           }
        );
    }

    public void geofenceOnCheck()
    {
        geoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if(isChecked)
                    {
                        fenceMgr.drawFences();
                    }
                    else
                    {
                        fenceMgr.eraseFences();
                    }
                }
            }
        );
    }

    public void travelPathOnCheck()
    {
        travelCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if(isChecked)
                    {
                       travelIsChecked = true;
                        llHistoryPolyline.setVisible(true);
                    }
                    else
                    {
                        travelIsChecked = false;
                        hideTravelPolyline();
                    }
                }
            }
        );
    }

    public void tourPathOnCheck()
    {
        tourCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if(isChecked)
                    {
                        tourIsChecked = true;
                        showTourPolyline();
                    }
                    else
                    {
                        tourIsChecked = false;
                        hideTourPolyLine();
                    }
                }
            }
        );
    }

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private boolean checkPermission() {

        // If R or greater, need to ask for these separately
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_ONLY_PERM_REQUEST);
                return false;
            }
            return true;

        } else {

            ArrayList<String> perms = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                perms.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    perms.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                }
            }

            if (!perms.isEmpty()) {
                String[] array = perms.toArray(new String[0]);
                ActivityCompat.requestPermissions(this,
                        array, LOC_COMBO_REQUEST);
                return false;
            }
        }

        return true;
    }

    public void requestBgPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BGLOC_ONLY_PERM_REQUEST);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOC_ONLY_PERM_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestBgPermission();
            }
        } else if (requestCode == LOC_COMBO_REQUEST) {
            int permCount = permissions.length;
            int permSum = 0;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    permSum++;
                } else {
                    sb.append(permissions[i]).append(", ");
                }
            }
            if (permSum == permCount) {
            } else {
                Toast.makeText(this,
                        "Required permissions not granted: " + sb.toString(),
                        Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == BGLOC_ONLY_PERM_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    private void setupLocationListener() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocListener(this);

        //minTime	    long: minimum time interval between location updates, in milliseconds
        //minDistance	float: minimum distance between location updates, in meters
        if (checkPermission() && locationManager != null) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        }

    }

    /*
    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null && locationListener != null)
            locationManager.removeUpdates(locationListener);
    }

     */

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission() && locationManager != null && locationListener != null)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }

    private void setupZoomListener() {
        mMap.setOnCameraIdleListener(() -> {
            if (zooming) {
                Log.d(TAG, "onCameraIdle: DONE ZOOMING: " + mMap.getCameraPosition().zoom);
                zooming = false;
                oldZoom = mMap.getCameraPosition().zoom;
            }
        });

        mMap.setOnCameraMoveListener(() -> {
            if (mMap.getCameraPosition().zoom != oldZoom) {
                Log.d(TAG, "onCameraMove: ZOOMING: " + mMap.getCameraPosition().zoom);
                zooming = true;
            }
        });
    }

    public void doLatLon(Location curr)
    {

        try{
            List<Address> addresses;
            addresses = geocoder.getFromLocation(curr.getLatitude(), curr.getLongitude(), 1);
            displayAddress(addresses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayAddress(List<Address> addresses)
    {
        if(addresses.size() == 0)
        {
            addressLabel.setText("No Location Found");
            return;
        }
        Address ad;
        ad = addresses.get(0);
        String userLoc = ad.getAddressLine(0);
        addressLabel.setText(userLoc);
    }

    private void showTravelPolyline(LatLng latLng)
    {

        if (llHistoryPolyline != null) {
            llHistoryPolyline.remove(); // Remove old polyline
        }

        if (latLonHistory.size() == 1) { // First update
            mMap.addMarker(new MarkerOptions().alpha(0.5f).position(latLng).title("My Origin"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            zooming = true;
            return;
        }

        if (latLonHistory.size() > 1) { // Second (or more) update
            PolylineOptions polylineOptions = new PolylineOptions();

            for (LatLng ll : latLonHistory) {
                polylineOptions.add(ll);
            }
            llHistoryPolyline = mMap.addPolyline(polylineOptions);
            llHistoryPolyline.setEndCap(new RoundCap());
            llHistoryPolyline.setWidth(12);
            llHistoryPolyline.setColor(Color.parseColor("#00703e"));
            llHistoryPolyline.setVisible(true);
        }
    }

    public void hideTravelPolyline()
    {
        llHistoryPolyline.setVisible(false);
    }

    void createTourPolyline(ArrayList<LatLng> latLonTour)
    {
        if(latLonTour.size() > 1) {
            PolylineOptions polyline = new PolylineOptions();

            for (LatLng ll : latLonTour) {
                polyline.add(ll);
            }
            llTourPolyline = mMap.addPolyline(polyline);
            llTourPolyline.setEndCap(new RoundCap());
            llTourPolyline.setWidth(12);
            llTourPolyline.setColor(Color.parseColor("#FFD700"));
        }
    }

    public void showTourPolyline()
    {
        if(llTourPolyline != null) {
            llTourPolyline.setVisible(true);
        }
    }

    public void hideTourPolyLine()
    {
        if(llTourPolyline != null) {
            llTourPolyline.setVisible(false);
        }
    }

    public void updateLocation(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        latLonHistory.add(latLng); // Add the LL to our location history

        if(travelIsChecked)
        {
            showTravelPolyline(latLng);
        }
        else
        {
            hideTravelPolyline();
        }

        if(tourIsChecked)
        {
            showTourPolyline();
        }
        else
        {
            hideTourPolyLine();
        }

        if (latLonHistory.size() > 1) {

            doLatLon(location);

            float r = getRadius();
            if (r > 0) {
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.walker_left);
                Bitmap resized = Bitmap.createScaledBitmap(icon, (int) r, (int) r, false);
                BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromBitmap(resized);

                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.icon(iconBitmap);
                options.rotation(location.getBearing());

                if (personMarker != null) {
                    personMarker.remove();
                }

                personMarker = mMap.addMarker(options);
            }
        }

        if (!zooming)
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));


    }

    public GoogleMap getMap() {
        return mMap;
    }

    private float getRadius() {
        float z = mMap.getCameraPosition().zoom;
        float factor = (float) ((35.0 / 2.0 * z) - (355.0 / 2.0));
        float multiplier = ((7.0f / 7200.0f) * screenWidth) - (1.0f / 20.0f);
        float radius = factor * multiplier;
        return radius;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null)
            locationManager.removeUpdates(locationListener);
    }

    private void checkLocationAccuracy() {

        Log.d(TAG, "checkLocationAccuracy: ");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {
            Log.d(TAG, "onSuccess: High Accuracy Already Present");
            initMap();
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(MapsActivity.this, ACCURACY_REQUEST);
                } catch (IntentSender.SendIntentException sendEx) {
                    sendEx.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCURACY_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: ");
            initMap();
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("High-Accuracy Location Services Required");
            builder.setMessage("High-Accuracy Location Services Required");
            builder.setPositiveButton("OK", (dialog, id) -> finish());
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }
}