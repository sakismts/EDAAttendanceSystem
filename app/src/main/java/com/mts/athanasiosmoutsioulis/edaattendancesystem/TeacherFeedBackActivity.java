package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;

public class TeacherFeedBackActivity extends AppCompatActivity implements TeacherFeedBackFragment.OnFeedbackItemClickedListener {

    AttendanceModel model=AttendanceModel.getOurInstance();
    public boolean hasTwoPanes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_feed_back);
        if(findViewById(R.id.list_studentsFD_fragment)==null){
            hasTwoPanes=false;

        }else{
            hasTwoPanes=true;
            ////load the first item to the second fragment


        }
    }

    @Override
    public void onFeedbackItemClicked(int position) {
        Calendar c_start = Calendar.getInstance();
        c_start.setTime(model.getTeacherAttendances().get(position).getStart());
        String startDate=c_start.get(Calendar.DAY_OF_MONTH)+"/"+String.valueOf(c_start.get(Calendar.MONTH)+1)+"/"+c_start.get(Calendar.YEAR)+"T"+c_start.get(Calendar.HOUR_OF_DAY)+":"+c_start.get(Calendar.MINUTE);

        Calendar c_end = Calendar.getInstance();
        c_end.setTime(model.getTeacherAttendances().get(position).getEnd());
        String endDate=c_end.get(Calendar.DAY_OF_MONTH)+"/"+String.valueOf(c_end.get(Calendar.MONTH)+1)+"/"+c_end.get(Calendar.YEAR)+"T"+c_end.get(Calendar.HOUR_OF_DAY)+":"+c_end.get(Calendar.MINUTE);

        if(hasTwoPanes){
            FragmentManager fragmentManager= getFragmentManager();
            TeacherFeedbackSheetFragment fragment= (TeacherFeedbackSheetFragment) fragmentManager.findFragmentById(R.id.list_studentsFD_fragment);
            fragment.updateStudentAttendances(model.getTeacherAttendances().get(position).getModule(),model.getTeacherAttendances().get(position).getType(),startDate,endDate);


        }else{


            Intent intent=new Intent(this,TeacherFeedbackSheetActivity.class);
            intent.putExtra("moduleID", model.getTeacherAttendances().get(position).getModule());
            intent.putExtra("moduleType", model.getTeacherAttendances().get(position).getType());
            intent.putExtra("startDate",startDate);
            intent.putExtra("endDate",endDate);
            startActivity(intent);
        }
    }
}
