package com.example.knowyourgov;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class GovViewHolder extends RecyclerView.ViewHolder{
    TextView title;
    TextView nameParty;
    public GovViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.officialTitle);
        nameParty = itemView.findViewById(R.id.officialNameParty);
    }
}
