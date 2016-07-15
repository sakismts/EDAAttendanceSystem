package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class FacebookList extends AppCompatActivity implements AttendanceModel.OnGetUsersAttendListener, AttendanceModel.OnGetFacebookDetailsListener {
    Lecture current_lecture;
    AttendanceModel model=AttendanceModel.getOurInstance();
    ArrayList<FBStudent> students=new ArrayList<FBStudent>();
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_list);
        model.setGetUsersAttendListener(this);
        model.setGetFacebookDetailsListener(this);
        current_lecture=checkLectures();
        RecyclerView fbList=(RecyclerView)findViewById(R.id.fblist_recycler_view);
        header=(TextView)findViewById(R.id.tv_Header);
        fbList.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        fbList.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterFacebookList(this,students);

        fbList.setAdapter(mAdapter);

        if (current_lecture!=null){
            //////check for attendance////

            header.setText("Your classmates that have already attended to the "+current_lecture.getType()+" "+current_lecture.getModule()+" "+current_lecture.getTitle() + " are:");
            String module_id = current_lecture.getModule();
            Calendar c_start = Calendar.getInstance();
            c_start.setTime(current_lecture.getStart());
            String startDate = c_start.get(Calendar.DAY_OF_MONTH) + "/" + String.valueOf(c_start.get(Calendar.MONTH) + 1) + "/" + c_start.get(Calendar.YEAR) + "T" + c_start.get(Calendar.HOUR_OF_DAY) + ":" + c_start.get(Calendar.MINUTE);

            Calendar c_end = Calendar.getInstance();
            c_end.setTime(current_lecture.getEnd());
            String endDate = c_end.get(Calendar.DAY_OF_MONTH) + "/" + String.valueOf(c_end.get(Calendar.MONTH) + 1) + "/" + c_end.get(Calendar.YEAR) + "T" + c_end.get(Calendar.HOUR_OF_DAY) + ":" + c_end.get(Calendar.MINUTE);

            String uri = "http://greek-tour-guides.eu/ioannina/dissertation/getUsersAttend.php?&module_id=" + module_id +"&startDate=" + startDate + "&endDate=" + endDate;
            Log.i("URI", uri.toString());
            model.GetUsersAttends(uri);


        }
    }

    public Lecture checkLectures(){
        Calendar c = Calendar.getInstance();
        int c_day = c.get(Calendar.DAY_OF_MONTH);
        int c_month = c.get(Calendar.MONTH)+1;
        int c_year = c.get(Calendar.YEAR);
        int c_hour = c.get(Calendar.HOUR_OF_DAY);
        int c_seconds = c.get(Calendar.SECOND);

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        for (Lecture tmp: model.getLectures_list_today()){
            c1.setTime(tmp.getStart());
            c2.setTime(tmp.getEnd());
            int day = c1.get(Calendar.DAY_OF_MONTH);
            int month = c1.get(Calendar.MONTH)+1;
            int year = c1.get(Calendar.YEAR);

            if (day==c_day && month==c_month && year==c_year) {
                int c_hour1 = c1.get(Calendar.HOUR_OF_DAY);

                int c_hour2 = c2.get(Calendar.HOUR_OF_DAY);

                if (c_hour>=c1.get(Calendar.HOUR_OF_DAY) && c_hour< c2.get(Calendar.HOUR_OF_DAY)){
                    System.out.println("Day :"+c_day+ " - " +day+", Month :"+c_month+ " - " +month+", Hour :"+c_hour+" - "+c1.get(Calendar.HOUR_OF_DAY));
                    Log.i("Lecture", "Current lecture is at :" + tmp.getLocation());

                    return tmp;
                }
            }
        }
        return null;

    }

    @Override
    public void OnGetUsersAttendListener(JSONObject response) {
        System.out.println(response);
        int result=-1;
        try {
            result= response.getInt("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result==1){
            try {
                JSONArray tmp_student = response.getJSONArray("students");
                for (int i=0; i<tmp_student.length();i++){
                    JSONObject objjson = tmp_student.getJSONObject(i);
                    String id = objjson.getString("student");
                    String uri = "http://greek-tour-guides.eu/ioannina/dissertation/getFBDetails.php?id="+id;
                    model.getfbDetails(uri);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onGetFacebookDetailsListener(String fbid, String fbName) {
        System.out.println(fbid+" "+fbName);
        students.add(new FBStudent(fbid, fbName));
        System.out.println("the total sum of students is :" + students.size());
        mAdapter.notifyDataSetChanged();

    }
}
