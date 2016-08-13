package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.Context;
import android.content.Intent;
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
 * Created by AthanasiosMoutsioulis on 25/07/16.
 */
public class AdapterAttendanceSheet extends RecyclerView.Adapter<AdapterAttendanceSheet.ViewHolder> {
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private Context context;


    private final static int TYPE_1 = 1;

    private AttendanceModel model = AttendanceModel.getOurInstance();


    public AdapterAttendanceSheet(Context context) {
        super();
        this.context = context;


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
        public TextView txtStudentID, txtvalid,txtStudentName;




        public ViewHolder(View v) {
            super(v);
            txtStudentID = (TextView) v.findViewById(R.id.tv_studentId_attendance);
            txtvalid = (TextView) v.findViewById(R.id.tv_valid_attendance);
            txtStudentName=(TextView) v.findViewById(R.id.tv_studentName_attendance);

        }

        public void setData(final Attendance item, int position) {
            txtStudentID.setText(item.getStudentId());
            txtvalid.setText(item.getValid());
            txtStudentName.setText(item.getFullName());

        }


    }



    // Create new views (invoked by the layout manager)
    @Override
    public AdapterAttendanceSheet.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        View v = null;
        // create a new view
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendace_sheet_item, parent, false);
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

        Attendance item = null;
        item = model.getStudents_Attendance_list().get(position);


        holder.setData(item, position);

        //if the user load more data the animation isn't enable


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {


        return model.getStudents_Attendance_list().size();
    }





}


