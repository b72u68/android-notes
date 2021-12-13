package com.example.androidnotes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private final ArrayList<Note> notes = new ArrayList<>();
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notes.clear();
        notes.addAll(loadFile());

        updateApplicationTitle();

        recyclerView = findViewById(R.id.recycler);

        noteAdapter = new NoteAdapter(notes, this);

        recyclerView.setAdapter(noteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResultFromEditActivity
        );
    }

    private void updateApplicationTitle() {
        setTitle(String.format(Locale.US, "Android Notes (%d)", notes.size()));
    }

    private void handleResultFromEditActivity(ActivityResult result) {
        if (result != null && result.getData() != null && result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();

            if (data.hasExtra("NOTE") && data.hasExtra("POSITION")) {
                Note note = (Note) data.getSerializableExtra("NOTE");
                int position = data.getIntExtra("POSITION", -1);

                if (position != -1) {
                    notes.remove(position);
                    noteAdapter.notifyItemRemoved(position);
                }

                notes.add(0, note);
                noteAdapter.notifyItemInserted(0);

                updateApplicationTitle();
            }
        }
    }

    private void openAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void openEditActivity(Note note, int position) {
        Intent intent = new Intent(this, EditActivity.class);

        intent.putExtra("NOTE", note);
        intent.putExtra("POSITION", position);
        
        activityResultLauncher.launch(intent);
    }

    private void openEditActivity() {
        Intent intent = new Intent(this, EditActivity.class);
        activityResultLauncher.launch(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.info_option) {
            openAboutActivity();
            return true;
        } else if (item.getItemId() == R.id.add_option) {
            openEditActivity();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<Note> loadFile() {
        ArrayList<Note> fileNoteList = new ArrayList<>();

        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.note_json_file));
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(sb.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String title = jsonObject.getString("title");
                String noteText = jsonObject.getString("noteText");
                String lastUpdateTime = jsonObject.getString("lastUpdateTime");

                Note note = new Note(title, noteText, lastUpdateTime);
                fileNoteList.add(note);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Cannot find Note.json file.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileNoteList;
    }

    @Override
    protected void onPause() {
        saveNote();
        super.onPause();
    }

    private void saveNote() {
        try {
            FileOutputStream fos = getApplicationContext()
                    .openFileOutput(getString(R.string.note_json_file), Context.MODE_PRIVATE);

            PrintWriter printWriter = new PrintWriter(fos);
            printWriter.print(notes);
            printWriter.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        int position = recyclerView.getChildLayoutPosition(view);
        Note note = notes.get(position);

        openEditActivity(note, position);
    }

    @Override
    public boolean onLongClick(View view) {
        int position = recyclerView.getChildLayoutPosition(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(String.format("Delete note '%s'?", notes.get(position).getTitle()));

        builder.setPositiveButton("YES", (dialog, id) -> {
            notes.remove(position);
            noteAdapter.notifyItemRemoved(position);
            updateApplicationTitle();

            Toast.makeText(this, "Note removed successfully.", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("NO", (dialog, id) -> { });

        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }
}