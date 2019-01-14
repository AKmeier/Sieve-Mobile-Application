package com.example.a53914.sievemobileapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import android.widget.TextView;

import com.example.a53914.sievemobileapplication.db.Class;
import com.example.a53914.sievemobileapplication.db.ClassDatabase;
import com.example.a53914.sievemobileapplication.db.TaskDatabase;
import com.example.a53914.sievemobileapplication.db.Task;
import com.example.a53914.sievemobileapplication.fragments.ClassCreationDialog;
import com.example.a53914.sievemobileapplication.fragments.DatePickerFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskCreate extends AppCompatActivity {
    private TaskDatabase taskDatabase;
    private Task task;
    private final ArrayList<String> classList = new ArrayList<>();
    int priorityID;
    private String classes;
    int typeID=0;
    private ClassDatabase classDatabase;
    GlobalVars global = GlobalVars.getInstance();

    private Spinner classChooser;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);

        createClassList();

        /* Shows today's date in the date selection box when user first opens screen */
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month =c.get(Calendar.MONTH);
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
        classChooser = findViewById(R.id.DetailsClassSpinner);
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, classList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classChooser.setAdapter(adapter);
        classChooser.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if ((parent.getItemAtPosition(pos)).toString().equals("Create New Class")){
                    ClassCreationDialog dialog = new ClassCreationDialog();
                    dialog.show(getSupportFragmentManager(), "ClassCreationDialog");
                } else {
                    classes = (parent.getItemAtPosition(pos)).toString();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* initialize Task Database */
        taskDatabase = TaskDatabase.getInstance(TaskCreate.this);
        Button button = findViewById(R.id.CreateButton);
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    EditText nameText= (EditText) findViewById(R.id.NameAddText);
                    EditText notesText = (EditText) findViewById(R.id.NotesText);
                    TextView dateText = findViewById(R.id.DateViewer);
                    task = new Task(priorityID,nameText.getText().toString(),classes,dateText.getText().toString(),
                            notesText.getText().toString(),typeID);
                    new InsertTask(TaskCreate.this,task).execute();
                }
            });
    }

    /** Creates a new class list and updates the spinner*/
    private void refreshSpinner(){
        createClassList();
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, classList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.notifyDataSetChanged();
    }

    /** Creates the array of classes that the spinner displays */
    private void createClassList() {
        classList.clear();
        classDatabase = ClassDatabase.getInstance(this);
        List<Class> clses = classDatabase.classDao().getAll();
        classList.add("Select Class");
        for (Class cls : clses) {
            classList.add(cls.getName());
        }
        classList.add("Create New Class");
    }

    /** Calls the AsyncTask InsertClass, that cannot be called from other classes */
    public void callInsertClass(Class mClass){
        new InsertClass(TaskCreate.this, mClass).execute();
    }

    /** Puts the class into the class database */
    private static class InsertClass extends AsyncTask<Void, Void,Boolean> {
        private final WeakReference<TaskCreate> activityReference;
        private final Class cls;
        InsertClass(TaskCreate context, Class mClass){
            activityReference = new WeakReference<>(context);
            cls = mClass;
        }
        @Override
        protected Boolean doInBackground(Void...objs){
            activityReference.get().classDatabase.classDao().insertAll(cls);
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

}
