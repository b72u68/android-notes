package com.example.androidnotes;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class Note implements Serializable {

    private final String title;
    private final String noteText;
    private final String lastUpdateTime;

    public Note(String title, String noteText, String lastUpdateTime) {
        this.title = title;
        this.noteText = noteText;
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getTitle() {
        return title;
    }

    public String getNoteText() {
        return noteText;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    @NonNull
    public String toString() {
        String noteString = "";
        try {
            StringWriter sw = new StringWriter();

            JsonWriter jsonWriter = new JsonWriter(sw);
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.name("title").value(getTitle());
            jsonWriter.name("noteText").value(getNoteText());
            jsonWriter.name("lastUpdateTime").value(getLastUpdateTime());
            jsonWriter.endObject();
            jsonWriter.close();
            sw.close();
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return noteString;
    }
}
