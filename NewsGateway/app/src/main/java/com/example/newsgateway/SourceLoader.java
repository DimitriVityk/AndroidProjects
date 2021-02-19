package com.example.newsgateway;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class SourceLoader implements Runnable{

    private static final String TAG = "SourceLoader";
    private final MainActivity mainActivity;
    private static final String dataURL = "https://newsapi.org/v2/sources?apiKey=ad2dd74200fc4ffdb43ab25670e7719f";


    SourceLoader(MainActivity ma) { mainActivity = ma; }
    @Override
    public void run() {

        Uri dataUri = Uri.parse(dataURL);
        String urlToUse = dataUri.toString();

        Log.d(TAG, "This is the url" + urlToUse);
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent", "");
            conn.connect();

            StringBuilder sb = new StringBuilder();
            String line;

            if (conn.getResponseCode() == HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode OK: " + conn.getResponseCode());
                BufferedReader reader =
                        new BufferedReader((new InputStreamReader(conn.getInputStream())));

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                conn.disconnect();
                List<NewsSource> sourceList = parseJSON(sb.toString());
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.setupSources(sourceList);
                    }
                });
            } else {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseMessage());
                BufferedReader reader =
                        new BufferedReader((new InputStreamReader(conn.getErrorStream())));

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                conn.disconnect();
                Log.d(TAG, "run: " + sb.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<NewsSource> parseJSON(String s) {
        List<NewsSource> listOfSources= new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jObjMain = jsonObject.getJSONArray("sources");

            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jSource = (JSONObject) jObjMain.get(i);
                String id = jSource.getString("id");
                String name = jSource.getString("name");
                String category = jSource.getString("category");
                String language = jSource.getString("language");
                String country = jSource.getString("country");

                NewsSource tempSource = new NewsSource(id, name, category, language, country);
                listOfSources.add(tempSource);
            }
            return listOfSources;
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
