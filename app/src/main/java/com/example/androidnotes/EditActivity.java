package com.example.androidnotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {

    private Note oldNote;
    private String title = "";
    private String noteText = "";
    private int position = -1;

    private EditText titleView;
    private EditText noteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        titleView = findViewById(R.id.noteTitle);
        noteTextView = findViewById(R.id.noteText);

        noteTextView.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();

        if (intent.hasExtra("NOTE")) {
            position = intent.getIntExtra("POSITION", -1);

            oldNote = (Note) intent.getSerializableExtra("NOTE");

            title = oldNote.getTitle();
            noteText = oldNote.getNoteText();
        }

        titleView.setText(title);
        noteTextView.setText(noteText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_option) {
            onSaveNote();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        title = titleView.getText().toString().trim();
        noteText = noteTextView.getText().toString().trim();

        if (oldNote != null && oldNote.getTitle().equals(title) && oldNote.getNoteText().equals(noteText)) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Your note is not saved!");
            builder.setMessage("Do you want to save this note?");

            builder.setPositiveButton("YES", (dialog, id) -> onSaveNote());

            builder.setNegativeButton("NO", (dialog, id) ->  super.onBackPressed());

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private String formatDateTime(java.util.Date datetime) {
        String pattern = "E MMM d, h:mm a";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.US);
        return formatter.format(datetime);
    }

    public void onSaveNote() {
        Intent data = new Intent();

        title = titleView.getText().toString().trim();
        noteText = noteTextView.getText().toString().trim();

        if (!title.isEmpty()) {
            if (oldNote == null || (!oldNote.getTitle().equals(title) || !oldNote.getNoteText().equals(noteText))) {
                Note newNote = new Note(title, noteText, formatDateTime(new Date()));

                data.putExtra("NOTE", newNote);
                data.putExtra("POSITION", position);
            }

            setResult(RESULT_OK, data);
            Toast.makeText(this, "Note added/edited successfully.", Toast.LENGTH_SHORT).show();
            finish();

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Note cannot have empty title.");
            builder.setMessage("This note will not be saved without a title. Do you want to go back to main screen?");

            builder.setPositiveButton("Ok", (dialog, id) -> {
                Toast.makeText(this, "Note without title was not saved.", Toast.LENGTH_SHORT).show();
                finish();
            });

            builder.setNegativeButton("Cancel", (dialog, id) ->  { });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}