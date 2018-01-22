package com.decypher.vesselsapp.DonationProcess;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DonationStep2 extends AppCompatActivity {
    String post_photo, post_id, post_receiver, selected_id, response_ctr;
    Button btnNext, btnCancel;

    GlobalFunctions globalFunctions;
    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference, donorReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String DONOR_REFERENCE = "posts_donors";

    ProgressDialog progDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question2);


        progDialog = new ProgressDialog(DonationStep2.this);
        progDialog.setMessage("Saving your donation information. Please wait . . .");

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        donorReference = mDatabase.getReference(DONOR_REFERENCE);

        globalFunctions = new GlobalFunctions(getApplicationContext());
        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        post_photo = extras.getString("POST_PHOTO");
        post_id = extras.getString("POST_ID");
        post_receiver = extras.getString("POST_RECEIVER");
        selected_id = extras.getString("SELECTED_ID");
        response_ctr = extras.getString("RESPONSE_COUNT");

        btnNext = (Button) findViewById(R.id.btnNext);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SAVE AS DONORS ka post
                if(globalFunctions.isNetworkAvailable()){
                    //firebase
                    progDialog.show();
                    donorReference.child(post_id).child(sharedpref.getString("USERID","")).setValue(false, new DatabaseReference.CompletionListener(){

                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            postReference.child(selected_id).child(post_id).child("response_count").setValue(String.valueOf(Integer.parseInt(response_ctr) + 1), new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    progDialog.dismiss();
                                    Intent intent = new Intent(DonationStep2.this, DonationStep3.class);
                                    intent.putExtra("POST_ID",post_id);
                                    intent.putExtra("POST_RECEIVER",post_receiver);
                                    intent.putExtra("POST_PHOTO",post_photo);
                                    startActivity(intent);
                                }
                            });

                        }
                    });

                }else{
                    Toast.makeText(DonationStep2.this, "Internet Connection problem", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
