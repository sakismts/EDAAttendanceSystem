package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragmentTeacher.OnTeacherFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MainFragmentTeacher extends Fragment  {
    Spinner spinner;
    Set<String> myModules;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    private OnTeacherFragmentInteractionListener mListener;
    TextView id,Name;
    LinearLayout ll_attendaces,ll_feedback,ll_chart;
    AttendanceModel model = AttendanceModel.getOurInstance();
    ArrayAdapter<String> spineradapter;
    String[] state;

    public void setModule(String module) {
        this.module = module;
    }

    public String module="";

    public MainFragmentTeacher() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_main_fragment_teacher, container, false);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        myModules = sharedpreferences.getStringSet("myModules", new HashSet<String>());

        id = (TextView)view.findViewById(R.id.tv_id);
        Name = (TextView)view.findViewById(R.id.tv_fbName);
        ll_attendaces = (LinearLayout)view.findViewById(R.id.teacher_ll_attendances);
        ll_feedback=(LinearLayout)view.findViewById(R.id.teacher_ll_feedback);
        ll_chart=(LinearLayout)view.findViewById(R.id.teacher_ll_charts);
        ll_attendaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attendancesList= new Intent(getActivity(),AttendancesList.class);
                attendancesList.putExtra("module",module);
                startActivity(attendancesList);
            }
        });
        ll_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedbackList= new Intent(getActivity(),TeacherFeedBackActivity.class);
                feedbackList.putExtra("module",module);
                startActivity(feedbackList);
            }
        });
        ll_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chartAct= new Intent(getActivity(),ChartActivity.class);
                chartAct.putExtra("module",module);
                startActivity(chartAct);
            }
        });
        /////spiner////
        state= model.getMyModules().toArray(new String[model.getMyModules().size()]);


        spinner = (Spinner)view.findViewById(R.id.spinner_module);
        //spinner.setPopupBackgroundResource(R.drawable.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("menu"+state[position]);
                module=state[position];
                SharedPreferences.Editor editor=sharedpreferences.edit();
                editor.putString("mymodule",module);
                editor.commit();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



       spineradapter = new ArrayAdapter<String>(getActivity(),  android.R.layout.simple_spinner_item, state);

        spineradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spineradapter);



        update_login();
        return view;
    }

    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    public void update_login() {
        id.setText(sharedpreferences.getString("id", "User"));
        Name.setText(sharedpreferences.getString("fullName", " "));

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onTeacherFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println(model.getMyModules().size());
        state= model.getMyModules().toArray(new String[model.getMyModules().size()]);
        spineradapter=null;
        spineradapter = new ArrayAdapter<String>(getActivity(),  android.R.layout.simple_spinner_item, state);
        spineradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spineradapter);
        if(!sharedpreferences.getString("mymodule","").isEmpty()){
            //private method of your class
            spinner.setSelection(getIndex(spinner, sharedpreferences.getString("mymodule","")));
            module=sharedpreferences.getString("mymodule","");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTeacherFragmentInteractionListener) activity;
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
    public interface OnTeacherFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onTeacherFragmentInteraction(Uri uri);
    }


}
