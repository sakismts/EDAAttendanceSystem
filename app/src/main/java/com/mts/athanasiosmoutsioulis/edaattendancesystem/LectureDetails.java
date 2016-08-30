package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class LectureDetails extends AppCompatActivity {
    TextView title,time,location,date,description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_details);
        Intent i = getIntent();
        Lecture lecture = (Lecture)i.getSerializableExtra("LectureDetails");
        title=(TextView)findViewById(R.id.tv_details_title);
        time=(TextView)findViewById(R.id.tv_details_time);
        location=(TextView)findViewById(R.id.tv_details_location);
        date=(TextView)findViewById(R.id.tv_details_date);
        description=(TextView)findViewById(R.id.tv_detail_description);

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);


        title.setText(lecture.getType() + " " + lecture.getTitle() + " " + lecture.getModule());
        location.setText(lecture.getLocation());

        String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", lecture.getStart());//Thursday
        String stringMonth = (String) android.text.format.DateFormat.format("MMM", lecture.getStart()); //Jun
        String intMonth = (String) android.text.format.DateFormat.format("MM", lecture.getStart()); //06
        String year = (String) android.text.format.DateFormat.format("yyyy", lecture.getStart()); //2013
        String day = (String) android.text.format.DateFormat.format("dd", lecture.getStart()); //20
        date.setText(day + "/" + intMonth + "/" + year);

        Calendar c1 = Calendar.getInstance();
        c1.setTime(lecture.getStart());
        String start_hour= Integer.toString(c1.get(Calendar.HOUR_OF_DAY));
        // String start_hour = (String) android.text.format.DateFormat.format("HH", Lectureitems.getStart());
        String start_minutes = (String) android.text.format.DateFormat.format("mm", lecture.getStart());
        //end time
        Calendar c2 = Calendar.getInstance();
        c2.setTime(lecture.getEnd());
        String end_hour= Integer.toString(c2.get(Calendar.HOUR_OF_DAY));
        //String end_hour = (String) android.text.format.DateFormat.format("HH", Lectureitems.getEnd());
        String end_minutes = (String) android.text.format.DateFormat.format("mm", lecture.getEnd());
        time.setText(start_hour + ":" + start_minutes + " - " + end_hour + ":" + end_minutes);


         String map="";
        String substr=lecture.getDescription().substring(lecture.getDescription().indexOf("http"));

        int index=0;
        for(int j=0;j<substr.length();j++) {
            char c = substr.charAt(j);
            if(c!='\n'){
                map=map+c;
            }else{
                index=j;
                break;
            }
        }
        final String finalMap = map;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link_map= new Intent(Intent.ACTION_VIEW, Uri.parse(finalMap));
                startActivity(link_map);
            }
        });
        description.setText(substr.substring(index));
    }
}
