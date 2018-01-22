package com.decypher.vesselsapp.Home;

/**
 * Created by trebd on 10/27/2017.
 */

public class HomeData {
    public String user_id;
    public int date;
    public String status;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HomeData(String user_id, int date, String status) {
        this.user_id = user_id;
        this.date = date;
        this.status = status;


    }

    public HomeData(){}
}

