package com.mts.athanasiosmoutsioulis.edaattendancesystem;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.estimote.sdk.SystemRequirementsChecker;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceSheetFragment extends Fragment implements AttendanceModel.OnGetTeacherSingleAttendance {

    AttendanceModel model=AttendanceModel.getOurInstance();
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton fab,fab1;
    private View cover_view;
    File myFile;
    private Animation fab_open,fab_close;
    private Boolean isFabOpen = false;
    ProgressDialog progress;
    String moduleID,SDate,EDate;
    public AttendanceSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_attendance_sheet, container, false);
        model.setGetTeacherSingleAttendanceListener(this);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        cover_view=(View)view.findViewById(R.id.cover_view);
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //  .setAction("Action", null).show();
                if (isFabOpen == false)
                    animateFAB();
                else {
                    try {
                        create_pdf();
                        open_pdf();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
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
                try {
                    create_pdf();
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_SUBJECT,"Attendances for "+moduleID+"_"+SDate+" - "+EDate);

                    Uri uri = Uri.fromFile(myFile);
                    email.putExtra(Intent.EXTRA_STREAM, uri);
                    email.setType("message/rfc822");
                    startActivity(email);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

            }
        });

        return view;
    }

    private void open_pdf() {
        new AlertDialog.Builder(getActivity())
                                .setTitle("Open PDF File")
                                .setMessage("Do you want to open the PDF file?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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
//
    }

    public void updateStudentAttendances(String moduleId,String startDate,String endDate){
        this.moduleID=moduleId;
        this.SDate=startDate;
        this.EDate=endDate;
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
            fab.setImageResource(R.drawable.pdf_icon);
            fab1.startAnimation(fab_open);

            fab1.setClickable(true);
            isFabOpen = true;


        }
    }

    public void create_pdf() throws FileNotFoundException,DocumentException {



        File pdfFolder = new File(Environment.getExternalStorageDirectory(), "EDA");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.i("PDF", "Pdf Directory created");
        }
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        System.out.println(pdfFolder.getAbsolutePath());
        String filename="attendances"+this.moduleID+"_"+this.SDate;
                ;
        myFile = new File(pdfFolder.getAbsolutePath(), filename.replace('/','_')+ ".pdf");
        OutputStream output = new FileOutputStream(myFile);
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, output);
        document.open();
//            for(String tmp:myList){
//                document.add(new Paragraph(tmp));
//            }
        //create a paragraph
        Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
        Paragraph paragraph = new Paragraph("Attendaces for the "+moduleID);
        SimpleDateFormat format = new SimpleDateFormat("dd'/'MM'/'yyyy'T'HH':'mm");
        Date tmpt_start=null;
        Date tmpt_end=null;
        try {
            tmpt_start = format.parse(SDate);
            tmpt_end = format.parse(EDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final String intMonth = (String) android.text.format.DateFormat.format("MM", tmpt_start); //06
        final String year = (String) android.text.format.DateFormat.format("yyyy", tmpt_start); //2013
        final String day = (String) android.text.format.DateFormat.format("dd", tmpt_start); //20
        String Lecture_date=day + "/" + intMonth + "/" + year;
        Paragraph paragraph2 = new Paragraph("Date: "+Lecture_date);
        //start time
        Calendar c1 = Calendar.getInstance();
        c1.setTime(tmpt_start);
        final String start_hour= Integer.toString(c1.get(Calendar.HOUR_OF_DAY));
        // String start_hour = (String) android.text.format.DateFormat.format("HH", Lectureitems.getStart());
        final String start_minutes = (String) android.text.format.DateFormat.format("mm", tmpt_start);
        //end time
        Calendar c2 = Calendar.getInstance();
        c2.setTime(tmpt_end);
        final String end_hour= Integer.toString(c2.get(Calendar.HOUR_OF_DAY));
        //String end_hour = (String) android.text.format.DateFormat.format("HH", Lectureitems.getEnd());
        final String end_minutes = (String) android.text.format.DateFormat.format("mm", tmpt_end);
        String Lecture_time=start_hour + ":" + start_minutes + " - " + end_hour + ":" + end_minutes;
        Paragraph paragraph3 = new Paragraph("Time: "+Lecture_time);



        //specify column widths
        float[] columnWidths = {1.5f, 2f, 2f,2f};
        //create PDF table with the given widths
        PdfPTable table = new PdfPTable(columnWidths);
        // set table width a percentage of the page width
        table.setWidthPercentage(90f);
        //insert an empty row


        //insert column headings
        insertCell(table, "A/A", Element.ALIGN_CENTER, 1, bf12);
        insertCell(table, "Student ID", Element.ALIGN_LEFT, 1, bf12);
        insertCell(table, "Student Name", Element.ALIGN_LEFT, 1, bf12);
        insertCell(table, "Valid", Element.ALIGN_LEFT, 1, bf12);

        table.setHeaderRows(1);

        //just some random data to fill
        int counter=1;
        for (Attendance tmp:model.getStudents_Attendance_list()){

            insertCell(table, Integer.toString(counter), Element.ALIGN_CENTER, 1, bf12);
            insertCell(table, tmp.getStudentId(), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, tmp.getFullName(), Element.ALIGN_LEFT, 1, bf12);
            insertCell(table, tmp.getValid(), Element.ALIGN_LEFT, 1, bf12);
            counter++;

        }

        document.add(paragraph);
        document.add(paragraph2);
        document.add(paragraph3);
        document.add( Chunk.NEWLINE );
        // add the paragraph to the document
        document.add(table);
        document.close();

    }

    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font){

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if(text.trim().equalsIgnoreCase("")){
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);


    }
}
