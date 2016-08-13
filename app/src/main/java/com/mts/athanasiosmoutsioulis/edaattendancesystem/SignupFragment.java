package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SignupFragment extends Fragment implements AttendanceModel.OnSignUpUpdateListener, AttendanceModel.OnCourseListUpdateListener{
    EditText id,pass,confpass,fullName,email;
    AutoCompleteTextView course;
    String role;
    AttendanceModel model = AttendanceModel.getOurInstance();

    ArrayAdapter<String> adapter;

    private OnFragmentInteractionListener mListener;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_signup, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button submit = (Button)view.findViewById(R.id.btn_submit);
        id = (EditText)view.findViewById(R.id.edt_signupId);
        email=(EditText)view.findViewById(R.id.edt_user_email);
        fullName = (EditText)view.findViewById(R.id.edt_FullName);
        pass = (EditText)view.findViewById(R.id.edt_signupPass);
        confpass = (EditText)view.findViewById(R.id.edt_signupConfPass);
        course = (AutoCompleteTextView)view.findViewById(R.id.autoCompleteTextView);
        RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.radioGroup);
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        if (checkedRadioButtonId == -1) {
            // No item selected
        }
        else{
            if (checkedRadioButtonId == R.id.rb_student) {
                role="Student";
            }else if (checkedRadioButtonId == R.id.rb_teacher){
                role="Teacher";
            }
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_student) {
                    role="Student";
                    course.setVisibility(View.VISIBLE);

                }else if (checkedId == R.id.rb_teacher){
                    role="Teacher";
                    course.setVisibility(View.GONE);
                }
            }
        });
        model.setSignupUpdateListener(this);
        model.setCourseListUpdateListener(this);
        model.updateCourseList();


        if (submit!= null){
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (id.getText().toString().isEmpty()){
                        Toast.makeText(getActivity(),"You have to fill the id",Toast.LENGTH_SHORT).show();

                    }else if(pass.getText().toString().isEmpty()){
                        Toast.makeText(getActivity(),"You have to fill the password",Toast.LENGTH_SHORT).show();

                    }else if(!confpass.getText().toString().equals(pass.getText().toString())){
                        Toast.makeText(getActivity(),"The confirm password doesn't match",Toast.LENGTH_SHORT).show();

                    } else if(fullName.getText().toString().isEmpty()){
                        Toast.makeText(getActivity(),"You have to fill your Name",Toast.LENGTH_SHORT).show();

                    } else if(email.getText().toString().isEmpty()){
                        Toast.makeText(getActivity(),"You have to fill your email",Toast.LENGTH_SHORT).show();

                    }else{
                        if (role.equals("Student")){
                            if(course.getText().toString().isEmpty()){
                                Toast.makeText(getActivity(),"You have to fill your course",Toast.LENGTH_SHORT).show();
                            }else{
                                Log.i("Signup", "Registering Student");
                                String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id="+id.getText().toString()+"&role="+role+"&pass="+pass.getText().toString()+"&course="+course.getText().toString().replaceAll(" ", "_")+"&FBname=&fullName="+fullName.getText().toString().replaceAll(" ", "_")+"&email="+email.getText().toString();
                                Log.i("Signup", uri);
                                model.signup(uri);
                            }
                        }else{
                            Log.i("Signup", "Registering Teacher");
                            String uri = "http://greek-tour-guides.eu/ioannina/dissertation/insert_user.php?id="+id.getText().toString()+"&role="+role+"&pass="+pass.getText().toString()+"&course=&FBname=&fullName="+fullName.getText().toString().replaceAll(" ","_")+"&email="+email.getText().toString();
                            model.signup(uri);
                        }


                    }


                }
            });

        }

        return view;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onSignUpUpdateListener(boolean signed) {
        if (signed==true){
            Log.i("Signup", "success");
            Toast.makeText(getActivity(), "The registration was completed", Toast.LENGTH_SHORT).show();
            //super.onBackPressed();
            ((AppCompatActivity) getActivity()).onBackPressed();
        }else{
            Toast.makeText(getActivity(),"The registration doesn't completed",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void oncourseListUpdateListener(ArrayList courses) {
        //retrieve the array with the courses thar are registed in the database
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,courses);
        course.setAdapter(adapter);
    }

}
