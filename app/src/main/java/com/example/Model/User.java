package com.example.Model;

import java.sql.Date;

public class User {
    private String phoneNumber;
    private int point;
    private String dateCreate;
    private String dateUpdate;

    public User(String phoneNumber, int point, String dateCreate, String dateUpdate) {
        this.dateCreate = dateCreate;
        this.dateUpdate = dateUpdate;
        this.phoneNumber = phoneNumber;
        this.point = point;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(String dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
