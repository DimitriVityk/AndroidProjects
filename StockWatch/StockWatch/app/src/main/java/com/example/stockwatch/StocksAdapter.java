package com.example.stockwatch;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class StocksAdapter extends RecyclerView.Adapter<StockViewHolder>{

    private List<Stock> stockList;
    private MainActivity mainAct;

    public StocksAdapter (List<Stock> sList, MainActivity ma)
    {
        this.stockList = sList;
        this.mainAct = ma;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_entry, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {

        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.FLOOR);

        Stock s = stockList.get(position);
        holder.stockSymbol.setText(s.getStockSymbol());
        holder.companyName.setText(s.getCompanyName());
        holder.price.setText(df.format(s.getPrice()));
        if(s.getPriceChange() >= 0.00) {
            holder.priceChangeAndPercentage.setText("▲" + df.format(s.getPriceChange()) + "(" + df.format(s.getChangePercentage()) + "%)");
            holder.priceChangeAndPercentage.setTextColor(Color.GREEN);
            holder.stockSymbol.setTextColor(Color.GREEN);
            holder.companyName.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
        }
        else
        {
            holder.priceChangeAndPercentage.setText("▼" + df.format(s.getPriceChange()) + "(" + df.format(s.getChangePercentage()) + "%)");
            holder.priceChangeAndPercentage.setTextColor(Color.RED);
            holder.stockSymbol.setTextColor(Color.RED);
            holder.companyName.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);

        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
