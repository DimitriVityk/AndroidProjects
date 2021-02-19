package com.example.rewards;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileRewardEntryViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView points;
    TextView date;
    TextView note;


    public ProfileRewardEntryViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.recyclerRewardName);
        points = itemView.findViewById(R.id.recyclerRewardPoints);
        note = itemView.findViewById(R.id.recyclerRewardNote);
        date = itemView.findViewById(R.id.recyclerRewardDate);
    }
}
