package ch.devtadel.luuser.model;

public class User {
    private String surname;
    private String prename;
    private String place;
    private String email;
    private String password;

    @Override
    public String toString() {
        return "=== " + surname + " " + prename + " ===" +
                "\nOrt: " + place +
                "\nEmail: " + email +
                "\nPasswort " + password;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
