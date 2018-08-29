package ch.devtadel.luuser.model;

import java.util.ArrayList;
import java.util.List;

public class School {
    private String name;
    private String place;
    private int cntLouse;
    private int cntChecks;
    private List<SchoolClass> classes = new ArrayList<>();

    public School(String name, String place){
        this.name = name;
        this.place = place;
    }

    @Override
    public String toString() {
        return name;
    }

    // GETTER / SETTER
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public List<SchoolClass> getClasses() {
        return classes;
    }
    public void setClasses(List<SchoolClass> classes) {
        this.classes = classes;
    }
    public int getCntLouse() {
        return cntLouse;
    }
    public void setCntLouse(int cntLouse) {
        this.cntLouse = cntLouse;
    }
    public int getCntChecks() {
        return cntChecks;
    }
    public void setCntChecks(int cntChecks) {
        this.cntChecks = cntChecks;
    }
}
