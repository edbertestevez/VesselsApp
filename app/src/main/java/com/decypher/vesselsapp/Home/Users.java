package com.decypher.vesselsapp.Home;

/**
 * Created by trebd on 10/17/2017.
 */

public class Users {

    public String name;
    public String email;
    public String password;
    public String gender;
    public String bloodtype;
    public String birthdate;
    public String city;
    public String address;
    public String contact;
    public String last_donated;
    public String donation_count;
    public String user_photo;
    public String user_id;

    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(Users.class)
    }

    public Users(String user_photo, String user_id, String name, String email, String password, String gender, String bloodtype, String birthdate, String city, String address, String contact, String last_donated, String donation_count) {
        this.user_photo = user_photo;
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.bloodtype = bloodtype;
        this.birthdate = birthdate;
        this.city = city;
        this.address = address;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodtype() {
        return bloodtype;
    }

    public void setBloodtype(String bloodtype) {
        this.bloodtype = bloodtype;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLast_donated() {
        return last_donated;
    }

    public void setLast_donated(String last_donated) {
        this.last_donated = last_donated;
    }

    public String getDonation_count() {
        return donation_count;
    }

    public void setDonation_count(String donation_count) {
        this.donation_count = donation_count;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
