package com.example.androidnotes;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView lastUpdateTime;
    TextView noteText;

    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title_holder);
        lastUpdateTime = itemView.findViewById(R.id.last_update_time_holder);
        noteText = itemView.findViewById(R.id.note_text_holder);
    }
}
