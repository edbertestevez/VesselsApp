package com.decypher.vesselsapp.Home;

/**
 * Created by trebd on 10/20/2017.
 */

public class PosterData {
    public String user_id;
    public String user_name;
    public String contact;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String user_photo;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public PosterData(String user_id, String user_name, String user_photo, String contact) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_photo = user_photo;
        this.contact = contact;
    }

    public PosterData(){}
}
