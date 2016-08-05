package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.AttributedCharacterIterator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnLoginListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment implements AttendanceModel.OnSignInUpdateListener{
    EditText id,pass;
    AttendanceModel model = AttendanceModel.getOurInstance();
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    String role;

    private OnLoginListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_login, container, false);

        model.setSigninUpdateListener(this);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // views
        Button login = (Button)view.findViewById(R.id.btn_login);
        Button signup = (Button)view.findViewById(R.id.btn_signup);
        id = (EditText)view.findViewById(R.id.edt_id);
        pass = (EditText)view.findViewById(R.id.edt_pass);
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
                }else if (checkedId == R.id.rb_teacher){
                    role="Teacher";
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "You have to fill the id", Toast.LENGTH_SHORT).show();

                }else if(pass.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(),"You have to fill the password",Toast.LENGTH_SHORT).show();

                }else{
                    Log.i("Signup", "ready");
                    String uri = "http://greek-tour-guides.eu/ioannina/dissertation/check_user.php?id="+id.getText().toString()+"&pass="+pass.getText().toString()+"&role="+role;
                    model.signin(uri);

                }
            }
        });


        //views actions
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent signup = new Intent(getApplicationContext(), SignUp.class);
               // startActivity(signup);
                Fragment fragment = new SignupFragment();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations( R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out);
                fragmentTransaction.replace(R.id.login_fragment, fragment);
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit();

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLoginListener) activity;
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
    public void onSignInUpdateListener(boolean login, String name) {
        if (login==true){
            Log.i("Signin", "success");
            SharedPreferences.Editor editor = sharedpreferences.edit();
            //put the user info to the shared preferences
            editor.putString("id", id.getText().toString());
            editor.putString("pass", pass.getText().toString());
            editor.putBoolean("logged", true);
            editor.putString("role",role);
            editor.putString("FName",name);
            editor.commit();
            Toast.makeText(getActivity(),"The Login was successful",Toast.LENGTH_SHORT).show();
            if (mListener!=null){
                mListener.onLoginClicked();
            }
//            Intent returnIntent = new Intent();
//            returnIntent.putExtra("result", true);
            //setResult(Activity.RESULT_OK, returnIntent);
            //finish();

        }else{
            Toast.makeText(getActivity(),"The login wasn't successful",Toast.LENGTH_SHORT).show();
//            Intent returnIntent = new Intent();
//            returnIntent.putExtra("result", true);
//            setResult(Activity.RESULT_CANCELED, returnIntent);
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
    public interface OnLoginListener {
        // TODO: Update argument type and name
        public void onLoginClicked();
    }

}
