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


/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceSheetFragment extends Fragment implements AttendanceModel.OnGetTeacherSingleAttendance {

    AttendanceModel model=AttendanceModel.getOurInstance();
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    ProgressDialog progress;
    public AttendanceSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_attendance_sheet, container, false);
        model.setGetTeacherSingleAttendanceListener(this);


        RecyclerView attendRecycler = (RecyclerView) view.findViewById(R.id.sheet_recycler_view);
        progress = ProgressDialog.show(getActivity(), "Attendacnes",
                "Loading Student's Attendances", true);
        attendRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        attendRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterAttendanceSheet(getActivity());
        attendRecycler.addItemDecoration(new DividerItemDecoration(getResources()));
        attendRecycler.setAdapter(mAdapter);
        return view;
    }

    public void updateStudentAttendances(String moduleId,String startDate,String endDate){
        String uri = "http://greek-tour-guides.eu/ioannina/dissertation/getTeacherSingleAttendances.php?module_id="+ moduleId+"&startDate="+startDate+"&endDate="+endDate;
        Log.i("URI", uri.toString());
        model.getStudents_Attendance_list().clear();
        model.getTeacherSingleAttendace(uri);
    }


    @Override
    public void onGetTeacherSingleAttendance(boolean signed) {
        mAdapter.notifyDataSetChanged();
        progress.dismiss();
    }
}
