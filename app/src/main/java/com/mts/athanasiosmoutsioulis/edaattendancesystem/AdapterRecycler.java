package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.sql.SQLException;
import java.util.Calendar;

/**
 * Created by AthanasiosMoutsioulis on 05/06/16.
 */
public class AdapterRecycler extends RecyclerView.Adapter<AdapterRecycler.ViewHolder> {
    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private Context context;
    private int tab_position;

    private final static int TYPE_1 = 1;

    private AttendanceModel model = AttendanceModel.getOurInstance();


    public AdapterRecycler(Context context, int tab_position) {
        super();
        this.context = context;
        this.tab_position = tab_position;
    }

    @Override
    public int getItemViewType(int position) {

        return TYPE_1;
    }

    public AdapterRecycler() {

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader, txtDate, txtTime, txtLocation;
        public ImageView type;


        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.tv_title);
            txtDate = (TextView) v.findViewById(R.id.tv_date);
            txtTime = (TextView) v.findViewById(R.id.tv_time);
            txtLocation = (TextView) v.findViewById(R.id.tv_location);
            type = (ImageView) v.findViewById(R.id.lecture_icon);

        }

        public void setData(Lecture Lectureitems, int position) {
            txtHeader.setText(Lectureitems.getType() + " " + Lectureitems.getTitle()+" "+ Lectureitems.getModule());
            txtLocation.setText(Lectureitems.getLocation());
            //date
            String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", Lectureitems.getStart());//Thursday
            String stringMonth = (String) android.text.format.DateFormat.format("MMM", Lectureitems.getStart()); //Jun
            String intMonth = (String) android.text.format.DateFormat.format("MM", Lectureitems.getStart()); //06
            String year = (String) android.text.format.DateFormat.format("yyyy", Lectureitems.getStart()); //2013
            String day = (String) android.text.format.DateFormat.format("dd", Lectureitems.getStart()); //20
            txtDate.setText(day + "/" + intMonth + "/" + year);
            //start time
            Calendar c1 = Calendar.getInstance();
            c1.setTime(Lectureitems.getStart());
            String start_hour= Integer.toString(c1.get(Calendar.HOUR_OF_DAY));
           // String start_hour = (String) android.text.format.DateFormat.format("HH", Lectureitems.getStart());
            String start_minutes = (String) android.text.format.DateFormat.format("mm", Lectureitems.getStart());
            //end time
            Calendar c2 = Calendar.getInstance();
            c2.setTime(Lectureitems.getEnd());
            String end_hour= Integer.toString(c2.get(Calendar.HOUR_OF_DAY));
            //String end_hour = (String) android.text.format.DateFormat.format("HH", Lectureitems.getEnd());
            String end_minutes = (String) android.text.format.DateFormat.format("mm", Lectureitems.getEnd());
            txtTime.setText(start_hour + ":" + start_minutes + " - " + end_hour + ":" + end_minutes);

            if (Lectureitems.getType().equals("WORKSHOP"))
                type.setImageResource(R.drawable.computer_lab);
            else
                type.setImageResource(R.drawable.lecture_icon);


        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public AdapterRecycler.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = null;
        // create a new view
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lecture_card_item, parent, false);
        }
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Lecture Lectureitem = null;
        switch (tab_position) {
            case 1:
                Lectureitem = model.getLectures_list_today().get(position);
                break;
            case 2:
                Lectureitem = model.getLectures_list_week().get(position);
                break;
            case 3:
                Lectureitem = model.getLectures_list_month().get(position);
                break;
            case 4:
                Lectureitem = model.getLectures_list().get(position);
                break;
        }


        holder.setData(Lectureitem, position);

        //if the user load more data the animation isn't enable


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //
        int size;
        switch (tab_position) {
            case 1:
                size = model.getLectures_list_today().size();
                break;
            case 2:
                size = model.getLectures_list_week().size();
                break;
            case 3:
                size = model.getLectures_list_month().size();
                break;
            case 4:
                size = model.getLectures_list().size();
                break;
            default:
                size = model.getLectures_list().size();
                break;

        }

        return size;
    }





}
