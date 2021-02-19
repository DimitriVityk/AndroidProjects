package com.example.rewards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class ProfileRewardAdapter extends RecyclerView.Adapter<ProfileRewardEntryViewHolder> {

    private List<Reward> rewardList;
    private ProfileActivity profileActivity;

    public ProfileRewardAdapter(List<Reward> rList, ProfileActivity pa)
    {
        this.rewardList = rList;
        this.profileActivity = pa;
    }

    @NonNull
    @Override
    public ProfileRewardEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_list_entry, parent, false);

        itemView.setOnClickListener(profileActivity);
        itemView.setOnLongClickListener(profileActivity);

        return new ProfileRewardEntryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileRewardEntryViewHolder holder, int position) {

        Reward r = rewardList.get(position);

        holder.name.setText(r.getGiverName());
        holder.note.setText(r.getNote());
        String profilePoints = String.valueOf(r.getAmount());
        holder.points.setText(profilePoints);
        Calendar calen = r.getAwardDate();
        int year = calen.get(Calendar.YEAR);
        int month = calen.get(Calendar.MONTH);
        String monthString = String.valueOf(month);
        if(month < 10)
        {
            monthString = "0"+String.valueOf(month);
        }
        int dayOfMonth = calen.get(Calendar.DAY_OF_MONTH);
        String dayOfMonthString = String.valueOf(dayOfMonth);
        if(dayOfMonth < 10)
        {
            dayOfMonthString = "0" + String.valueOf(dayOfMonth);
        }
        String cal = monthString + "/" + dayOfMonthString + "/" + year;
        holder.date.setText(cal);
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }
}
