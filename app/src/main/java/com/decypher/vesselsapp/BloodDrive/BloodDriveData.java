package com.decypher.vesselsapp.BloodDrive;

/**
 * Created by trebd on 10/21/2017.
 */

public class BloodDriveData {
    public String drive_id, address, date,  description, name, photo, status, time_end, time_start, contact, association, cor_photo;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime_end() {
        return time_end;
    }

    public String getDrive_id() {

        return drive_id;
    }

    public void setDrive_id(String drive_id) {
        this.drive_id = drive_id;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;

    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public String getCor_photo() {
        return cor_photo;
    }

    public void setCor_photo(String cor_photo) {
        this.cor_photo = cor_photo;
    }

    public BloodDriveData(String cor_photo, String association, String contact, String drive_id, String address, String date, String description, String name, String photo, String status, String time_end, String time_start) {
        this.drive_id = drive_id;
        this.address = address;
        this.date = date;
        this.description = description;
        this.photo = photo;
        this.status = status;
        this.time_end = time_end;
        this.time_start = time_start;
        this.contact  = contact;
        this.association = association;
        this.cor_photo = cor_photo;

    }

    public BloodDriveData(){}


}
