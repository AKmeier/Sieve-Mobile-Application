package com.example.a53914.sievemobileapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a53914.sievemobileapplication.db.Task;

import java.util.List;

/**
 * Receives values from the SQL database and assigns the code to multiple views. These views are
 * compiled into a ViewHolder in preparation for use by the RecyclerView.
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    /** Assigns contents of input database to a local database */
    private final List<Task> mTasks;
    public TaskListAdapter(List<Task> tasks) {
        mTasks = tasks;
    }

    /** Creates the ViewHolder */
    @NonNull
    @Override
    public TaskListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View taskView = inflater.inflate(R.layout.item_task_list, parent, false);
        return new ViewHolder(taskView);
    }

    /** Gets values from the Task list*/
    @Override
    public void onBindViewHolder(@NonNull TaskListAdapter.ViewHolder viewHolder, int position) {
        Task task = mTasks.get(position);

        TextView textView = viewHolder.taskTitle;
        textView.setText(task.getNameID());

        ImageView imageView = viewHolder.taskPriority;
        TypedArray mPriority = textView.getContext().getResources().obtainTypedArray(R.array.priority);
        imageView.setImageResource(mPriority.getResourceId(task.getPriority(), -1));
        mPriority.recycle();
    }

    /** Returns the total count of items in the list */
    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    /** Assigns layout values to current task item */
    public class ViewHolder extends RecyclerView.ViewHolder{
        final TextView taskTitle;
        final ImageView taskPriority;
        final Button detailsButton;

        ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            taskTitle = itemView.findViewById(R.id.Title);
            taskPriority = itemView.findViewById(R.id.Priority);
            detailsButton = itemView.findViewById(R.id.ToDetails);

        }

    }

}
