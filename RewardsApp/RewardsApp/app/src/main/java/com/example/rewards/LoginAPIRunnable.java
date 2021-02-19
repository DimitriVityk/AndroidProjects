package com.example.rewards;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

import static java.net.HttpURLConnection.HTTP_OK;

public class LoginAPIRunnable implements Runnable {

    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/Login";
    private static final String TAG = "loginRunnable";

    private CreateProfileActivity createProfileActivity;
    private MainActivity mainActivity;
    private EditProfileActivity editProfileActivity;
    private LeaderboardActivity leaderboardActivity;
    private String userNameLOGIN;
    private String passwordLOGIN;
    private String apiKey;
    private Profile profile;


    public LoginAPIRunnable (LeaderboardActivity leaderboardActivity, String userNameLOGIN, String passwordLOGIN, String apiKey)
    {
        this.leaderboardActivity = leaderboardActivity;
        this.userNameLOGIN = userNameLOGIN;
        this.passwordLOGIN = passwordLOGIN;
        this.apiKey = apiKey;
    }

    public LoginAPIRunnable (CreateProfileActivity createProfileActivity, String userNameLOGIN, String passwordLOGIN, String apiKey)
    {
        this.createProfileActivity = createProfileActivity;
        this.userNameLOGIN = userNameLOGIN;
        this.passwordLOGIN = passwordLOGIN;
        this.apiKey = apiKey;
    }

    public LoginAPIRunnable (MainActivity mainActivity, String userNameLOGIN, String passwordLOGIN, String apiKey)
    {
        this.mainActivity = mainActivity;
        this.userNameLOGIN = userNameLOGIN;
        this.passwordLOGIN = passwordLOGIN;
        this.apiKey = apiKey;
    }

    public LoginAPIRunnable (EditProfileActivity editProfileActivity, String userNameLOGIN, String passwordLOGIN, String apiKey)
    {
        this.editProfileActivity = editProfileActivity;
        this.userNameLOGIN = userNameLOGIN;
        this.passwordLOGIN = passwordLOGIN;
        this.apiKey = apiKey;
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

            buildURL.appendQueryParameter("userName", userNameLOGIN);
            buildURL.appendQueryParameter("password", passwordLOGIN);
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

                if(mainActivity != null) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.badLoginForm(result.toString());
                        }
                    });
                }
                else if(createProfileActivity != null)
                {
                    createProfileActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createProfileActivity.badLogin(result.toString());
                        }
                    });
               } else if (editProfileActivity != null)
                {
                    editProfileActivity.badLogin(result.toString());
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
        final String userFirstName;
        final String userLastName;
        final String userUserName;
        final String userPassword;
        final String userDepartment;
        final String userStory;
        final String userPosition;
        final int userRemainingPointsToAward;
        final String userLocation;
        final String userImage64;
        JSONArray jsonArray;

        try {
            JSONObject loginAPI = new JSONObject(HTTPResponse);
            userFirstName = loginAPI.getString("firstName");
            userLastName = loginAPI.getString("lastName");
            userUserName = loginAPI.getString("userName");
            userPassword = loginAPI.getString("password");
            userDepartment = loginAPI.getString("department");
            userStory = loginAPI.getString("story");
            userPosition = loginAPI.getString("position");
            userRemainingPointsToAward = loginAPI.getInt("remainingPointsToAward");
            userLocation = loginAPI.getString("location");
            userImage64 = loginAPI.getString("imageBytes");
            profile = new Profile(userFirstName, userLastName, userUserName, userDepartment, userStory, userPosition, userPassword, userRemainingPointsToAward, userLocation, userImage64);


            jsonArray = loginAPI.getJSONArray("rewardRecordViews");

            int pointsAwarded = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                Reward reward;
                JSONObject jsonReward = (JSONObject) jsonArray.get(i);

                String giver = jsonReward.getString("giverName");
                int amount = jsonReward.getInt("amount");
                pointsAwarded += amount;
                String note = jsonReward.getString("note");
                String awardDate = jsonReward.getString("awardDate");
                String year = awardDate.substring(0, 4);
                String month = awardDate.substring(5, 7);
                String day = awardDate.substring(8, 10);
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

                reward = new Reward(giver, amount, note, cal);
                profile.addReward(reward);
            }
            profile.setPointsAwarded(pointsAwarded);

            if (createProfileActivity != null) {
                createProfileActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createProfileActivity.login(profile);
                    }
                });
            } else if (mainActivity != null)
            {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.login(profile);
                    }
                });
            } else if (editProfileActivity != null)
            {
                editProfileActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editProfileActivity.login(profile);
                    }
                });

            } else if (leaderboardActivity != null)
            {
                leaderboardActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        leaderboardActivity.login(profile);
                    }
                });
            }
            } catch(Exception e){
                Log.d(TAG, "parseJSON: " + e.getMessage());
                e.printStackTrace();
            }
    }
}
