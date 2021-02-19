package com.example.rewards;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteProfileAPIRunnable implements Runnable {

    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/DeleteProfile";
    private static final String TAG = "DeleteProfileRunnable";

    private ProfileActivity profileActivity;
    private String deleteUsername;
    private String apiKey;

    public DeleteProfileAPIRunnable(ProfileActivity profileActivity, String deleteUsername, String apiKey) {
        this.profileActivity = profileActivity;
        this.deleteUsername = deleteUsername;
        this.apiKey = apiKey;
    }

    @Override
    public void run() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = baseURL + endPoint;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            Log.d(TAG, "run: Initial URL: " + urlString);

            buildURL.appendQueryParameter("userName", deleteUsername);

            String urlToUse = buildURL.build().toString();

            URL url = new URL(urlToUse);
            Log.d(TAG, "run: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiKey);
            connection.connect();

            final StringBuilder sb = new StringBuilder();
            Log.d(TAG, "response code: " + connection.getResponseCode());
            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }

            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "doInBackground: Invalid URL: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
        profileActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                profileActivity.exitApplication();
            }
        });
    }

}
