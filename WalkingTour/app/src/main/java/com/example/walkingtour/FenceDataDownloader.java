package com.example.walkingtour;


import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class FenceDataDownloader implements Runnable {

    private static final String TAG = "FenceDataDownloader";
    private final Geocoder geocoder;
    private final FenceMgr fenceMgr;
    private static final String FENCE_URL = "http://www.christopherhield.com/data/WalkingTourContent.json";

    FenceDataDownloader(MapsActivity mapsActivity, FenceMgr fenceMgr) {
        this.fenceMgr = fenceMgr;
        geocoder = new Geocoder(mapsActivity);
    }

    private void processData(String result) {

        if (result == null)
            return;

        ArrayList<FenceData> fences = new ArrayList<>();
        ArrayList<String> pathStrings = new ArrayList<>();
        try {
            JSONObject jObj = new JSONObject(result);
            JSONArray jArr = jObj.getJSONArray("fences");
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject fObj = jArr.getJSONObject(i);
                String id = fObj.getString("id");
                String address = fObj.getString("address");
                Double latitude = fObj.getDouble("latitude");
                Double longitude = fObj.getDouble("longitude");
                float rad = (float) fObj.getDouble("radius");
                String description = fObj.getString("description");
                String color = fObj.getString("fenceColor");
                String image = fObj.getString("image");

                FenceData fd = new FenceData(id, latitude, longitude, address, rad, description, color, image);
                fences.add(fd);
            }
            fenceMgr.addFences(fences);

            JSONArray pathArr = jObj.getJSONArray("path");
            for (int i = 0; i < pathArr.length(); i++) {
                String point = pathArr.getString(i);
                pathStrings.add(point);
            }
            ArrayList<LatLng> tourPath = getLatLongList(pathStrings);
            fenceMgr.addTourPolyline(tourPath);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void run() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(FENCE_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: Response code: " + connection.getResponseCode());
                return;
            }

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            processData(buffer.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<LatLng> getLatLongList(List<String> pointList) {

        ArrayList<LatLng> latLngList = new ArrayList<>();

        for(int i = 0; i < pointList.size(); i++)
        {
            String fullLatLon = pointList.get(i);
            String [] splitLatLon = fullLatLon.split(", ");
            String lon = splitLatLon[0];
            String lat = splitLatLon[1];
            latLngList.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));
        }

        return latLngList;

    }
}