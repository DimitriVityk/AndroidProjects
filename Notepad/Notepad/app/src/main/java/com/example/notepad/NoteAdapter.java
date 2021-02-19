package com.example.notepad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder>{

    private List<Note> noteList;
    private MainActivity mainAct;

    NoteAdapter(List<Note> noteList, MainActivity ma)
    {
        this.noteList = noteList;
        this.mainAct = ma;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_entry, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

        Note note = noteList.get(position);
        holder.subject.setText(note.getTitle());
        holder.date.setText(note.getLastDate().toString());
        if(note.getContent().length() > 79) {
            holder.content.setText(note.getContent().substring(0, 80) + "...");
        }
        else { holder.content.setText(note.getContent()); }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
