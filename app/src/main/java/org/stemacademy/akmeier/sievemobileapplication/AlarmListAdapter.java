package org.stemacademy.akmeier.sievemobileapplication;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.StrictMath.toIntExact;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.ViewHolder>{
    GlobalVars global = GlobalVars.getInstance();

    /** Assigns contents of input database to a local database */
    private final ArrayList<String> mAlarms;
    public AlarmListAdapter(ArrayList<String> alarms) {
        mAlarms= alarms;
    }


    /** Creates the TaskViewHolder */
    @NonNull
    @Override
    public AlarmListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View alarmView = inflater.inflate(R.layout.item_alarms_list, parent, false);
        return new AlarmListAdapter.ViewHolder(alarmView, 1);

    }


    /** Gets values from the Task list*/
    @Override
    public void onBindViewHolder(@NonNull AlarmListAdapter.ViewHolder viewHolder, int position) {
        String alarm = mAlarms.get(position);
        int scheduleDay=0;
        int scheduleMonth=0;
        int scheduleYear=0;
        int scheduleHour=0;
        int scheduleMinute=0;

        List<String> alarmseparate = new ArrayList<>(Arrays.asList(alarm.split("/")));
        for (int II = 0; II < alarmseparate.size(); II++) {
            if (II == 0) {
                scheduleHour = Integer.parseInt(alarmseparate.get(II));
            } else if (II == 1) {
                scheduleMinute = Integer.parseInt(alarmseparate.get(II));
            }else if (II == 2) {
                scheduleYear = Integer.parseInt(alarmseparate.get(II));
            } else if (II == 3) {
                scheduleMonth = Integer.parseInt(alarmseparate.get(II));
            } else if (II == 4) {
                ArrayList<String> prelimString =new ArrayList<>( Arrays.asList(alarmseparate.get(II).split(":")));
                scheduleDay = Integer.parseInt(prelimString.get(0));
            }
        }
        int LastNotificationID = toIntExact(SystemClock.uptimeMillis() / 1000);
        String AlarmText = scheduleHour + ":" + scheduleMinute + " " + (scheduleMonth + 1) + "/" + scheduleDay + "/" + scheduleYear;
        TextView textView = viewHolder.alarmContent;
        textView.setText(AlarmText);
        alarmseparate.clear();
    }

    /** Returns the total count of items in the list */
    @Override
    public int getItemCount() {
        if(mAlarms==null){
            return 0;
        }else {
            return mAlarms.size();
        }
}

    /** Assigns layout values to current task item */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView alarmTitle;
        final TextView alarmContent;
        int alarmID;

        ViewHolder(View itemView, int alarmID) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any TaskViewHolder instance.
            super(itemView);

            alarmTitle = itemView.findViewById(R.id.alarmTitle);
            alarmContent = itemView.findViewById(R.id.alarmContent);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            removeAlarm(getAdapterPosition());
        }
    }

    public void removeAlarm(int position){
        mAlarms.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mAlarms.size());
    }
}
