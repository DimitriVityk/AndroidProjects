package com.example.knowyourgov;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.location.Geocoder;
import android.location.Address;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private final List<Official> officialList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GovAdapter govAdapter;
    private String searchLocation;
    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    private LocationManager locationManager;
    private Criteria criteria;
    private String displayLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();

        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
        } else {
            setLocation();
        }

        recyclerView = findViewById(R.id.govRecycler);
        govAdapter = new GovAdapter(officialList, this);
        recyclerView.setAdapter(govAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Content Cannot Be Updated Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

    }

    private void badDataAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("No data");
        builder.setTitle("Info Not Found: " );

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addOfficial(Official off)
    {
        if(off == null)
        {
            badDataAlert();
            return;
        }

        officialList.add(off);
        govAdapter.notifyDataSetChanged();
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void doDownload(String location)
    {
        OfficialDownloader od = new OfficialDownloader(this, location);
        new Thread(od).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
                return;
            }
        }
        ((TextView) findViewById(R.id.userLocation)).setText("No Permission");

    }

    @SuppressLint("MissingPermission")
    private void setLocation() {
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location currentLocation = null;
        if (bestProvider != null) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        }
        if(currentLocation != null)
        {
            doLatLon(currentLocation);
        }
        else
        {
            ((TextView) findViewById(R.id.userLocation)).setText("Location Unavailable");
        }
    }

//    public void recheckLocation(View v) {
//        setLocation();
//    }

    public void doLatLon(Location curr)
    {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try{
            List<Address> addresses;
            addresses = geocoder.getFromLocation(curr.getLatitude(), curr.getLongitude(), 1);
            displayAddress(addresses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doLocationName(String location){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses;
            addresses = geocoder.getFromLocationName(location, 1);
            displayAddressForSearch(addresses, location);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void displayAddress(List<Address> addresses)
    {
        if(addresses.size() == 0)
        {
            ((TextView) findViewById(R.id.userLocation)).setText("No Location Found");
            return;
        }
        officialList.clear();
        Address ad;
        ad = addresses.get(0);
        String userLoc = String.format("%s, %s %s", (ad.getLocality() == null?"":ad.getLocality()), (ad.getAdminArea()==null?"":ad.getAdminArea()), (ad.getPostalCode()==null?"":ad.getPostalCode()));
        displayLocation = userLoc;
        doDownload(ad.getPostalCode());
        ((TextView) findViewById(R.id.userLocation)).setText(userLoc);
    }

    private void displayAddressForSearch(List<Address> addresses, String search)
    {
        if(addresses.size() == 0)
        {
            ((TextView) findViewById(R.id.userLocation)).setText("No Location Found");
            return;
        }
        officialList.clear();
        Address ad;
        ad = addresses.get(0);
        String userLoc = String.format("%s, %s %s", (ad.getLocality() == null?"":ad.getLocality()), (ad.getAdminArea()==null?"":ad.getAdminArea()), (ad.getPostalCode()==null?"":ad.getPostalCode()));
        displayLocation = userLoc;
        doDownload(search);
        ((TextView) findViewById(R.id.userLocation)).setText(userLoc);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menuAbout:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            case R.id.menuSearch:
                createSearchDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createSearchDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                searchLocation = et.getText().toString().trim();
                doLocationName(searchLocation);
                }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Please enter a City, State or a Zip Code:");
        builder.setTitle("Location Search");
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        Official clickOfficial = officialList.get(pos);
        Intent officialIntent = new Intent(this, OfficialActivity.class);
        officialIntent.putExtra("currentLocation", displayLocation);
        officialIntent.putExtra("officialName", clickOfficial.getName());
        officialIntent.putExtra("officialParty", clickOfficial.getParty());
        officialIntent.putExtra("officialTitle", clickOfficial.getOffice());
        officialIntent.putExtra("officialImage", clickOfficial.getPhotoURL());
        officialIntent.putExtra("officialAddress", clickOfficial.getOfficeAddress());
        officialIntent.putExtra("officialPhone", clickOfficial.getPhoneNumber());
        officialIntent.putExtra("officialEmail", (String) clickOfficial.getEmail());
        officialIntent.putExtra("officialWebsite", clickOfficial.getWebsiteURL());
        officialIntent.putExtra("officialFacebook", clickOfficial.getFacebook());
        officialIntent.putExtra("officialTwitter", clickOfficial.getTwitter());
        officialIntent.putExtra("officialYoutube", clickOfficial.getYoutube());

        startActivity(officialIntent);

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public void showDownloaderError()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Failed to download representative information");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}