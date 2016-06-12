package com.mts.athanasiosmoutsioulis.edaattendancesystem;

/**
 * Created by AthanasiosMoutsioulis on 12/06/16.
 */
public class Beacon {
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

}
