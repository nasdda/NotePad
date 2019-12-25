package com.example.javaprac.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.javaprac.Database.NoteDatabase;
import com.example.javaprac.R;
import com.example.javaprac.Recycler.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    private String promptText = "Enter Title";
    Executor dbExecutor = Executors.newSingleThreadExecutor();
    private RecyclerViewAdapter adapter;
    public static Activity home = null; //used to finish HomeActivity from another activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("NotePad");
        FloatingActionButton fab = findViewById(R.id.fab);
        home = this;
        dbExecutor.execute(()->{ //Gets data from database and initializes recycler view
            List<String> entries = NoteDatabase.getNoteDatabase(this).noteDatabaseDao().getTitles();
            List<Long> ids = NoteDatabase.getNoteDatabase(this).noteDatabaseDao().getIds();
            initRecyclerView(new ArrayList<>(entries), new ArrayList<>(ids));
        });

        fab.setOnClickListener(view -> createNote());
    }

    // Shows dialog to ask for title of new note
    private void createNote(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(promptText);
        final EditText input = new EditText(getBaseContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

        builder.setPositiveButton("Create", (dialogInterface, i) -> {
            String newTitle = input.getText().toString();
            if(!newTitle.trim().isEmpty()){
                Intent intent = new Intent(getBaseContext(), NoteActivity.class);
                Bundle extra = new Bundle();
                extra.putString("title",newTitle.trim());
                intent.putExtras(extra);
                startActivity(intent);
            }else{
                Toast.makeText(HomeActivity.this, "Title cannot be empty", Toast.LENGTH_LONG).show();
            }
        });

        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(NoteActivity.noteActivity != null){
            NoteActivity.noteActivity.finish();
        }

//        dbExecutor.execute(()->{
//            NoteDatabase.getNoteDatabase(this).noteDatabaseDao().deleteAll();
//        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Home", "stopped");
    }

    private void initRecyclerView(ArrayList<String> mTitles, ArrayList<Long> ids){
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(this,mTitles, ids);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("Home", "restarted");
    }


}
