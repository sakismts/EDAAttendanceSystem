package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by philipp on 02/06/16.
 */
public class DayAxisValueFormatter implements AxisValueFormatter {
    AttendanceModel model=AttendanceModel.getOurInstance();

    protected String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    private BarLineChartBase<?> chart;

    public DayAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        System.out.println("the value is :"+value);

        List<String > dates= Arrays.asList("01/1/16","02/1/16","03/1/16","04/1/16","05/1/16","06/1/16","07/1/16","08/1/16","09/1/16","10/1/16","11/1/16","12/1/16","13/1/16","14/1/16");
       /* dates.add(0,"test");
        dates.add(" ");
        System.out.println(dates);

        String str_return=dates.get((int)value).toString();*/
        String str_return=" ";
       if(value==0){
            str_return=" ";
            System.out.println("the value is:" +value);
        }
        else if((int)value==model.getTeacherAttendances().size()+1){
            str_return=" ";
           System.out.println("the value is:" +value);
        }
        else {
            str_return="sakis";
           System.out.println("the value is:" +value);
//
           final String intMonth = (String) android.text.format.DateFormat.format("MM", model.getTeacherAttendances().get((int)value-1).getStart()); //06
           final String year = (String) android.text.format.DateFormat.format("yyyy",model.getTeacherAttendances().get((int)value-1).getStart()); //2013
           final String day = (String) android.text.format.DateFormat.format("dd", model.getTeacherAttendances().get((int)value-1).getStart()); //20
           System.out.println(day + "/" + intMonth + "/" + year);
           str_return=day + "/" + intMonth + "/" + year;//dates.get((int)value-1);
        }

    return str_return;
      //  return dates[index];
      // return "salos";
        /* int days = (int) value;

        int year = determineYear(days);

        int month = determineMonth(days);
        String monthName = mMonths[month % mMonths.length];
        String yearName = String.valueOf(year);

        if (chart.getVisibleXRange() > 30 * 6) {

            return monthName + " " + yearName;
        } else {

            int dayOfMonth = determineDayOfMonth(days, month + 12 * (year - 2016));

            String appendix = "th";

            switch (dayOfMonth) {
                case 1:
                    appendix = "st";
                    break;
                case 2:
                    appendix = "nd";
                    break;
                case 3:
                    appendix = "rd";
                    break;
                case 21:
                    appendix = "st";
                    break;
                case 22:
                    appendix = "nd";
                    break;
                case 23:
                    appendix = "rd";
                    break;
                case 31:
                    appendix = "st";
                    break;
            }

            return dayOfMonth == 0 ? "" : dayOfMonth + appendix + " " + monthName;
        }*/
    }

    private int getDaysForMonth(int month, int year) {

        if (month == 1) {

            if (year == 2016 || year == 2020)
                return 29;
            else
                return 28;
        }

        if (month == 3 || month == 5 || month == 8 || month == 10)
            return 30;
        else
            return 31;
    }

    private int determineMonth(int dayOfYear) {

        int month = -1;
        int days = 0;

        while (days < dayOfYear) {
            month = month + 1;

            if (month >= 12)
                month = 0;

            int year = determineYear(days);
            days += getDaysForMonth(month, year);
        }

        return Math.max(month, 0);
    }

    private int determineDayOfMonth(int dayOfYear, int month) {

        int count = 0;
        int days = 0;

        while (count < month) {

            int year = determineYear(days);
            days += getDaysForMonth(count % 12, year);
            count++;
        }

        return dayOfYear - days;
    }

    private int determineYear(int days) {

        if (days <= 366)
            return 2016;
        else if (days <= 730)
            return 2017;
        else if (days <= 1094)
            return 2018;
        else if (days <= 1458)
            return 2019;
        else
            return 2020;

    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
