package com.example.rewards;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateProfileAPIRunnable implements Runnable {

    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/UpdateProfile";
    private static final String TAG = "UpdateProfileRunnable";

    private String apiKey;
    private EditProfileActivity editProfileActivity;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String department;
    private String position;
    private String story;
    private String location;
    private Bitmap image;
    private String imageBase64;

    public UpdateProfileAPIRunnable(String apiKey, EditProfileActivity editProfileActivity, String firstName, String lastName, String username, String password, String department, String position, String story, String location, Bitmap image) {
        this.apiKey = apiKey;
        this.editProfileActivity = editProfileActivity;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.department = department;
        this.position = position;
        this.story = story;
        this.location = location;
        this.image = image;
        this.imageBase64 = makeImageBase64();
    }

    private String makeImageBase64() {
        // Remember - API requirements:
        // Profile image (as Base64 String) – Not null or empty, 100000 character maximum
        ByteArrayOutputStream byteArrayOutputStream;
        int value = 80;

        while (value > 0) {
            byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
            String b64 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            Log.d(TAG, "makeImagesBase64: " + b64.length());
            if (b64.length() > 100000) {
                value -= 10;
            } else {
                Log.d(TAG, "makeImageBase64: " + value);
                return b64;
            }
        }
        return null;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = baseURL + endPoint;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            Log.d(TAG, "run: Initial URL: " + urlString);

            buildURL.appendQueryParameter("firstName", firstName);
            buildURL.appendQueryParameter("lastName", lastName);
            buildURL.appendQueryParameter("userName", username);
            buildURL.appendQueryParameter("department", department);
            buildURL.appendQueryParameter("story", story);
            buildURL.appendQueryParameter("position", position);
            buildURL.appendQueryParameter("password", password);
            buildURL.appendQueryParameter("location", location);
            String urlToUse = buildURL.build().toString();

            URL url = new URL(urlToUse);
            Log.d(TAG, "run: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiKey);
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(imageBase64);
            out.close();

            final StringBuilder sb = new StringBuilder();
            Log.d(TAG, "response code: " + connection.getResponseCode());
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }
                process(sb.toString());
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }
                editProfileActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editProfileActivity.saveError(sb.toString());
                    }
                });
            }

        }  catch (Exception e) {
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
    }

    public void process(String HTTPResponse)
    {
        final String userNameJSON;
        final String passwordJSON;

        try {
            JSONObject createJSON = new JSONObject(HTTPResponse);
            userNameJSON = createJSON.getString("userName");
            passwordJSON = createJSON.getString("password");
            editProfileActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    editProfileActivity.loginFromEdit(userNameJSON, passwordJSON);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
