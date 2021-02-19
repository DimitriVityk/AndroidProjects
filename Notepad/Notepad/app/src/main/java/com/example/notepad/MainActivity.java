package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private final List<Note> noteList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NoteAdapter nAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFile();

        recyclerView = findViewById(R.id.noteRecycler);
        nAdapter = new NoteAdapter(noteList, this);
        recyclerView.setAdapter(nAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(new VerticalSpaceDecoration(20));

        this.setTitle("Multi Notes " + "(" + noteList.size() + ")");
    }

    @Override
    protected void onPause() {
        saveNotes();
        super.onPause();
    }

    @Override
    protected void onResume() {
        this.setTitle("Multi Notes " + "(" + noteList.size() + ")");
        super.onResume();
    }

    private void loadFile() {
        try {
            FileInputStream fis = getApplicationContext().
                    openFileInput("Notes.json");

            byte[] data = new byte[(int) fis.available()];
            int loaded = fis.read(data);
            fis.close();
            String json = new String(data);

            JSONArray noteArr = new JSONArray(json);
            for (int i = 0; i < noteArr.length(); i++) {
                JSONObject nObj = noteArr.getJSONObject(i);

                String title = nObj.getString("title");
                String text = nObj.getString("text");
                long dateMS = nObj.getLong("lastDate");

                Note n = new Note(title, text);
                n.setLastDate(dateMS);
                noteList.add(n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveNotes()
    {
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput("Notes.json", Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginArray();
            for (Note n : noteList) {
                writer.beginObject();
                writer.name("title").value(n.getTitle());
                writer.name("text").value(n.getContent());
                writer.name("lastDate").value(n.getLastDate().getTime());
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED)
        {
            return;
        }

        switch(requestCode){
            case(1):
            {
                String title = data.getStringExtra("Title");
                String content = data.getStringExtra("Content");
                Note newNote = new Note(title, content);
                if (!newNote.getTitle().equals("")) {
                    noteList.add(newNote);
                    Collections.sort(noteList);
                    nAdapter.notifyDataSetChanged();
                }
                Toast.makeText(this,
                        "Your note titled: " + data.getStringExtra("Title") + " was successfully saved.",
                        Toast.LENGTH_LONG).show();
            }
            break;
            case(2):
            {
                String title = data.getStringExtra("Title");
                String content = data.getStringExtra("Content");
                int pos = data.getIntExtra("Position", -1);
                Note newNote = new Note(title, content);
                if(pos > -1) { noteList.remove(pos); }
                noteList.add(newNote);
                Collections.sort(noteList);
                nAdapter.notifyDataSetChanged();
                Toast.makeText(this,
                        "Your note titled: " + data.getStringExtra("Title") + " was successfully saved.",
                        Toast.LENGTH_LONG).show();
            }
            break;
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.opt_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            switch(item.getItemId())
            {
                case R.id.addNote:
                    Intent addIntent = new Intent(this, EditActivity.class);
                    startActivityForResult(addIntent,1);
                    return true;
                case R.id.about:
                    Intent aboutIntent = new Intent(this, AboutActivity.class);
                    startActivity(aboutIntent);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        @Override
        public void onClick(View v) {
            int pos = recyclerView.getChildLayoutPosition(v);
            Note n = noteList.get(pos);
            Intent clickIntent = new Intent(this, EditActivity.class);
            clickIntent.putExtra("editTitle", n.getTitle());
            clickIntent.putExtra("editContent", n.getContent());
            clickIntent.putExtra("position", pos);
            startActivityForResult(clickIntent, 2);
        }

        @Override
        public boolean onLongClick(View v) {
            final int pos = recyclerView.getChildLayoutPosition(v);
            Note clickNote = noteList.get(pos);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Note \'" + clickNote.getTitle() + "\'?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    noteList.remove(pos);
                    nAdapter.notifyDataSetChanged();
                    onResume();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

    }