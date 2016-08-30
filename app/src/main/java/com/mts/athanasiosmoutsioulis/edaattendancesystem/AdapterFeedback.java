package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import static com.mts.athanasiosmoutsioulis.edaattendancesystem.R.color.colorAccent;

/**
 * Created by AthanasiosMoutsioulis on 08/07/16.
 */
public class AdapterFeedback  extends RecyclerView.Adapter<AdapterFeedback.ViewHolder> {
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    String feedback_text = "";
    String stars="";
    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private Context context;
    private ArrayList<Lecture> attendanceList;

    private final static int TYPE_1 = 1;

    private AttendanceModel model = AttendanceModel.getOurInstance();


    public AdapterFeedback(Context context) {
        super();
        this.context = context;
        this.OnupdateFeedbackListener = ((MyListener) context);
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

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
        public TextView txtHeader, txtDate, txtTime, txtLocation;
        Button btn_feedback;




        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.tv_title);
            txtDate = (TextView) v.findViewById(R.id.tv_date);
            txtTime = (TextView) v.findViewById(R.id.tv_time);
            txtLocation = (TextView) v.findViewById(R.id.tv_location);
            btn_feedback= (Button)v.findViewById(R.id.btn_feedback);




        }

        public void setData(final Lecture Lectureitems, int position) {
            txtHeader.setText(Lectureitems.getType() + " " + Lectureitems.getTitle()+" "+ Lectureitems.getModule());
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


            btn_feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(Lectureitems.getStart());
                    // custom dialog
                    // final Dialog dialog = new Dialog(context);
                    // dialog.setContentView(R.layout.feedback_dialog);

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);

                    // Get the layout inflater
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View view = inflater.inflate(R.layout.feedback_dialog, null);
                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    dialog.setView(view);


                    dialog.setTitle("Feedback");


                    // set the custom dialog components - text, image and button

                    LinearLayout ll_item = (LinearLayout) view.findViewById(R.id.ll_dialog);

                    final TextView rate_desc = (TextView) view.findViewById(R.id.rate_desc);
                    RatingBar ratingbar=(RatingBar) view.findViewById(R.id.ratingBar);
                    ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        public void onRatingChanged(RatingBar ratingBar, float rating,
                                                    boolean fromUser) {

                           stars=String.valueOf(rating);
                            int rate= (int)rating;
                            switch (rate){
                                case 1:
                                    rate_desc.setText("Very bored");
                                    break;
                                case 2:
                                    rate_desc.setText("Bored");
                                    break;
                                case 3:
                                    rate_desc.setText("Neutral");
                                    break;
                                case 4:
                                    rate_desc.setText("Interested");
                                    break;
                                case 5:
                                    rate_desc.setText("Very interested");
                                    break;


                            }

                        }
                    });


                    TextView title = (TextView) view.findViewById(R.id.tv_title);
                    title.setText(Lectureitems.getType() + " " + Lectureitems.getTitle() + " " + Lectureitems.getModule());
                    System.out.println(Lectureitems.getType() + " " + Lectureitems.getTitle() + " " + Lectureitems.getModule());
                    TextView location = (TextView) view.findViewById(R.id.tv_location);
                    location.setText(Lectureitems.getLocation());
                    TextView date = (TextView) view.findViewById(R.id.tv_date);
                    date.setText(day + "/" + intMonth + "/" + year);
                    TextView time = (TextView) view.findViewById(R.id.tv_time);
                    time.setText(start_hour + ":" + start_minutes + " - " + end_hour + ":" + end_minutes);
                    final AlertDialog ad = dialog.show();
                    final EditText et_feedback = (EditText) view.findViewById(R.id.et_feedback);


                    et_feedback.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                hideKeyboard(v);
                            }
                        }
                    });

                    Button dialogButton = (Button) view.findViewById(R.id.btn_send);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (OnupdateFeedbackListener != null) {
                                ////preparing the URI



                                feedback_text = et_feedback.getText().toString();

//                                et_feedback.addTextChangedListener(new TextWatcher() {
//                                    public void afterTextChanged(Editable s) {
//                                        feedback_text =s.toString();
//                                        System.out.println(s.toString());
//                                    }
//
//                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                                    }
//
//                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                                    }
//                                });
                                System.out.println(feedback_text);

                                CheckBox anonymous = (CheckBox) view.findViewById(R.id.check_id);
                                String share_ID = null;
                                if (anonymous.isChecked())
                                    share_ID = "false";
                                else
                                    share_ID = "true";
                                String user_id = sharedpreferences.getString("id", "User");
                                String module_id = Lectureitems.getModule();
                                String lecture_type = Lectureitems.getType();
                                String mylocation = Lectureitems.getLocation();
                                Calendar c_start = Calendar.getInstance();
                                c_start.setTime(Lectureitems.getStart());
                                String startDate = c_start.get(Calendar.DAY_OF_MONTH) + "/" + String.valueOf(c_start.get(Calendar.MONTH) + 1) + "/" + c_start.get(Calendar.YEAR) + "T" + c_start.get(Calendar.HOUR_OF_DAY) + ":" + c_start.get(Calendar.MINUTE);

                                Calendar c_end = Calendar.getInstance();
                                c_end.setTime(Lectureitems.getEnd());
                                String endDate = c_end.get(Calendar.DAY_OF_MONTH) + "/" + String.valueOf(c_end.get(Calendar.MONTH) + 1) + "/" + c_end.get(Calendar.YEAR) + "T" + c_end.get(Calendar.HOUR_OF_DAY) + ":" + c_end.get(Calendar.MINUTE);

                                final String uri = "http://greek-tour-guides.eu/ioannina/dissertation/updateFeedback.php?student_id=" + user_id + "&module_id=" + module_id + "&lectureType=" + lecture_type + "&location=" + mylocation + "&startDate=" + startDate + "&endDate=" + endDate + "&feedback=" + feedback_text + "&shareid=" + share_ID+"&stars="+stars;

                                Log.i("URI", uri.toString());
                                OnupdateFeedbackListener.onupdateFeedback(uri, Lectureitems);
                                ad.dismiss();
                            }
                        }
                    });


//                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//                    lp.copyFrom(dialog.getWindow().getAttributes());
//                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;

                    //dialog.getWindow().setAttributes(lp);
                }
            });


        }


    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterFeedback.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = null;
        // create a new view
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_carditem, parent, false);
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
        Lectureitem = model.getAttendances_list().get(position);


        holder.setData(Lectureitem, position);

        //if the user load more data the animation isn't enable


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {


        return model.getAttendances_list().size();
    }


    public void setOnupdateFeedbackListener(MyListener onupdateFeedbackListener) {
        OnupdateFeedbackListener = onupdateFeedbackListener;
    }

    public MyListener OnupdateFeedbackListener;


    public interface  MyListener{
        void onupdateFeedback(String uri,Lecture lectureItem);

    }


}
