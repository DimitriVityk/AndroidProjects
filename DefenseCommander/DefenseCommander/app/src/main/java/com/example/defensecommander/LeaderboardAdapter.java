package com.example.defensecommander;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {
    private final List<Player> pList;

    public LeaderboardAdapter(List<Player> pList)
    {
        this.pList = pList;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_entry, parent, false);


        return new LeaderboardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {

        Player p = pList.get(position);

        String score = String.valueOf(p.getScore());
        holder.score.setText(score);

        String pos = String.valueOf(p.getPosition());
        holder.position.setText(pos);

        String level = String.valueOf(p.getLevel());
        holder.level.setText(level);

        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
        long date = p.getDate();
        holder.date.setText(sdf.format(new Date(date)));

        String Init = p.getInitials();
        holder.init.setText(Init);


    }

    @Override
    public int getItemCount() {
        return pList.size();
    }
}
