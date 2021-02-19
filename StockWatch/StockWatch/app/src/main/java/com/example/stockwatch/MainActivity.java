package com.example.stockwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.JsonWriter;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    private final List<Stock> stockList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StocksAdapter sAdapter;
    private String decision;
    private SwipeRefreshLayout swiper;
    Boolean symDownloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swiper = findViewById(R.id.swiper);
        recyclerView = findViewById(R.id.stockRecycler);
        sAdapter = new StocksAdapter(stockList, this);
        recyclerView.setAdapter(sAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        swiper.setOnRefreshListener(this);

        if(!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Content Cannot Be Updated Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            symDownloaded = false;
            loadStocksNoCon();
            return;
        }
        SymbolNameDownloader rd = new SymbolNameDownloader(this);
        new Thread(rd).start();
        loadFile();
    }

    @Override
    protected void onPause() {
        saveStocks();
        super.onPause();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.add_menu, menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addStock) {
            createStockDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createStockDialog()
    {
        if(!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Content Cannot Be Added Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                decision = et.getText().toString().trim();

                final ArrayList<String> results = SymbolNameDownloader.findMatches(decision);

                if (results.size() == 0) {
                    doNoAnswer(decision);
                } else if (results.size() == 1) {
                    doSelection(results.get(0));
                } else {
                    String[] array = results.toArray(new String[0]);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Make a selection");
                    builder.setItems(array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String symbol = results.get(which);
                            doSelection(symbol);
                        }
                    });
                    builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog2 = builder.create();
                    dialog2.show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Please enter a Symbol or Name:");
        builder.setTitle("Stock Selection");

        AlertDialog dialog = builder.create();
        dialog.show();



    }

    private void doNoAnswer(String symbol) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("No data for specified symbol/name");
        builder.setTitle("No Data Found: " + symbol);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doSelection(String sym) {
        String[] data = sym.split("-");
        StockDownloader stockDownloader = new StockDownloader(this, data[0].trim());
        new Thread(stockDownloader).start();
    }

    private void doRefreshSelection(String sym, int position) {
        String[] data = sym.split("-");
        StockRefresher stockRefresher = new StockRefresher(this, data[0].trim(), position);
        new Thread(stockRefresher).start();
    }

    public void addStock(Stock stock)
    {
        if(stock == null)
        {
            badDataAlert(decision);
            return;
        }

        if (stockList.contains(stock)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(stock.getStockSymbol() + " is already displayed");
            builder.setTitle("Duplicate Stock");

            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        stockList.add(stock);
        Collections.sort(stockList);
        sAdapter.notifyDataSetChanged();
    }

    public void refreshStock(Stock stock, int position)
    {
        if(stock == null)
        {
            badDataAlert(decision);
            return;
        }

        stockList.set(position, stock);
        Collections.sort(stockList);
        sAdapter.notifyDataSetChanged();
    }

    private void badDataAlert(String sym) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("No data");
        builder.setTitle("Symbol Not Found: " + sym);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDownloaderError()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Failed to download stock symbols and company names");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    public void onClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        Stock clickStock = stockList.get(pos);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://www.marketwatch.com/investing/stock/" + clickStock.getStockSymbol()));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        Stock clickStock = stockList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete stock \'" + clickStock.getStockSymbol() + "\'?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stockList.remove(pos);
                sAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    private void loadFile() { //loadfile

        try {
            FileInputStream fis = getApplicationContext().
                    openFileInput("Stocks.json");

            // Read string content from file
            byte[] data = new byte[fis.available()]; // this technique is good for small files
            int loaded = fis.read(data);
            fis.close();
            String json = new String(data);

            // Create JSON Array from string file content
            JSONArray stockArr = new JSONArray(json);
            for (int i = 0; i < stockArr.length(); i++) {
                JSONObject sObj = stockArr.getJSONObject(i);
                String name = sObj.getString("name");
                String symbol = sObj.getString("symbol");
                String sym = symbol + "-" + name;
                doSelection(sym);
            }
            sAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStocksNoCon()
    {
        try {
            FileInputStream fis = getApplicationContext().
                    openFileInput("Stocks.json");

            // Read string content from file
            byte[] data = new byte[fis.available()]; // this technique is good for small files
            int loaded = fis.read(data);
            fis.close();
            String json = new String(data);

            // Create JSON Array from string file content
            JSONArray stockArr = new JSONArray(json);
            for (int i = 0; i < stockArr.length(); i++) {
                JSONObject sObj = stockArr.getJSONObject(i);
                String name = sObj.getString("name");
                String symbol = sObj.getString("symbol");
                Stock stock = new Stock(symbol, name, 0.0,0.0,0.0);
                stockList.add(stock);
            }
            Collections.sort(stockList);
            sAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveStocks() {
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput("Stocks.json", Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginArray();
            for (Stock s : stockList) {
                writer.beginObject();
                writer.name("name").value(s.getCompanyName());
                writer.name("symbol").value(s.getStockSymbol());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onRefresh() {
        if(!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Content Cannot Be Updated Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            swiper.setRefreshing(false);
            return;
        }
        else if (symDownloaded == false)
        {
            symDownloaded = true;
            SymbolNameDownloader rd = new SymbolNameDownloader(this);
            new Thread(rd).start();
        }

        int pos = 0;
        for(Stock s : stockList)
        {
            String name = s.getCompanyName();
            String symbol = s.getStockSymbol();
            String sym = symbol + "-" + name;
            doRefreshSelection(sym, pos);
            pos++;
        }
        sAdapter.notifyDataSetChanged();
        swiper.setRefreshing(false);
    }
}