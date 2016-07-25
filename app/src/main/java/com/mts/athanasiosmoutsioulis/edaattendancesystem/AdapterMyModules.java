package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by AthanasiosMoutsioulis on 19/07/16.
 */
public class AdapterMyModules extends RecyclerView.Adapter<AdapterMyModules.ViewHolder> {

    public static final String MyPREFERENCES = "MyPrefs" ;
    private AttendanceModel model = AttendanceModel.getOurInstance();


    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private Context context;

    private final static int TYPE_1 = 1;


    public AdapterMyModules(Context context) {
        super();
        this.context = context;
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



        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.Module);


        }

        public void setData(final String moduleItem, int position) {
            txtHeader.setText(moduleItem);

        }


    }



    // Create new views (invoked by the layout manager)
    @Override
    public AdapterMyModules.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v = null;
        // create a new view
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.module_item, parent, false);
        }
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String moduleItem = null;
        ArrayList<String> mymodules_array =new ArrayList<String>(model.getMyModules());
        moduleItem = mymodules_array.get(position).toString();
        holder.setData(moduleItem, position);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return model.getMyModules().size();
    }


}
