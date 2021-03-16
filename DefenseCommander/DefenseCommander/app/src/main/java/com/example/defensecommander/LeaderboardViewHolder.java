package com.example.defensecommander;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardViewHolder extends RecyclerView.ViewHolder{

    TextView init;
    TextView position;
    TextView score;
    TextView level;
    TextView date;

    public LeaderboardViewHolder(@NonNull View itemView) {
        super(itemView);


        position = itemView.findViewById(R.id.position);
        init = itemView.findViewById(R.id.initials);
        score = itemView.findViewById(R.id.score);
        level = itemView.findViewById(R.id.level);
        date = itemView.findViewById(R.id.date);
    }
}
