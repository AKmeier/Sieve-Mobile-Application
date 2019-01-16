package com.example.a53914.sievemobileapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


import com.example.a53914.sievemobileapplication.db.TaskDatabase;
import com.example.a53914.sievemobileapplication.db.Task;
import com.example.a53914.sievemobileapplication.db.TimePickerFragmentAlarm;
import com.example.a53914.sievemobileapplication.fragments.DatePickerFragment;
import com.example.a53914.sievemobileapplication.fragments.DatePickerFragmentAlarm;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

public class TaskCreate extends AppCompatActivity {
    public class SharedPreferencesManager{
        private SharedPreferences themeStorage;
        private SharedPreferences.Editor sEditor;
        private Context context;
        SharedPreferencesManager(Context context){
            this.context = context;
            themeStorage = PreferenceManager.getDefaultSharedPreferences(context);
        }
        private SharedPreferences.Editor getEditor(){
            return themeStorage.edit();
        }
        int retrieveInt(String tag, int defValue){
            return themeStorage.getInt(tag, defValue);
        }
        void storeInt(String tag, int defValue){
            sEditor = getEditor();
            sEditor.putInt(tag, defValue);
            sEditor.commit();
        }
    }
    private TaskDatabase taskDatabase;
    private Task task;
    int priorityID;
    String classes;
    int typeID=0;

    public ArrayList<String> alarms;
    public String currentTime;
    public String currentDate;

    GlobalVars global = GlobalVars.getInstance();

    RecyclerView recyclerView;
    AlarmListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);
        alarms = new ArrayList<>();

        recyclerView = findViewById(R.id.TaskCreateRV);
        adapter=new AlarmListAdapter(alarms);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Date view shows today's date when user first opens screen
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month =c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        TextView dateText = findViewById(R.id.DateViewer);
        String dateText1 = month +"/"+day+"/"+year;
        dateText.setText(dateText1);

        //Slide bar code
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

        //Class chooser code
        Spinner classChooser = (Spinner) findViewById(R.id.DetailsClassSpinner);
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
                    String alarmsString="";
                    EditText nameText= (EditText) findViewById(R.id.NameAddText);
                    EditText notesText = (EditText) findViewById(R.id.NotesText);
                    TextView dateText = findViewById(R.id.DateViewer);
                    for(int i=0; i<alarms.size();i++){
                        alarmsString= alarmsString + alarms.get(i);
                    }
                    task = new Task(priorityID,nameText.getText().toString(),classes,dateText.getText().toString(),
                            notesText.getText().toString(),typeID,0,alarmsString);
                    new InsertTask(TaskCreate.this,task).execute();
                }
            });
    }

    protected void onStart(){
        super.onStart();
        determineTheme();
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

    public void HabitClick(View view){ typeID=0; }
    public void AssignClick(View view){ typeID=1; }
    public void ProjectClick(View view){ typeID=2; }
    //Returning Home
    public void toHomePage(View view){
        Intent toHomePage = new Intent(this, HomePage.class);
        startActivity(toHomePage);
    }
    public void showDatePickerDialog(View view){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(),"datePicker");

    }
    public void alarmSet1(View view){
        DialogFragment newFragment = new TimePickerFragmentAlarm();
        newFragment.show(getSupportFragmentManager(),"timePicker");

        //DialogFragment newFragment2 = new DatePickerFragmentAlarm();
        //newFragment2.show(getSupportFragmentManager(),"datePickerA");

        //String alarmTime = currentTime +currentDate + ":";
        //alarms.add(alarmTime);
    }
    public void alarmSet2(View view){
        DialogFragment newFragment = new DatePickerFragmentAlarm();
        newFragment.show(getSupportFragmentManager(),"datePickerA");

    }
    public void alarmSet3(View view){
        String alarmTime = currentTime + currentDate +":";
        alarms.add(alarmTime);
        global.setgAlarms(alarms);
        adapter.notifyDataSetChanged();

    }
    public void determineTheme(){
        int themeId = new SharedPreferencesManager(this).retrieveInt("themeId",1);
        if(themeId == 1){setTheme(R.style.SieveDefault);}
        else if(themeId == 2){setTheme(R.style.SieveAlternative);}
        else if(themeId == 3){setTheme(R.style.SieveCombined);}
        else if(themeId == 4){setTheme(R.style.SieveDark);}
        else if(themeId == 5){setTheme(R.style.SieveSimple);}
        else if(themeId == 6){setTheme(R.style.SieveCandy);}
        else{setTheme(R.style.SieveDefault);}
    }
}
