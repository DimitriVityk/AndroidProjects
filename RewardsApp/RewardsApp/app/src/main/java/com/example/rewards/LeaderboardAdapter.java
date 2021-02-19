package com.example.rewards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {
    private List<Profile> profileList;
    private LeaderboardActivity leaderboardActivity;

    public LeaderboardAdapter(List<Profile> pList, LeaderboardActivity la)
    {
        this.profileList = pList;
        this.leaderboardActivity = la;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_entry, parent, false);

        itemView.setOnClickListener(leaderboardActivity);
        itemView.setOnLongClickListener(leaderboardActivity);

        return new LeaderboardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {

        Profile p = profileList.get(position);

        String name = p.getLastName() + ", " + p.getFirstName();
        holder.name.setText(name);

        String posAndDept = p.getPosition() + ", " + p.getDepartment();
        holder.position.setText(posAndDept);

        int rewardPoints = p.getPointsAwarded();
        String rewardP = String.valueOf(rewardPoints);
        holder.points.setText(rewardP);

        String image64 = p.getImage64();
        byte[] imageBytes = Base64.decode(image64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.image.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }
}
