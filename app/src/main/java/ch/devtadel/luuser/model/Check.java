package ch.devtadel.luuser.model;

import java.util.Date;

public class Check {
    private boolean noLouse;
    private int louseCount;
    private int studentCount;
    private Date date;
    private String className;
    private String schoolName;

    public boolean isNoLouse() {
        return noLouse;
    }

    public void setNoLouse(boolean noLouse) {
        this.noLouse = noLouse;
    }

    public int getLouseCount() {
        return louseCount;
    }

    public void setLouseCount(int louseCount) {
        this.louseCount = louseCount;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
