package com.example.a53914.sievemobileapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.example.a53914.sievemobileapplication.db.TaskDatabase;
import com.example.a53914.sievemobileapplication.db.Task;

import java.lang.ref.WeakReference;

public class TaskCreate extends AppCompatActivity {
    private TaskDatabase taskDatabase;
    private Task task;
    int priorityID;
    String classes;
    int typeID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);

        SeekBar slidey = findViewById(R.id.TaskCreateSeekbar);
        slidey.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress>=0&&progress<34){
                    priorityID=0;
                }
                else if (progress>=34&&progress<67){
                    priorityID=1;
                }
                else{
                    priorityID=2;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Spinner classChooser = (Spinner) findViewById(R.id.planets_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.classes_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classChooser.setAdapter(adapter);
        classChooser.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                classes = (parent.getItemAtPosition(pos)).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //initialize Database:
        taskDatabase = TaskDatabase.getInstance(TaskCreate.this);
        Button button =findViewById(R.id.CreateButton);
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    EditText nameText= (EditText) findViewById(R.id.NameAddText);
                    EditText notesText = (EditText) findViewById(R.id.NotesText);
                    task = new Task(priorityID,nameText.getText().toString(),classes,/*null,*/
                            notesText.getText().toString(),typeID);
                    new InsertTask(TaskCreate.this,task).execute();
                }
            });
    }
    private void setResult(Task task, int flag){
        setResult(flag,new Intent().putExtra("task",task.toString()));
        finish();
    }

    private static class InsertTask extends AsyncTask<Void, Void,Boolean> {
        private WeakReference<TaskCreate> activityReference;
        private Task task;
        InsertTask(TaskCreate context, Task task){
            activityReference=new WeakReference<>(context);
            this.task=task;
        }
        @Override
        protected Boolean doInBackground(Void...objs){
            activityReference.get().taskDatabase.taskDao().insertAll(task);
            return true;
        }
        @Override
        protected void onPostExecute(Boolean bool){
            if(bool){
                activityReference.get().setResult(task,1);
                Log.d("TaskCreate","The Async Task has finished!");
                Log.d("TaskCreate",task.toString());
                activityReference.get().finish();
            }
        }
    }
    /*class DBAddition implements Runnable {
        public void run() {
            final TaskDao taskDao = new TaskDao() {
                @Override
                public List<Task> getAll() {
                    return null;
                }

                @Override
                public void insertAll(Task task) {

                }

                @Override
                public void delete(Task user) {

                }
            };

            //task.setPriority(priorityID);

            EditText nameText = (EditText) findViewById(R.id.NameAddText);
            //task.setNameID(nameText.getText().toString());
            String textName = nameText.getText().toString();
            //task.setClassroom(classes);

            //task.setDueDate();

            EditText notesText = (EditText) findViewById(R.id.NotesText);
            //task.setNotes(notesText.getText().toString());
            String textNotes = notesText.getText().toString();

            //task.setTypeID(typeID);
            Task task = new Task(priorityID,textName,classes,null,textNotes,typeID);
            taskDao.insertAll(task);
        }
    }*/


    public void HabitClick(View view){
        typeID=0;
    }
    public void AssignClick(View view){
        typeID=1;
    }
    public void ProjectClick(View view){
        typeID=2;
    }
    //Returning Home
    public void toHomePage(View view){
        Intent toHomePage = new Intent(this, HomePage.class);
        startActivity(toHomePage);
    }
}
