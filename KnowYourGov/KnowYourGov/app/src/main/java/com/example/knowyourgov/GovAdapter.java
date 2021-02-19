package com.example.knowyourgov;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GovAdapter extends RecyclerView.Adapter<GovViewHolder>{

    private List<Official> oList;
    private MainActivity mainAct;

    public GovAdapter(List<Official> oList, MainActivity ma) {
        this.oList = oList;
        this.mainAct = ma;
    }

    @NonNull
    @Override
    public GovViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gov_list_entry, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new GovViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GovViewHolder holder, int position) {
        Official o = oList.get(position);
        holder.title.setText(o.getOffice());
        holder.nameParty.setText(o.getName() + " (" + o.getParty() + ")");

    }

    @Override
    public int getItemCount() {
        return oList.size();
    }
}
