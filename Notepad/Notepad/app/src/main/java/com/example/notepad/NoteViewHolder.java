package com.example.notepad;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder {
    TextView subject;
    TextView date;
    TextView content;

    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);

        subject = itemView.findViewById(R.id.recyclerSubject);
        date = itemView.findViewById(R.id.recyclerDate);
        content = itemView.findViewById(R.id.recyclerContent);
    }
}
