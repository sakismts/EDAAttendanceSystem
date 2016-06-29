package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AthanasiosMoutsioulis on 12/06/16.
 */
public class Beacon {
    public Beacon(String UUID, String major, String minor,String location) {
        this.UUID = UUID;
        this.major = major;
        this.minor = minor;
        this.location= location;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    private String UUID;
    private String major;
    private String minor;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;


}
