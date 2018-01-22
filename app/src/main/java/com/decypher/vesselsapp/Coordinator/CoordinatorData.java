package com.decypher.vesselsapp.Coordinator;

/**
 * Created by trebd on 10/21/2017.
 */

public class CoordinatorData {
    public String cor_id, name,  email, address, contact, association, photo;


    public CoordinatorData(String name, String email, String address, String contact, String association, String photo, String cor_id) {
        this.cor_id = cor_id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.contact = contact;
        this.association = association;
        this.photo = photo;

    }

    public String getCor_id() {
        return cor_id;
    }

    public void setCor_id(String cor_id) {
        this.cor_id = cor_id;
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

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public CoordinatorData(){}


}

