package com.example.knowyourgov;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OfficialDownloader implements Runnable{
    private static final String TAG = "OfficialDownloader";
    private MainActivity mainActivity;
    private String searchTarget;
    private static final String API_KEY = "AIzaSyD_naYbQ3Y_gpz4qVghDqPdOY6_1Xr4OCk";
    private static final String URL = "https://www.googleapis.com/civicinfo/v2/representatives";


    public OfficialDownloader(MainActivity mainActivity, String searchTarget)
    {
        this.mainActivity = mainActivity;
        this.searchTarget = searchTarget;
    }

    @Override
    public void run() {
        Uri.Builder uriBuilder = Uri.parse(URL).buildUpon();
        uriBuilder.appendQueryParameter("key", API_KEY);
        uriBuilder.appendQueryParameter("address", searchTarget);
        String urlToUse = uriBuilder.toString();

        StringBuilder sb = new StringBuilder();
        try {
            java.net.URL url = new URL(urlToUse);

            Log.d(TAG, "url:  " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.showDownloaderError();
                    }
                });
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "run: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            return;
        }

        process(sb.toString());
        Log.d(TAG, "run: ");

    }

    private void process(String s)
    {
        String office;
        String name;
        String party;
        String officeAddress;
        String phoneNumber;
        String email;
        String photoURL;
        String websiteURL;
        String facebook = "";
        String twitter = "";
        String youtube = "";
        int [] officialIndices;

        try {
            JSONObject jOfficial = new JSONObject(s);

            JSONObject normalizedInput = jOfficial.getJSONObject("normalizedInput");
            JSONArray offices = jOfficial.getJSONArray("offices");
            JSONArray officials = jOfficial.getJSONArray("officials");

            for(int i = 0; i < offices.length(); i++)
            {
                JSONObject officeObject = (JSONObject) offices.get(i);
                office = officeObject.getString("name");
                JSONArray indices = officeObject.optJSONArray("officialIndices");

                if(indices == null)
                {
                    // do something
                }


                officialIndices = new int [indices.length()];
                for(int k = 0; k < indices.length(); k++)
                {
                    officialIndices[k] = indices.optInt(k);
                }
                for(int j = 0; j < officialIndices.length; j++)
                {
                    JSONObject officialsObject = (JSONObject) officials.get(officialIndices[j]);
                    name = officialsObject.isNull("name")? null : officialsObject.getString("name");
                    party = officialsObject.isNull("party")? null : officialsObject.getString("party");
                    photoURL = officialsObject.isNull("photoUrl")? null : officialsObject.getString("photoUrl");
                    Log.d(TAG, "process: " + photoURL);

                    phoneNumber = officialsObject.isNull("phones")? null : officialsObject.getJSONArray("phones").get(0).toString();
                    email = officialsObject.isNull("emails")? null : officialsObject.getJSONArray("emails").get(0).toString();
                    websiteURL = officialsObject.isNull("urls")? null : officialsObject.getJSONArray("urls").get(0).toString();
                    JSONArray addressArr = officialsObject.getJSONArray("address");
                    JSONObject addressObj =  (JSONObject) addressArr.get(0);
                    String line1 = addressObj.isNull("line1")? "": addressObj.getString("line1");
                    String line2 = addressObj.isNull("line2")? "": addressObj.getString("line2");
                    String line3 = addressObj.isNull("line3")? "": addressObj.getString("line3");
                    String city = addressObj.isNull("city")? "": addressObj.getString("city");
                    String state = addressObj.isNull("state")? "": addressObj.getString("state");
                    String zip = addressObj.isNull("zip")? "": addressObj.getString("zip");
                    officeAddress = line1 + line2 + line3 + ", " + city + ", " + state + ", " + zip;

                    if (officialsObject.has("channels"))
                    {
                        JSONArray channels = officialsObject.getJSONArray("channels");
                        for (int x = 0; x < channels.length(); x++) {
                            JSONObject channel = (JSONObject) channels.get(x);
                            if (channel.getString("type").equals("Facebook")) {
                                facebook = channel.getString("id");
                            }
                            if (channel.getString("type").equals("Twitter")) {
                                twitter = channel.getString("id");
                            }
                            if (channel.getString("type").equals("YouTube")) {
                                youtube = channel.getString("id");
                            }
                        }
                    }

                    final Official off = new Official(office, name, party, officeAddress, phoneNumber, email, websiteURL, facebook, twitter, youtube, photoURL);

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.addOfficial(off);
                        }
                    });
                }
                office = "";
                name = "";
                party = "";
                party = "";
                officeAddress = "";
                phoneNumber = "";
                email = "";
                websiteURL = "";
                facebook = "";
                twitter = "";
                twitter = "";
                youtube = "";
                photoURL = "";

            }


        } catch (Exception e)
        {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
