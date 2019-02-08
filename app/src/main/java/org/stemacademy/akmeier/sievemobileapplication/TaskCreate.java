package org.stemacademy.akmeier.sievemobileapplication;

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

import org.stemacademy.akmeier.sievemobileapplication.db.Classroom;
import org.stemacademy.akmeier.sievemobileapplication.db.ClassroomDatabase;
import org.stemacademy.akmeier.sievemobileapplication.db.TaskDatabase;
import org.stemacademy.akmeier.sievemobileapplication.db.Task;
import org.stemacademy.akmeier.sievemobileapplication.fragments.ClassroomCreationDialog;
import org.stemacademy.akmeier.sievemobileapplication.db.TimePickerFragmentAlarm;
import org.stemacademy.akmeier.sievemobileapplication.fragments.DatePickerFragment;
import org.stemacademy.akmeier.sievemobileapplication.fragments.DatePickerFragmentAlarm;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private final ArrayList<String> classroomList = new ArrayList<>();
    int priorityID;
    private String classroom;
    int typeID=0;
    private ClassroomDatabase classroomDatabase;
    GlobalVars global = GlobalVars.getInstance();

    private Spinner classroomChooser;
    private ArrayAdapter classroomAdapter;

    public ArrayList<String> alarms;
    public String currentTime;
    public String currentDate;
    RecyclerView recyclerView;
    AlarmListAdapter alarmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        determineTheme();
        setContentView(R.layout.activity_task_create);
        alarms = new ArrayList<>();

        recyclerView = findViewById(R.id.TaskCreateRV);
        alarmAdapter = new AlarmListAdapter(alarms);
        recyclerView.setAdapter(alarmAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /* Shows today's date in the date selection box when user first opens screen */
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month =c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        TextView dateText = findViewById(R.id.DateViewer);
        String dateText1 = month +"/"+day+"/"+year;
        dateText.setText(dateText1);

        /* Manages the priority slide bar */
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

        /* Fills the spinner and allows user to select a class from the class database */
        createClassroomList();
        classroomChooser = findViewById(R.id.DetailsClassSpinner);
        classroomAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, classroomList);
        classroomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classroomChooser.setAdapter(classroomAdapter);
        classroomChooser.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if ((parent.getItemAtPosition(pos)).toString().equals("Create New Classroom")){
                    ClassroomCreationDialog dialog = new ClassroomCreationDialog();
                    dialog.PARENT = "TaskCreate";
                    dialog.show(getSupportFragmentManager(), "ClassroomCreationDialog");
                } else {
                    classroom = (parent.getItemAtPosition(pos)).toString();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* initialize Task Database */
        taskDatabase = TaskDatabase.getInstance(TaskCreate.this);
        /*Add Task to Database*/
        Button button = findViewById(R.id.CreateButton);
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
                    task = new Task(priorityID,nameText.getText().toString(), classroom,dateText.getText().toString(),
                            notesText.getText().toString(),typeID,0,alarmsString);
                    new InsertTask(TaskCreate.this,task).execute();
                }
            });
    }

    /** Creates a new class list and updates the spinner*/
    private void refreshSpinner(){
        createClassroomList();
        classroomAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, classroomList);
        classroomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classroomAdapter.notifyDataSetChanged();
    }

    /** Creates the array of currentClassroom that the spinner displays */
    private void createClassroomList() {
        classroomList.clear();
        classroomDatabase = ClassroomDatabase.getInstance(this);
        List<Classroom> classrooms = classroomDatabase.classroomDao().getAll();
        classroomList.add("Select Classroom");
        for (Classroom mClassroom : classrooms) {
            classroomList.add(mClassroom.getName());
        }
        classroomList.add("Create New Classroom");
    }

    /** Calls the AsyncTask InsertClass, that cannot be called from other currentClassroom */
    public void callInsertClassroom(Classroom mClassroom){
        new InsertClass(TaskCreate.this, mClassroom).execute();
    }

    /** Puts the class into the class database */
    private static class InsertClass extends AsyncTask<Void, Void,Boolean> {
        private final WeakReference<TaskCreate> activityReference;
        private final Classroom cls;
        InsertClass(TaskCreate context, Classroom mClassroom){
            activityReference = new WeakReference<>(context);
            cls = mClassroom;
        }
        @Override
        protected Boolean doInBackground(Void...objs){
            activityReference.get().classroomDatabase.classroomDao().insertAll(cls);
            return true;
        }
        @Override
        protected void onPostExecute(Boolean bool){
            if(bool){
                Log.d("TaskCreate","The Async Task has finished!");
                Log.d("TaskCreate", cls.toString());
                activityReference.get().refreshSpinner();
            }
        }
    }

    protected void onStart(){
        super.onStart();
        determineTheme();
    }

    /** Close Task Create Activity */
    private void setResultTask(Task task, int flag){
        setResult(flag,new Intent().putExtra("task",task.toString()));
        finish();
    }

    /** Puts Task into Task Database */
    private static class InsertTask extends AsyncTask<Void, Void,Boolean> {
        private final WeakReference<TaskCreate> activityReference;
        private final Task task;
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
                activityReference.get().setResultTask(task,1);
                Log.d("TaskCreate","The Async Task has finished!");
                Log.d("TaskCreate",task.toString());
                activityReference.get().finish();
            }
        }
    }

    public void HabitClick(View view){ typeID=0; }
    public void AssignClick(View view){ typeID=1; }
    public void ProjectClick(View view){ typeID=2; }

    public void toHomePage(View view){
        Intent toHomePage = new Intent(this, HomePage.class);
        startActivity(toHomePage);
    }

    /** Shows a Calendar Dialog for user to select a date */
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
        classroomAdapter.notifyDataSetChanged();

    }
    public void determineTheme(){
        int themeId = new SharedPreferencesManager(this).retrieveInt("themeId",1);
        if(themeId == 1){setTheme(R.style.SieveDefault);}
        else if(themeId == 2){setTheme(R.style.SieveAlternative);}
        else if(themeId == 3){setTheme(R.style.SieveTwilight);}
        else if(themeId == 4){setTheme(R.style.SieveDark);}
        else if(themeId == 5){setTheme(R.style.SieveSimple);}
        else if(themeId == 6){setTheme(R.style.SieveOlive);}
        else{setTheme(R.style.SieveDefault);}
    }
}
