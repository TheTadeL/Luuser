package ch.devtadel.luuser.model;

public class SchoolClass {
    private String name;
    private int cntStudents;
    private int cntLouse;
    private int cntChecks;

    public SchoolClass(String name){
        this.name = name;
        this.cntStudents = 0;
        this.cntChecks = 0;
        this.cntLouse = 0;
    }

    public SchoolClass(String name, int anzSchueler, int anzKontrollen, int anzLause){
        this.name = name;
        this.cntStudents = anzSchueler;
        this.cntChecks = anzKontrollen;
        this.cntLouse = anzLause;
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
    public void setCntStudents(int cntStudents) {
        this.cntStudents = cntStudents;
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
