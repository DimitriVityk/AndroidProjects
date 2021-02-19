package com.example.rewards;

import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class GetStudentAPIKeyRunnable implements Runnable {

    private static final String TAG = "getAPI";
    private MainActivity mainActivity;
    private String firstName;
    private String lastName;
    private String email;
    private String ID;
    private String baseURL = "http://christopherhield.org/api";
    private String endPoint = "/Profile/GetStudentApiKey";

    public GetStudentAPIKeyRunnable(MainActivity mainActivity, String firstName, String lastName, String email, String ID) {
        this.mainActivity = mainActivity;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.ID = ID;
    }

    @Override
    public void run() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        String endUrl = baseURL + endPoint;
        Uri.Builder buildURL = Uri.parse(endUrl).buildUpon();
        buildURL.appendQueryParameter("firstName", firstName);
        buildURL.appendQueryParameter("lastName", lastName);
        buildURL.appendQueryParameter("studentId", ID);
        buildURL.appendQueryParameter("email", email);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "This is the url" + urlToUse);
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            Log.d(TAG, "This is the url2" + url.toString());
            connection = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "Still alive 2");
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();
            Log.d(TAG, "Still good1 ");
            int responseCode = connection.getResponseCode();



            if (responseCode == HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode OK: " + connection.getResponseCode());
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                process(result.toString());
            } else {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + connection.getResponseCode());
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.badAPIKeyForm(result.toString());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Something went wrong: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {

                }
            }
        }


    }

    private void process(String s)
    {
        final String apiKey;

        try {
            JSONObject jAPI = new JSONObject(s);
            apiKey = jAPI.getString("apiKey");
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.saveAPIKeyDialog(apiKey, firstName, lastName, email, ID);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
