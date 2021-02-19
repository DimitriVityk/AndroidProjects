package com.example.rewards;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RewardsAPIRunnable implements Runnable {

    private static final String TAG = "RewardRunnable";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Rewards/AddRewardRecord";

    private RewardActivity rewardActivity;
    private String apiKey;
    private String receiverUsername;
    private String giverUsername;
    private String giverName;
    private int amount;
    private String note;

    public RewardsAPIRunnable(RewardActivity rewardActivity, String apiKey, String receiverUsername,
                              String giverUsername, String giverName, int amount, String note) {
        this.rewardActivity = rewardActivity;
        this.apiKey = apiKey;
        this.receiverUsername = receiverUsername;
        this.giverUsername = giverUsername;
        this.giverName = giverName;
        this.amount = amount;
        this.note = note;
    }

    @Override
    public void run() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = baseURL + endPoint;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            Log.d(TAG, "run: Initial URL: " + urlString);

            buildURL.appendQueryParameter("receiverUser", receiverUsername);
            buildURL.appendQueryParameter("giverUser", giverUsername);
            buildURL.appendQueryParameter("giverName", giverName);
            buildURL.appendQueryParameter("amount", String.valueOf(amount));
            buildURL.appendQueryParameter("note", note);

            String urlToUse = buildURL.build().toString();

            URL url = new URL(urlToUse);
            Log.d(TAG, "run: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
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
    }
}
