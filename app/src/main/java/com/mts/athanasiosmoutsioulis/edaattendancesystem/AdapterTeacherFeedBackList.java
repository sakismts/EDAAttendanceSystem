package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by AthanasiosMoutsioulis on 26/07/16.
 */
public class AdapterTeacherFeedBackList extends RecyclerView.Adapter<AdapterTeacherFeedBackList.ViewHolder> {
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private TeacherFeedBackFragment.OnFeedbackItemClickedListener listener;

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private Context context;
    private ArrayList<Lecture> attendanceList;

    private final static int TYPE_1 = 1;

    private AttendanceModel model = AttendanceModel.getOurInstance();


    public AdapterTeacherFeedBackList(Context ctx) {
        super();
        this.context = ctx;

        if (this.context instanceof TeacherFeedBackFragment.OnFeedbackItemClickedListener){
            listener= (TeacherFeedBackFragment.OnFeedbackItemClickedListener) context;

        }


    }

    @Override
    public int getItemViewType(int position) {

        return TYPE_1;
    }





    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader, txtDate, txtTime, txtLocation,tvAttendancesCount;
        public LinearLayout ll_attendance;



        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.tv_title);
            txtDate = (TextView) v.findViewById(R.id.tv_date);
            txtTime = (TextView) v.findViewById(R.id.tv_time);
            txtLocation = (TextView) v.findViewById(R.id.tv_location);
            tvAttendancesCount=(TextView) v.findViewById(R.id.tv_attendance_count);
            ll_attendance=(LinearLayout)v.findViewById(R.id.ll_teacher_attendance);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        listener.onFeedbackItemClicked(getAdapterPosition());
                    }
                }
            });



        }

        public void setData(final Lecture Lectureitems, int position) {
            txtHeader.setText(Lectureitems.getType() + " "+Lectureitems.getModule());
            txtLocation.setText(Lectureitems.getLocation());
            //date
            String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", Lectureitems.getStart());//Thursday
            String stringMonth = (String) android.text.format.DateFormat.format("MMM", Lectureitems.getStart()); //Jun
            final String intMonth = (String) android.text.format.DateFormat.format("MM", Lectureitems.getStart()); //06
            final String year = (String) android.text.format.DateFormat.format("yyyy", Lectureitems.getStart()); //2013
            final String day = (String) android.text.format.DateFormat.format("dd", Lectureitems.getStart()); //20
            txtDate.setText(day + "/" + intMonth + "/" + year);

            //start time
            Calendar c1 = Calendar.getInstance();
            c1.setTime(Lectureitems.getStart());
            final String start_hour= Integer.toString(c1.get(Calendar.HOUR_OF_DAY));
            // String start_hour = (String) android.text.format.DateFormat.format("HH", Lectureitems.getStart());
            final String start_minutes = (String) android.text.format.DateFormat.format("mm", Lectureitems.getStart());
            //end time
            Calendar c2 = Calendar.getInstance();
            c2.setTime(Lectureitems.getEnd());
            final String end_hour= Integer.toString(c2.get(Calendar.HOUR_OF_DAY));
            //String end_hour = (String) android.text.format.DateFormat.format("HH", Lectureitems.getEnd());
            final String end_minutes = (String) android.text.format.DateFormat.format("mm", Lectureitems.getEnd());
            txtTime.setText(start_hour + ":" + start_minutes + " - " + end_hour + ":" + end_minutes);
            tvAttendancesCount.setText(Integer.toString(Lectureitems.getStudentsFeedBack()));

        }


    }



    // Create new views (invoked by the layout manager)
    @Override
    public AdapterTeacherFeedBackList.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        View v = null;
        // create a new view
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_feedback_item, parent, false);
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
//        try {
//            model.opendb_read();
//            this.attendanceList = model.readAttendances();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        Lecture Lectureitem = null;
        Lectureitem = model.getTeacherAttendances().get(position);


        holder.setData(Lectureitem, position);

        //if the user load more data the animation isn't enable


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {


        return model.getTeacherAttendances().size();
    }





}


