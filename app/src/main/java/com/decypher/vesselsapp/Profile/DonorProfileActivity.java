package com.decypher.vesselsapp.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.decypher.vesselsapp.Home.Users;
import com.decypher.vesselsapp.Messages.SelectedChatActivity;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.Others.LoginActivity;
import com.decypher.vesselsapp.Others.SignupActivity1;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DonorProfileActivity extends AppCompatActivity {

    long allowedDate, dateNow;

    TextView txtRecipient, txtName, txtProfile, txtEmail, txtContact, txtAddress, txtCity, txtBirthdate,txtGender, txtType,txtCount,txtPostCount;
    ImageView imgEdit, imgUser;
    Button btnConfirm;
    TextView txtDateDonated, txtStatus;

    Button btnMessage;
    Users userValue;
    String donor_id, post_id;
    String name;
    String donation_count;

    String donor_name;
    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, donorReference, userReference, directoryReference, chatReference,donationReference;
    String POST_REFERENCE = "posts";
    String DIRECTORY_REFERENCE = "chat_directory";
    String DONOR_REFERENCE = "posts_donors";
    String USER_REFERENCE = "users";
    String CHAT_REFERENCE = "chats";
    String DONATION_REFERENCE = "users_donation";

    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;
    GlobalFunctions globalFunctions;

    int countChat;
    
    //PROGRESS
    ProgressBar progress;
    TextView txtProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        progress = findViewById(R.id.progress);
        txtProgress = findViewById(R.id.txtProgress);

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        donorReference = mDatabase.getReference(DONOR_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        directoryReference = mDatabase.getReference(DIRECTORY_REFERENCE);
        chatReference = mDatabase.getReference(CHAT_REFERENCE);
        donationReference = mDatabase.getReference(DONATION_REFERENCE);

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        donor_id = extras.getString("DONOR_ID");
        post_id = extras.getString("POST_ID");
        donor_name = extras.getString("DONOR_NAME");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Donor's Profile");

        btnMessage = (Button) findViewById(R.id.btnMessage);
        txtRecipient = (TextView) findViewById(R.id.txtRecipient);
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

        imgUser = (ImageView) findViewById(R.id.imgUser);

        btnConfirm = (Button) findViewById(R.id.btnConfirm);

        displayPostCount();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(DonorProfileActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.dialog_confirm_donate, null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                TextView txtDonorName = mView.findViewById(R.id.txtDonorName);
                Button btnProceed = mView.findViewById(R.id.btnProceed);
                txtDonorName.setText("Donor: "+name);

                btnProceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //update donation count ni toto
                        userReference.child(donor_id).child("donation_count").setValue(String.valueOf(Integer.parseInt(donation_count)+1));
                        donorReference.child(post_id).child(donor_id).setValue(true);
                        DateFormat dateFormat = new SimpleDateFormat(
                                "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                        try {
                            Calendar c = Calendar.getInstance();
                           Date newDate = dateFormat.parse(c.getTime().toString());
                            userReference.child(donor_id).child("last_donated").setValue(newDate.toString());

                            String key = donationReference.child(donor_id).push().getKey();
                            donationReference.child(donor_id).child(key).child("association").setValue("FPBEH7EyHrMkjoAoOFGxWCbSS1Y2");
                            donationReference.child(donor_id).child(key).child("date").setValue(newDate.toString(), new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    Intent intent = new Intent(DonorProfileActivity.this, DonorProfileActivity.class);
                                    intent.putExtra("DONOR_ID",donor_id);
                                    intent.putExtra("POST_ID",post_id);

                                    startActivity(intent);
                                    finish();
                                }
                            });

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });


            }
        });
        
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedpref.getString("USERID","")!="") {

                }else if(!sharedpref.getString("COR_ID","").equals("")){
                    directoryReference.child(sharedpref.getString("COR_ID","")).child(donor_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                Intent intent = new Intent(DonorProfileActivity.this, SelectedChatActivity.class);
                                intent.putExtra("RECIPIENT_ID",donor_id);
                                intent.putExtra("RECIPIENT_NAME",donor_name);
                                intent.putExtra("CHAT_ID",dataSnapshot.child("chat_id").getValue().toString());
                                startActivity(intent);
                            }else{
                                final String key = chatReference.push().getKey();
                                directoryReference.child(sharedpref.getString("COR_ID","")).child(donor_id).child("chat_id").setValue(key, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        directoryReference.child(sharedpref.getString("COR_ID","")).child(donor_id).child("status").setValue("0", new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                final String new_key = chatReference.child(key).push().getKey();
                                                chatReference.child(key).child(new_key).child("sender").setValue("");
                                                chatReference.child(key).child(new_key).child("message").setValue("");
                                                chatReference.child(key).child(new_key).child("receiver").setValue("");
                                                chatReference.child(key).child(new_key).child("time").setValue(123, new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        Intent intent = new Intent(DonorProfileActivity.this, SelectedChatActivity.class);
                                                        intent.putExtra("RECIPIENT_ID",donor_id);
                                                        intent.putExtra("RECIPIENT_NAME",donor_name);
                                                        intent.putExtra("CHAT_ID",key);
                                                        startActivity(intent);
                                                    }
                                                });


                                            }
                                        });

                                    }
                                });


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(DonorProfileActivity.this);
                    final View mView = getLayoutInflater().inflate(R.layout.dialog_message, null);
                    Button btnSign = mView.findViewById(R.id.btnProceed);
                    Button btnRegister = mView.findViewById(R.id.btnRegister);
                    btnSign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(DonorProfileActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });

                    btnRegister.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(DonorProfileActivity.this, SignupActivity1.class);
                            startActivity(intent);
                        }
                    });

                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }
            
            }
        });

        loadRecipientName();
        loadProfile();
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        
        /*if(id==R.id.menu_message){
            if(sharedpref.getString("USERID","")!="") {

            }else if(sharedpref.getString("COR_ID","")!=""){
                directoryReference.child(sharedpref.getString("COR_ID","")).child(donor_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            Intent intent = new Intent(DonorProfileActivity.this, SelectedChatActivity.class);
                            intent.putExtra("RECIPIENT_ID",donor_id);
                            intent.putExtra("RECIPIENT_NAME",donor_name);
                            intent.putExtra("CHAT_ID",dataSnapshot.getValue().toString());
                            startActivity(intent);
                        }else{
                            final String key = chatReference.push().getKey();
                            directoryReference.child(sharedpref.getString("COR_ID","")).child(donor_id).setValue(key, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    final String new_key = chatReference.child(key).push().getKey();
                                    chatReference.child(key).child(new_key).child("sender").setValue("");
                                    chatReference.child(key).child(new_key).child("message").setValue("");
                                    chatReference.child(key).child(new_key).child("receiver").setValue("");
                                    chatReference.child(key).child(new_key).child("time").setValue(123);

                                    Intent intent = new Intent(DonorProfileActivity.this, SelectedChatActivity.class);
                                    intent.putExtra("RECIPIENT_ID",donor_id);
                                    intent.putExtra("RECIPIENT_NAME",donor_name);
                                    intent.putExtra("CHAT_ID",key);
                                    startActivity(intent);
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(DonorProfileActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.dialog_message, null);
                Button btnSign = mView.findViewById(R.id.btnProceed);
                Button btnRegister = mView.findViewById(R.id.btnRegister);
                btnSign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DonorProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });

                btnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DonorProfileActivity.this, SignupActivity1.class);
                        startActivity(intent);
                    }
                });

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void loadRecipientName(){
        postReference.child(sharedpref.getString("SELECTED_ID","")).child(post_id).child("receiver").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtRecipient.setText(String.valueOf(dataSnapshot.getValue()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void loadProfile() {
        userReference.child(donor_id).child("last_donated").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!String.valueOf(dataSnapshot.getValue()).equals("")) {
                    SimpleDateFormat simpleDate = new SimpleDateFormat("MMMM d, y");
                    SimpleDateFormat checkDate = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    Calendar now = Calendar.getInstance();

                    String checkDateString = checkDate.format(new Date(String.valueOf(dataSnapshot.getValue())));
                    try {
                        c.setTime(checkDate.parse(checkDateString));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //3 months rule
                    c.add(Calendar.MONTH, 3);
                    allowedDate = c.getTimeInMillis();
                    dateNow = now.getTimeInMillis();

                    if (allowedDate >= dateNow) {
                        btnConfirm.setText("Not allowed to donate");
                        btnConfirm.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        donorReference.child(post_id).child(donor_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getValue().equals(false)){
                        btnConfirm.setVisibility(View.VISIBLE);
                    }else{
                        //btnConfirm.setVisibility(View.INVISIBLE);
                        btnConfirm.setEnabled(false);
                        btnConfirm.setText("Donor Already Confirmed");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                    String email = userValue.getEmail();
                    String gender = userValue.getGender();
                    String birthdate = userValue.getBirthdate();
                    String address = userValue.getAddress();
                    String contact = userValue.getContact();
                    name = userValue.getName();
                    String city = userValue.getCity();
                    String bloodtype = userValue.getBloodtype();
                    donation_count = userValue.getDonation_count();
                    String last_donated = userValue.getLast_donated();

                    Picasso.with(getApplicationContext()).load(user_photo).fit().centerCrop().into(imgUser);
                    txtEmail.setText(email);
                    txtGender.setText(gender);
                    txtBirthdate.setText(simpleDate.format(new Date(birthdate)));
                    txtAddress.setText(address);
                    txtContact.setText(contact);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
