package com.decypher.vesselsapp.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.decypher.vesselsapp.Home.Users;
import com.decypher.vesselsapp.Messages.UserSelectedChatActivity;
import com.decypher.vesselsapp.MyPosts.ViewPost;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserProfileActivity extends AppCompatActivity {

    long allowedDate, dateNow;

    TextView txtName, txtProfile, txtEmail, txtContact, txtAddress, txtCity, txtBirthdate, txtGender, txtType, txtCount, txtPostCount;
    TextView txtDateDonated, txtStatus;
    ImageView imgEdit, imgUser;

    ProgressDialog progDialog;
    Button btnConfirm;

    Users userValue;
    String donor_id;
    String name;
    String donation_count;

    //PROGRESS
    ProgressBar progress;
    TextView txtProgress;

    TextView txtRecipient, txtShow;

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, donorReference, userReference;
    String POST_REFERENCE = "posts";
    String DONOR_REFERENCE = "posts_donors";
    String USER_REFERENCE = "users";

    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;
    GlobalFunctions globalFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        donorReference = mDatabase.getReference(DONOR_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);

        progress = findViewById(R.id.progress);
        txtProgress = findViewById(R.id.txtProgress);
        txtProgress.setText("Loading profile. . .");

        progDialog = new ProgressDialog(UserProfileActivity.this);
        progDialog.setMessage("Loading profile. .");
        progDialog.show();

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        donor_id = extras.getString("DONOR_ID");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Donor's Profile");

        txtPostCount = (TextView) findViewById(R.id.txtPostCount);
        txtName = (TextView) findViewById(R.id.txtAssociation);
        txtEmail = (TextView) findViewById(R.id.txtName);
        txtContact = (TextView) findViewById(R.id.txtContactOrg);
        txtAddress = (TextView) findViewById(R.id.etAddress);
        txtBirthdate = (TextView) findViewById(R.id.txtBirthdate);
        txtGender = (TextView) findViewById(R.id.txtGender);
        txtType = (TextView) findViewById(R.id.txtType);
        txtCount = (TextView) findViewById(R.id.txtCount);
        txtDateDonated = (TextView) findViewById(R.id.txtDateDonated);
        txtStatus = (TextView) findViewById(R.id.txtStatus);

        txtRecipient = (TextView) findViewById(R.id.txtRecipient);
        txtShow = (TextView) findViewById(R.id.txtShow);

        txtRecipient.setVisibility(View.INVISIBLE);
        txtShow.setVisibility(View.INVISIBLE);

        imgUser = (ImageView) findViewById(R.id.imgUser);

        btnConfirm = (Button) findViewById(R.id.btnConfirm);



        btnConfirm.setText("Message Blood Center to contact this donor");
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                final View mView = getLayoutInflater().inflate(R.layout.dialog_message_chat, null);
                //variables
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                Button btnContinue = mView.findViewById(R.id.btnProceed);
                btnContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserProfileActivity.this, UserSelectedChatActivity.class);
                        intent.putExtra("RECEIVER", " ID("+donor_id+")");
                        startActivity(intent);
                    }
                });
            }
        });


        loadProfile();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadProfile() {
        userReference.child(donor_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SimpleDateFormat simpleDate =  new SimpleDateFormat("MMMM d, y");
                    SimpleDateFormat checkDate =  new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    Calendar now = Calendar.getInstance();
                    userValue = dataSnapshot.getValue(Users.class);

                    String user_photo = userValue.getUser_photo();
                    String email = "nfpbloodcenter@gmail.com (Blood Bank)";
                    String gender = userValue.getGender();
                    String birthdate = userValue.getBirthdate();
                    String address = userValue.getAddress();
                    String contact = userValue.getContact();
                    name = "Anonymous";
                    String city = userValue.getCity();
                    String bloodtype = userValue.getBloodtype();
                    donation_count = userValue.getDonation_count();
                    String last_donated = userValue.getLast_donated();

                    Picasso.with(getApplicationContext()).load(R.drawable.default_user).into(imgUser);
                    txtEmail.setText(email);
                    txtGender.setText(gender);
                    txtBirthdate.setText(simpleDate.format(new Date(birthdate)));
                    txtAddress.setText(address);
                    txtContact.setText("(034)441-6313 (Blood Bank)");
                    txtName.setText(name);
                    txtType.setText(bloodtype);
                    txtCount.setText(donation_count);
                    if(!last_donated.equals("")) {
                        txtDateDonated.setText(simpleDate.format(new Date(last_donated)));
                        String checkDateString = checkDate.format(new Date(last_donated));
                        try {
                            c.setTime(checkDate.parse(checkDateString));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        c.add(Calendar.MONTH, 3);
                        allowedDate = c.getTimeInMillis();
                        dateNow = now.getTimeInMillis();

                        if(allowedDate<=dateNow){
                            txtStatus.setText("Available for Donation");
                        }else{
                            int days = (int)(((allowedDate-dateNow)/(1000*60*60*24)));
                            txtStatus.setText("Not Available ( "+String.valueOf(days)+" days remaining )");
                        }
                        //compare dates
                    }else{
                        txtDateDonated.setText("No donation made");
                        txtStatus.setText("Available for Donation");
                    }

                    progress.setVisibility(View.GONE);
                    txtProgress.setVisibility(View.GONE);

                    displayPostCount();
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayPostCount() {

        postReference.child(donor_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // -3 kay may names
                if (dataSnapshot.getChildrenCount() == 0) {
                    txtPostCount.setText("0");
                } else {
                    txtPostCount.setText(String.valueOf(dataSnapshot.getChildrenCount() - 3));
                }
                progDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}