package com.example.rewards;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardViewHolder extends RecyclerView.ViewHolder{

    TextView name;
    TextView position;
    TextView points;
    ImageView image;

    public LeaderboardViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.leaderboardName);
        position = itemView.findViewById(R.id.leaderboardPosition);
        points = itemView.findViewById(R.id.leaderboardPoints);
        image = itemView.findViewById(R.id.leaderboardImage);
    }
}
