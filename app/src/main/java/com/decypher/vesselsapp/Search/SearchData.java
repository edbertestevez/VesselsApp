package com.decypher.vesselsapp.Search;

/**
 * Created by trebd on 10/19/2017.
 */

public class SearchData {
    public String user_id, user_photo,  name, city, bloodtype, donation_count, contact, post_id, gender;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBloodtype() {
        return bloodtype;
    }

    public void setBloodtype(String bloodtype) {
        this.bloodtype = bloodtype;
    }

    public String getDonation_count() {
        return donation_count;
    }

    public void setDonation_count(String donation_count) {
        this.donation_count = donation_count;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public SearchData(String user_id, String user_photo, String name, String city, String bloodtype, String donation_count, String contact, String post_id, String gender) {
        this.user_id = user_id;
        this.name = name;
        this.city = city;
        this.bloodtype = bloodtype;
        this.donation_count = donation_count;
        this.user_photo = user_photo;
        this.contact = contact;
        this.post_id = post_id;
        this.gender = gender;
    }

    public SearchData(){}

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}
