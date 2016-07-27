package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
 * Activities that contain this fragment must implement the
 * {@link TeacherFeedBackFragment.OnFeedbackItemClickedListener} interface
 * to handle interaction events.
 */
public class TeacherFeedBackFragment extends Fragment implements AttendanceModel.OnGetTeacherAttendances{
    AttendanceModel model=AttendanceModel.getOurInstance();
    String module;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    ProgressDialog progress;

    private OnFeedbackItemClickedListener mListener;

    public TeacherFeedBackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_teacher_feed_back, container, false);
        RecyclerView attendRecycler = (RecyclerView) view.findViewById(R.id.feed_recycler_view);
        attendRecycler.setHasFixedSize(true);
        model.getTeacherAttendances().clear();
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        attendRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterTeacherFeedBackList(getActivity());

        attendRecycler.setAdapter(mAdapter);
        model.setGetTeacherAttendancesListener(this);
        Intent intent = getActivity().getIntent();
        module=intent.getStringExtra("module");
        String uri = "http://greek-tour-guides.eu/ioannina/dissertation/getTeacherAttendances.php?module_id="+ module;
        Log.i("URI", uri.toString());
        progress = ProgressDialog.show(getActivity(), "Feedbacks List",
                "Loading feedbacks for "+module, true);
        model.getTeacherAttendaces(uri);
        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFeedbackItemClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onGetTeacherAttendances(boolean signed) {
        progress.dismiss();
        mAdapter.notifyDataSetChanged();
        if(!model.getTeacherAttendances().isEmpty()){
            if(((TeacherFeedBackActivity)getActivity()).hasTwoPanes)
                 ((TeacherFeedBackActivity)getActivity()).onFeedbackItemClicked(0);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFeedbackItemClickedListener {
        // TODO: Update argument type and name
        public void onFeedbackItemClicked(int position);
    }

}
