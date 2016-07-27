package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by AthanasiosMoutsioulis on 26/07/16.
 */
public class AdapterFeedbackSheet extends RecyclerView.Adapter<AdapterFeedbackSheet.ViewHolder> {
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


    public AdapterFeedbackSheet(Context context) {
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
        public TextView txtStudentID, txtfeedback;




        public ViewHolder(View v) {
            super(v);
            txtStudentID = (TextView) v.findViewById(R.id.tv_fd_sheet_studentID);
            txtfeedback = (TextView) v.findViewById(R.id.tv_fd_sheet_feedback);

        }

        public void setData(final Attendance item, int position) {
            if(item.getAnonymous().equals("false")){
                txtStudentID.setText("Anonymous");
            }else{
                txtStudentID.setText(item.getStudentId());
            }

                txtfeedback.setText(item.getFeedback());



        }


    }



    // Create new views (invoked by the layout manager)
    @Override
    public AdapterFeedbackSheet.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        View v = null;
        // create a new view
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_sheet_item, parent, false);
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
        item = model.getTeacherFeedback().get(position);


        holder.setData(item, position);

        //if the user load more data the animation isn't enable


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {


        return model.getTeacherFeedback().size();
    }





}



