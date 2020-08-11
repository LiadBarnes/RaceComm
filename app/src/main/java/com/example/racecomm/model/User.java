package com.example.racecomm.model;

public class User {

    private String birthdate;
    private String country;
    private String fullname;
    private String gender;
    private String profileimage;
    private String status;
    private String username;

    public User() {
    }

    public User(String birthdate, String country, String fullname, String gender, String profileimage, String status, String username) {
        this.birthdate = birthdate;
        this.country = country;
        this.fullname = fullname;
        this.gender = gender;
        this.profileimage = profileimage;
        this.status = status;
        this.username = username;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
