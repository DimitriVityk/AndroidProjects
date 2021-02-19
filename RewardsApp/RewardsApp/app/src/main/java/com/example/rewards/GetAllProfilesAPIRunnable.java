package com.example.rewards;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class GetAllProfilesAPIRunnable implements Runnable {

    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/GetAllProfiles";
    private static final String TAG = "AllProfilesRunnable";

    private String apiKey;
    private LeaderboardActivity leaderboardActivity;

    public GetAllProfilesAPIRunnable (LeaderboardActivity leaderboardActivity, String APIKEY)
    {
        this.apiKey = APIKEY;
        this.leaderboardActivity = leaderboardActivity;
    }

    @Override
    public void run() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();


        try {
            String urlString = baseURL + endPoint;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            Log.d(TAG, "run: Initial URL: " + urlString);
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);
            Log.d(TAG, "run: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiKey);
            connection.connect();

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

    public void process(String HTTPResponse)
    {
        List<Profile> profList = new ArrayList<>();

        try {
            JSONArray profileArr = new JSONArray(HTTPResponse);
            for(int i = 0; i < profileArr.length(); i++)
            {
                Profile profile;
                JSONObject jsonProfile = (JSONObject) profileArr.get(i);
                String first = jsonProfile.getString("firstName");
                String last = jsonProfile.getString("lastName");
                String user = jsonProfile.getString("userName");
                String department = jsonProfile.getString("department");
                String story = jsonProfile.getString("story");
                String position = jsonProfile.getString("position");
                String image64 = jsonProfile.getString("imageBytes");
                profile = new Profile(first, last, user, department, story, position, image64);
                JSONArray rewardArr = jsonProfile.getJSONArray("rewardRecordViews");
                int rewardAmount = 0;
                for(int j = 0; j < rewardArr.length(); j++)
                {
                    Reward reward;
                    JSONObject jsonReward = (JSONObject) rewardArr.get(j);
                    int amount = jsonReward.getInt("amount");
                    rewardAmount += amount;
                }
                profile.setPointsAwarded(rewardAmount);
                profList.add(profile);
            }

            leaderboardActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    leaderboardActivity.loadProfileList(profList);
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
