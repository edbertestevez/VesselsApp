package com.decypher.vesselsapp.Others;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by trebd on 10/18/2017.
 */

public class GlobalFunctions {

    Context mContext;
    // constructor
    public GlobalFunctions(Context context){
        this.mContext = context;
    }
    //firebase
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    public DatabaseReference postReference, userReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";

    public FirebaseDatabase firebaseMain(){
        return mDatabase;
    }

    public DatabaseReference postReference(){
        return mDatabase.getReference(POST_REFERENCE);
    }

    public DatabaseReference getUserReference(){
        return mDatabase.getReference(USER_REFERENCE);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String stringMonth(int intMonth) {
        String month[] = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        String monthString = month[intMonth];
        return monthString;
    }
}
