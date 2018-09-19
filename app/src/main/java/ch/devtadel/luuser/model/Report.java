package ch.devtadel.luuser.model;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Report {
    private List<SchoolClass> classes;
    private String name;

    public String getName() {
        return name;
    }

    public int getCheckCount(){
        int returnInt = 0;
        for(SchoolClass sClass : classes){
            returnInt += sClass.getChecks().size();
        }
        return returnInt;
    }

    public List<Check> getAllChecks(){
        List<Check> allChecks = new ArrayList<>();
        for(SchoolClass sClass : classes){
            allChecks.addAll(sClass.getChecks());
        }
        return allChecks;
    }

    @SuppressLint("SimpleDateFormat")
    public List<String> getDates(){
        List<String> allDates = new ArrayList<>();
        for(SchoolClass sClass : classes){
            for(Check check: sClass.getChecks()){
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                String date = sdf.format(check.getDate());
                if(!allDates.contains(date)){
                    allDates.add(date);
                }
            }
        }
        return allDates;
    }

    public int getBiggestCheckCount(){
        int returnInt = 0;
        for(SchoolClass sClass : classes){
           if(sClass.getChecks().size() > returnInt){
                returnInt = sClass.getChecks().size();
           }
        }
        return returnInt;
    }

    public Report(String name){
        classes = new ArrayList<>();
        this.name = name;
    }

    public void addClass(SchoolClass c){
        classes.add(c);
    }

    public int getClassCount(){
        return classes.size();
    }

    public List<SchoolClass> getClasses() {
        return classes;
    }
}
