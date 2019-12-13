package com.example.javaprac.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javaprac.Database.Note;
import com.example.javaprac.Database.NoteDatabase;
import com.example.javaprac.R;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NoteActivity extends AppCompatActivity {
    private Button saveButton, cancelButton;
    private int saveID, cancelID;
    private String title;
    private String preBody = "";
    private TextView body;
    Executor dbExecutor = Executors.newSingleThreadExecutor();
    private boolean saved = false;
    private Intent intent;
    private Long id;
    public static NoteActivity noteActivity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        title = getIntent().getExtras().getString("title");
        setTitle(title);
        body = (TextView) findViewById(R.id.bodyText);
        id = getIntent().getExtras().getLong("ID");
        if(id != 0){
            dbExecutor.execute(()->{
                Note note = NoteDatabase.getNoteDatabase(this).noteDatabaseDao().getNote(id);
                body.setText(note.getBody());
                preBody = note.getBody().toString();
            });
        }

        noteActivity = this;

        saveButton = (Button) findViewById(R.id.save_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        intent = new Intent(this, HomeActivity.class);
        saveID = saveButton.getId();
        cancelID = cancelButton.getId();
        saveButton.setOnClickListener((button)->{//adds listener to cancel button dialog
            if(!preBody.equals(body.getText().toString())) {
                listenerById(saveID);
            }
        });
        cancelButton.setOnClickListener((button)->{
            if(!preBody.equals(body.getText().toString())) {
                listenerById(cancelID);
            }
            else{
                if (saved) {
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    private void listenerById(int id){
        if(id == saveID){
            AlertDialog.Builder builder = makeDialog("Save Changes?",false);
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //do nothing
                }
            });
            builder.show();
        }else if(id == cancelID){
            AlertDialog.Builder builder = makeDialog("Save Changes?", true);
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) { //cancel without saving
                    if (saved) {
                        startActivity(intent);
                    }
                    else{
                        finish();
                    }
                }
            });
            builder.show();
        }
    }


    private AlertDialog.Builder makeDialog(String textBody,boolean cancel){
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
        builder.setMessage(textBody);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {//saves on cancel
                dbExecutor.execute(()->{
                    startSave();
                    HomeActivity.home.finish();
                    saved = true; //saved, thus main activity is destroyed and needs to be restarted
                    if(cancel){
                        startActivity(intent);
                    }
                });
            }
        });
        return builder;
    }


    private void startSave() { //saves note into database and destroys previous activity
        Note note = new Note(title, body.getText().toString());
        preBody = body.getText().toString();
        dbExecutor.execute(() -> { //update database if note already exist, else create new note
            if(id != 0) {
                NoteDatabase.getNoteDatabase(this).noteDatabaseDao().update(id,note.getBody());
            }
            else{
                id = NoteDatabase.getNoteDatabase(this).noteDatabaseDao().getNewId(note);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if(!preBody.equals(body.getText().toString())) {
            listenerById(cancelID);
        }
        else{
            if (saved) {
                startActivity(intent);
            }
            else{
                finish();
            }
        }
    }
}
