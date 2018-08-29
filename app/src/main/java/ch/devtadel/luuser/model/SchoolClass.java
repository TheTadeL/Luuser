package ch.devtadel.luuser.model;

import java.util.ArrayList;
import java.util.List;

public class SchoolClass {
    private String name;
    private int cntStudents;
    private int cntLouse;
    private int cntChecks;

    public List<Check> getChecks() {
        return checks;
    }

    private List<Check> checks;

    public SchoolClass(String name){
        this.name = name;
        this.cntStudents = 0;
        this.cntChecks = 0;
        this.cntLouse = 0;

        checks = new ArrayList<>();
    }

    public SchoolClass(String name, int anzSchueler, int anzKontrollen, int anzLause){
        this.name = name;
        this.cntStudents = anzSchueler;
        this.cntChecks = anzKontrollen;
        this.cntLouse = anzLause;

        checks = new ArrayList<>();
    }

    public void addCheck(Check c){
        checks.add(c);
    }

    //GETTER / SETTER
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getCntStudents() {
        return cntStudents;
    }
    public int getCntLouse() {
        return cntLouse;
    }
    public int getCntChecks() {
        return cntChecks;
    }
}
