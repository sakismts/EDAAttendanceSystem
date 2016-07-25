package com.mts.athanasiosmoutsioulis.edaattendancesystem;

/**
 * Created by AthanasiosMoutsioulis on 25/07/16.
 */
public class Attendance {
    public Attendance(String studentId, String valid, String anonymous, String feedback) {
        StudentId = studentId;
        this.valid = valid;
        this.anonymous = anonymous;
        this.feedback = feedback;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    String StudentId;

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    String valid;
    String anonymous;
    String feedback;
}
