package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by AthanasiosMoutsioulis on 19/07/16.
 */
public class AdapterAvailableModules extends RecyclerView.Adapter<AdapterAvailableModules.ViewHolder> {
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    Set<String> myModules;

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private Context context;

    private boolean welcome;
    private final static int TYPE_1 = 1;

    private AttendanceModel model = AttendanceModel.getOurInstance();


    public AdapterAvailableModules(Context context,boolean welcome) {
        super();
        this.context = context;
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        myModules=model.getMyModules();
        this.welcome=welcome;
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
        public TextView txtHeader;
        CheckBox check;



        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.Module);
            check= (CheckBox)v.findViewById(R.id.ch_module);


        }

        public void setData(final String moduleItem, int position) {
            txtHeader.setText(moduleItem);
            if(myModules.contains(moduleItem)){
                check.setChecked(true);
                check.setText("Added");
            }else{
                check.setText("Add Module");
            }
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        Log.i("check", "is checked");
                        check.setText("Added");
                        myModules.add(moduleItem);

                    }else{
                        Log.i("check", "is not checked");
                        check.setText("Add Module");
                        myModules.remove(moduleItem);


                    }
                }
            });
        }


    }



    // Create new views (invoked by the layout manager)
    @Override
    public AdapterAvailableModules.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = null;
        // create a new view
        if (viewType == 1) {
            if(welcome)
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.avalaible_module_item_teacher, parent, false);
                else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_module_item, parent, false);
        }
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String moduleItem = null;
        moduleItem = model.getModules_list().get(position);
        holder.setData(moduleItem, position);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return model.getModules_list().size();
    }


}
