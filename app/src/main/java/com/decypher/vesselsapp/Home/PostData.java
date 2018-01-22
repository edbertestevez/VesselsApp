package com.decypher.vesselsapp.Home;

/**
 * Created by trebd on 10/17/2017.
 */

public class PostData {
    public String bloodtype;
    public String city;
    public String description;
    public String location;
    public String photo;
    public String receiver;
    public String status;
    public String title;
    public String user_name;
    public String user_id;
    public String user_photo;
    public String bags;
    public String date;
    public String post_id;
    public String date_needed;
    public String contact;
    public String response_count;

    public String getBags() {
        return bags;
    }

    public void setBags(String bags) {
        this.bags = bags;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBloodtype() {
        return bloodtype;
    }

    public String getCity() {
        return city;
    }

    public void setBloodtype(String bloodtype) {
        this.bloodtype = bloodtype;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getPhoto() {
        return photo;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getDate_needed() {
        return date_needed;
    }

    public void setDate_needed(String date_needed) {
        this.date_needed = date_needed;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getResponse_count() {
        return response_count;
    }

    public void setResponse_count(String response_count) {
        this.response_count = response_count;
    }

    public PostData(String contact, String response_count, String post_id, String date_needed, String date, String bloodtype, String city, String description, String location, String photo, String receiver, String status, String title, String user_name, String user_id, String user_photo, String bags) {
        this.bloodtype = bloodtype;
        this.city = city;
        this.description = description;
        this.location = location;
        this.photo = photo;
        this.receiver = receiver;
        this.status = status;
        this.title = title;
        this.post_id = post_id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_photo = user_photo;
        this.date = date;
        this.bags = bags;
        this.date_needed = date_needed;
        this.contact = contact;
        this.response_count = response_count;

    }

    public PostData(){}
}
