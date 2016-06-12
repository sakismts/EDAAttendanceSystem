package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by AthanasiosMoutsioulis on 04/06/16.
 */
public class Lecture implements Serializable, Comparable<Lecture>{
    public Lecture(String title, String module, String type, Date start, Date end, String location, String description) {
        this.title = title;
        this.module = module;
        this.type = type;
        this.start = start;
        this.end = end;
        this.location = location;
        this.description = description;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String title;
    private String module;
    private String type;
    private Date  start;
    private Date  end;
    private String location;
    private String description;


    @Override
    public int compareTo(Lecture another) {
        return getStart().compareTo(another.getStart());
    }


}
