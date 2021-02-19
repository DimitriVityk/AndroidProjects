package com.example.stockwatch;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StockRefresher implements Runnable{

    private static final String STOCK_URL = "https://cloud.iexapis.com/stable/stock/";
    private static final String TAG = "stockDownloader";
    private MainActivity mainActivity;
    private String searchTarget;
    private int position;
    private static final String API_KEY = "pk_26461c126b2e475eb42ca530eb120415";

    public StockRefresher(MainActivity mainActivity, String searchTarget, int position) {
        this.mainActivity = mainActivity;
        this.searchTarget = searchTarget;
        this.position = position;
    }

    @Override
    public void run() {
        Uri.Builder uriBuilder = Uri.parse(STOCK_URL + searchTarget + "/quote?token=" + API_KEY).buildUpon();
        String urlToUse = uriBuilder.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

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

    private void process(String s) {
        try {
            JSONObject jStock = new JSONObject(s);

            String symbol = jStock.getString("symbol");
            String companyName = jStock.getString("companyName");
            double price = jStock.getDouble("latestPrice");
            double priceChange = jStock.getDouble("change");
            double changePercent = jStock.getDouble("changePercent");
            final Stock stock = new Stock(symbol, companyName, price, priceChange, changePercent);

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.refreshStock(stock, position);
                }
            });

        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}