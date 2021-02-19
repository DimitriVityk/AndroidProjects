package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {
    private TextView title;
    private TextView content;
    private String startTitle;
    private String startContent;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        title = findViewById(R.id.editSubject);
        content = findViewById((R.id.editData));
        String titleText = getIntent().getStringExtra("editTitle");
        String contentText = getIntent().getStringExtra("editContent");
        pos = getIntent().getIntExtra("position", -1);
        title.setText(titleText);
        content.setText(contentText);
        startTitle = titleText;
        startContent = contentText;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_opt_menu, menu);
        return true;
    }

    public void sendData() {
        Intent data = new Intent();
        data.putExtra("Title", title.getText().toString());
        data.putExtra("Content", content.getText().toString());
        setResult(RESULT_OK, data);
        finish();
    }

    public void sendData(int pos) {
        if (pos == -1) {
            sendData();
        } else {
            Intent data = new Intent();
            data.putExtra("Title", title.getText().toString());
            data.putExtra("Content", content.getText().toString());
            data.putExtra("Position", this.pos);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_save:
                if (!title.getText().toString().equals("") && (this.pos == -1))
                {
                    sendData();
                }
                else if (!title.getText().toString().equals("") && this.pos > -1)
                {
                    if (title.getText().toString().equals(startTitle) && content.getText().toString().equals(startContent))
                    {
                        Intent data = new Intent();
                        setResult(RESULT_CANCELED, data);
                        finish();
                    }
                    else
                    {
                        sendData(pos);
                    }
                }
                else
                {
                    Intent data = new Intent();
                    setResult(RESULT_CANCELED, data);
                    Toast.makeText(this, "Note not saved.", Toast.LENGTH_LONG).show();
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        if (!title.getText().toString().equals("") && !(title.getText().toString().equals(startTitle) && content.getText().toString().equals(startContent))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Save Note");
            builder.setMessage("Your changes are not saved!\nSave note \'" + title.getText().toString() + "\'?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    sendData(pos);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Intent data = new Intent();
                    setResult(RESULT_CANCELED, data);
                    Toast.makeText(EditActivity.this, "Note not saved.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (title.getText().toString().equals("")) {
            Intent data = new Intent();
            setResult(RESULT_CANCELED, data);
            Toast.makeText(EditActivity.this, "Untitled Note not saved.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
