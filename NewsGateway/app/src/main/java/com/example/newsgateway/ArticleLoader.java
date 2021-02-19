package com.example.newsgateway;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import static java.net.HttpURLConnection.HTTP_OK;

public class ArticleLoader implements Runnable {

    private static final String TAG = "ArticleLoader";
    private MainActivity mainActivity;
    private String sourceName;
    private static final String dataUrl = "https://newsapi.org/v2/top-headlines";
    private String apiKey = "ad2dd74200fc4ffdb43ab25670e7719f";

    public ArticleLoader(MainActivity mainActivity, String sourceName) {
        this.mainActivity = mainActivity;
        this.sourceName = sourceName;
    }

    @Override
    public void run() {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            String endUrl = dataUrl;
            Uri.Builder buildURL = Uri.parse(endUrl).buildUpon();
            buildURL.appendQueryParameter("sources", sourceName);
            buildURL.appendQueryParameter("apiKey", apiKey);
            String urlToUse = buildURL.build().toString();
            Log.d(TAG, "This is the url" + urlToUse);
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urlToUse);
                Log.d(TAG, "This is the url2" + url.toString());
                connection = (HttpURLConnection) url.openConnection();
                Log.d(TAG, "Still alive 2");
                connection.setRequestMethod("GET");
                connection.addRequestProperty("User-Agent", "");
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

                    List<Article> articleList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(result.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("articles");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jArticle = (JSONObject) jsonArray.get(i);
                        String author = jArticle.getString("author");
                        String title = jArticle.getString("title");
                        String description = jArticle.getString("description");
                        String articleUrl = jArticle.getString("url");
                        String urlToImage = jArticle.getString("urlToImage");
                        String date = jArticle.getString("publishedAt");

                        Article tempArticle = new Article(author, title, description, articleUrl, urlToImage, date);
                        articleList.add(tempArticle);
                    }
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.setArticles(articleList);
                        }
                    });

                } else {
                    Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + connection.getResponseCode());
                    Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + connection.getResponseMessage());
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                    String line;
                    while (null != (line = reader.readLine())) {
                        result.append(line).append("\n");
                    }

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
    }
