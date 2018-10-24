package com.example.a53914.sievemobileapplication;

import android.arch.persistence.room.Room;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.a53914.sievemobileapplication.db.Task;
import com.example.a53914.sievemobileapplication.db.AppDatabase;
import com.example.a53914.sievemobileapplication.db.TaskDao;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private AppDatabase appDatabase;
    List<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //Setup the database
        appDatabase = AppDatabase.getInstance(HomePage.this);

        // Lookup the recyclerview in activity layout
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.TaskList);

        // Initialize contacts
        tasks = appDatabase.getTaskDao().getAll();
        // Create adapter passing in the sample user data
        TaskListAdapter adapter = new TaskListAdapter(tasks);
        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        // That's all!
    }

}
