package ch.devtadel.luuser.model;

public class Klasse {
    private String name;
    private int anzSchueler;
    private int anzLause;
    private int anzKontrollen;

    public Klasse(String name, int anzSchueler, int anzKontrollen, int anzLause){
        this.name = name;
        this.anzSchueler = anzSchueler;
        this.anzKontrollen = anzKontrollen;
        this.anzLause = anzLause;
    }

    //GETTER / SETTER
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAnzSchueler() {
        return anzSchueler;
    }
    public void setAnzSchueler(int anzSchueler) {
        this.anzSchueler = anzSchueler;
    }
    public int getAnzLause() {
        return anzLause;
    }
    public void setAnzLause(int anzLause) {
        this.anzLause = anzLause;
    }
    public int getAnzKontrollen() {
        return anzKontrollen;
    }
    public void setAnzKontrollen(int anzKontrollen) {
        this.anzKontrollen = anzKontrollen;
    }
}
