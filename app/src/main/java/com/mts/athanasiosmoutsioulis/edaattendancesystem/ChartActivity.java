package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;
import com.mts.athanasiosmoutsioulis.edaattendancesystem.DayAxisValueFormatter;
import com.mts.athanasiosmoutsioulis.edaattendancesystem.MyAxisValueFormatter;
import com.mts.athanasiosmoutsioulis.edaattendancesystem.XYMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChartActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener, AttendanceModel.OnGetTeacherAttendances {
    protected BarChart mChart;
    private SeekBar mSeekBarX;
    private TextView tvX, tvY;
    protected Typeface mTfRegular;
    protected Typeface mTfLight;
    private Animation fab_open,fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1;
    private View cover_view;
    private AttendanceModel model=AttendanceModel.getOurInstance();
    String filePath;
    String module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);
        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        tvX = (TextView) findViewById(R.id.tvXMax);
        model.setGetTeacherAttendancesListener(this);
        model.getTeacherAttendances().clear();
        Intent intent = getIntent();
         module=intent.getStringExtra("module");
        String uri = "http://greek-tour-guides.eu/ioannina/dissertation/getTeacherAttendances.php?module_id="+ module;
        Log.i("URI", uri.toString());
        model.getTeacherAttendaces(uri);
        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        cover_view=(View)findViewById(R.id.cover_view);
        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //  .setAction("Action", null).show();
                if (isFabOpen == false)
                    animateFAB();
                else {

                        saveImage();
                        open_Image();

                }
            }
        });
        cover_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animateFAB();

            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    saveImage();
                final File myfile = new File(filePath);
                if (!myfile.exists()) {
                    Log.i("FILE", "Pdf Directory created");
                }else{ Log.i("FILE", "Pdf Directory exist");
                }
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_SUBJECT,"Chart for "+module);

                    Uri uri = Uri.fromFile(myfile);
                    email.putExtra(Intent.EXTRA_STREAM, uri);
                    email.setType("message/rfc822");
                    startActivity(email);


            }
        });




        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(2);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        AxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(4);
        // xAxis.setAvoidFirstLastClipping(true);
        xAxis.setValueFormatter(xAxisFormatter);

        AxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

        mChart.setMarkerView(new XYMarkerView(this, xAxisFormatter));

        //setData(1, 50);


        // setting data

        mSeekBarX.setProgress(1);
        tvX.setText("" + (mSeekBarX.getProgress()+1));

        mSeekBarX.setOnSeekBarChangeListener(this);


       // mChart.setDrawLegend(false);
    }

    private void open_Image() {

        final File myfile = new File(filePath);
        if (!myfile.exists()) {
            Log.i("FILE", "Pdf Directory created");
        }else{ Log.i("FILE", "Pdf Directory exist");
        }

        new AlertDialog.Builder(this)
                .setTitle("Open Image File")
                .setMessage("Do you want to open the Image file?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        System.out.println(filePath);
                        intent.setDataAndType(Uri.fromFile(myfile), "*/*");
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    private void saveImage() {
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        String filename="Chart_"+module+"_"+timeStamp;
        mChart.saveToPath(filename,"/Documents/EDA/");
        filePath=Environment.getExternalStorageDirectory().getPath()+ "/Documents/EDA/"+ filename
                + ".png";

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                for (IDataSet set : mChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (mChart.getData() != null) {
                    mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
                    mChart.invalidate();
                }
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
                mChart.notifyDataSetChanged();
                break;
            }
            case R.id.actionToggleBarBorders: {
                for (IBarDataSet set : mChart.getData().getDataSets())
                    ((BarDataSet) set).setBarBorderWidth(set.getBarBorderWidth() == 1.f ? 0.f : 1.f);

                mChart.invalidate();
                break;
            }
            case R.id.animateX: {
                mChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(3000);
                break;
            }
            case R.id.animateXY: {

                mChart.animateXY(3000, 3000);
                break;
            }
            case R.id.actionSave: {
                if (mChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();
                break;
            }
        }
        return true;
    }*/

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText("" + (mSeekBarX.getProgress()+1));


        setData(mSeekBarX.getProgress());
        mChart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    private void setData(int count) {

        int start = 0;
        //float[] myvalues={1,2,3,9};
        mChart.getXAxis().setAxisMinValue(start);
        mChart.getXAxis().setAxisMaxValue(start + count+2);

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = start; i < start + count + 1; i++) {
//            float mult = (range + 1);
//            float val = (float) (Math.random() * mult);

            yVals1.add(new BarEntry(i + 1f, model.getTeacherAttendances().get(i).getStudentsAttend()));
           // yVals1.add(new BarEntry(i + 1f,myvalues[i]));
                        //yVals1.add(new BarEntry(i + 1f, val));
        }

        System.out.println(yVals1);
        BarDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {

            set1 = new BarDataSet(yVals1, "The year 2016-2017");
            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);

            mChart.setData(data);
        }
    }



    protected RectF mOnValueSelectedRectF = new RectF();

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        mChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = mChart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + mChart.getLowestVisibleX() + ", high: "
                        + mChart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() { }

    @Override
    public void onGetTeacherAttendances(boolean signed) {
        //System.out.println("the number is:"+model.getTeacherAttendances().size()-1);
        mSeekBarX.setMax(model.getTeacherAttendances().size()-1);
        setData(1);
        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "EDA");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.i("PDF", "Pdf Directory created");
        }
//        Date date = new Date() ;
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
//
//        System.out.println("the folder path is:"+pdfFolder.getAbsolutePath());
//
//        File myFile = new File(pdfFolder.getAbsolutePath(),  "chart.png");

       // mChart.saveToGallery("chart.jpg", 85);

//        for (Lecture tmp:model.getTeacherAttendances()){
//            System.out.println("the number is:"+tmp.getStudentsAttend());
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int hasReadExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasReadExternalPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);
            return;
        }
        int hasWriteExternalPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    124);
            return;
        }
    }

    public void animateFAB(){

        if(isFabOpen){
            cover_view.setVisibility(View.GONE);
            fab.setImageResource(R.drawable.options_menu_float);
            fab1.startAnimation(fab_close);

            fab1.setClickable(false);
            isFabOpen = false;


        } else {

            //fab.startAnimation(rotate_forward);
            cover_view.setVisibility(View.VISIBLE);
            cover_view.bringToFront();
            fab.setImageResource(R.drawable.save_icon);
            fab1.startAnimation(fab_open);

            fab1.setClickable(true);
            isFabOpen = true;


        }
    }

}


