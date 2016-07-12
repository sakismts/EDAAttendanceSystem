package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AthanasiosMoutsioulis on 04/06/16.
 */
public class LecturesDBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME ="lecturesList";
    public static final String COLUMN_ID ="_id";
    public static final String COLUMN_TITLE ="title";
    public static final String COLUMN_MODULE ="module";
    public static final String COLUMN_TYPE ="type";
    public static final String COLUMN_LOCATION ="location";
    public static final String COLUMN_START ="start";
    public static final String COLUMN_END ="end";
    public static final String COLUMN_DESCRIPTION ="description";
    public static final String COLUMN_ATTENDANCE ="attendance";
    public static final String COLUMN_FEEDBACK ="feedback";



    private static  final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 1;

    private  static  final  String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " VARCHAR(80) NOT NULL, "
            + COLUMN_MODULE + " VARCHAR(80) NOT NULL, "
            + COLUMN_TYPE + " VARCHAR(80) NOT NULL, "
            + COLUMN_LOCATION + " VARCHAR(200) NOT NULL, "
            + COLUMN_START+ " VARCHAR(80) NOT NULL, "
            + COLUMN_END+ " VARCHAR(80) NOT NULL, "
            + COLUMN_DESCRIPTION+ " VARCHAR(255) NOT NULL, "
            +COLUMN_ATTENDANCE + " VARCHAR(10) NOT NULL, "
            +COLUMN_FEEDBACK + " VARCHAR(10) NULL );";


    public LecturesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

    }
}
