package com.mts.athanasiosmoutsioulis.edaattendancesystem;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherFeedbackSheetFragment extends Fragment implements AttendanceModel.OnGetTeacherSingleAttendance {

    AttendanceModel model=AttendanceModel.getOurInstance();
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextView header,status;
    private String modId,modType;
    ProgressDialog progress;
    public TeacherFeedbackSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_teacher_feedback_sheet, container, false);
        model.setGetTeacherSingleAttendanceListener(this);


        RecyclerView attendRecycler = (RecyclerView) view.findViewById(R.id.fdsheet_recycler_view);
        header=(TextView)view.findViewById(R.id.tv_feedbackSheet_header);
        status=(TextView)view.findViewById(R.id.tv_feedbackSheet_status);
        status.setVisibility(View.GONE);
        attendRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        attendRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterFeedbackSheet(getActivity());
        attendRecycler.setAdapter(mAdapter);
        return view;
    }

    public void updateStudentAttendances(String moduleId,String moduleType,String startDate,String endDate){
        String uri = "http://greek-tour-guides.eu/ioannina/dissertation/getTeacherSingleAttendances.php?module_id="+ moduleId+"&startDate="+startDate+"&endDate="+endDate;
        Log.i("URI", uri.toString());
        progress = ProgressDialog.show(getActivity(), "Feedbacks",
                "Loading feedbacks", true);
        header.setText(moduleType + " " + moduleId);
        model.getTeacherFeedback().clear();
        model.getTeacherSingleAttendace(uri);
        modId=moduleId;
        modType=moduleType;
    }

    @Override
    public void onGetTeacherSingleAttendance(boolean signed) {
        progress.dismiss();
        if (model.getTeacherFeedback().size()==0){
            status.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
    }
}
