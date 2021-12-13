package com.example.androidnotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private final ArrayList<Note> notes;
    private final MainActivity mainActivity;

    public NoteAdapter(ArrayList<Note> notes, MainActivity mainActivity) {
        this.notes = notes;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        String noteText = note.getNoteText();

        if (noteText.length() > 80) {
            noteText = noteText.substring(0, 80).trim() + "...";
        }

        holder.title.setText(note.getTitle());
        holder.lastUpdateTime.setText(note.getLastUpdateTime());
        holder.noteText.setText(noteText);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
