package com.example.stockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockViewHolder extends RecyclerView.ViewHolder{
    TextView stockSymbol;
    TextView companyName;
    TextView price;
    TextView priceChangeAndPercentage;

    public StockViewHolder(@NonNull View itemView) {
        super(itemView);

        stockSymbol = itemView.findViewById(R.id.recyclerStockSymbol);
        companyName = itemView.findViewById(R.id.recyclerCompanyName);
        price = itemView.findViewById(R.id.recyclerPrice);
        priceChangeAndPercentage = itemView.findViewById(R.id.recyclerPriceAndPercent);
    }
}
