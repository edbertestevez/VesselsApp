package com.decypher.vesselsapp.BloodDrive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.Coordinator.EventModule.EditBloodDriveActivity;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.Others.LoginActivity;
import com.decypher.vesselsapp.Others.SignupActivity1;
import com.decypher.vesselsapp.R;
import com.decypher.vesselsapp.Search.SearchAdapterNotLogged;
import com.decypher.vesselsapp.Search.SearchData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SelectedEventActivity extends AppCompatActivity {

    TextView txtTitle, txtDescription, txtDate, txtTime, txtAddress, txtNoneOne, txtOrg, txtContact;
    TextView txtRespondentCount, txtConfirmedCount;
    Button btnViewRespondents, btnViewDonations;
    Button btnIn;
    String date_orig, drive_id, title, description, date, time, address, photo, cor_photo, cor_association, cor_contact, time_from, time_to;
    ImageView imgPost, imgOrg;
    GlobalFunctions globalFunctions;

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference eventGoingReference, userReference, drivesReference;
    String EVENT_GOING_REFERENCE = "events_going";
    String USER_REFERENCE = "users";
    String DRIVES_REFERENCE = "blood_drives";

    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    Boolean blood_stat;

    RecyclerView recyclerDonor;
    SearchData searchValue;
    ArrayList<SearchData> donorList, confirmList;
    SearchAdapterNotLogged adapter, confirmAdapter;

    ProgressDialog progDialog;
    int countRespondent, countConfirmed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_event);
        setTitle("Event Details");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progDialog = new ProgressDialog(SelectedEventActivity.this);
        progDialog.setMessage("Loading list. .");


        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(getApplicationContext());

        txtNoneOne = (TextView) findViewById(R.id.txtNoneOne);

        txtConfirmedCount = (TextView) findViewById(R.id.txtConfirmedCount);
        txtRespondentCount = (TextView) findViewById(R.id.txtRespondentCount);

        imgOrg = (ImageView) findViewById(R.id.imgOrg);
        txtOrg = (TextView) findViewById(R.id.txtOrg);
        txtContact = (TextView) findViewById(R.id.txtContactOrg);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        //convert
        SimpleDateFormat simpleDate = new SimpleDateFormat("MMMM d, y");
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtAddress = (TextView) findViewById(R.id.etAddress);
        btnIn = (Button) findViewById(R.id.btnIn);
        imgPost = (ImageView) findViewById(R.id.imgPost);

        btnViewDonations = (Button) findViewById(R.id.btnViewDonations);
        btnViewRespondents = (Button) findViewById(R.id.btnViewRespondents);

        Bundle extras = getIntent().getExtras();
        title = extras.getString("DRIVE_TITLE");
        drive_id = extras.getString("DRIVE_ID");
        description = extras.getString("DRIVE_DESC");
        date = extras.getString("DRIVE_DATE");
        time = extras.getString("DRIVE_TIME");
        address = extras.getString("DRIVE_ADDRESS");
        photo = extras.getString("DRIVE_PHOTO");

        date_orig = extras.getString("DATE_ORIG");
        time_from = extras.getString("DRIVE_TIME_FROM");
        time_to = extras.getString("DRIVE_TIME_TO");

        cor_photo = extras.getString("COR_PHOTO");
        cor_association = extras.getString("COR_ASSOCIATION");
        cor_contact = extras.getString("COR_CONTACT");
        blood_stat = extras.getBoolean("MY_BLOOD_DRIVE");

        txtTitle.setText(title);
        txtDescription.setText(description);
        txtAddress.setText(address);
        txtDate.setText(simpleDate.format(new Date(date_orig)));
        txtTime.setText(time);
        Picasso.with(getApplicationContext()).load(photo).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imgPost);

        txtOrg.setText(cor_association);
        txtContact.setText(cor_contact);
        Picasso.with(getApplicationContext()).load(cor_photo).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imgOrg);

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        eventGoingReference = mDatabase.getReference(EVENT_GOING_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        drivesReference = mDatabase.getReference(DRIVES_REFERENCE);

        recyclerDonor = (RecyclerView) findViewById(R.id.recyclerDonor);
        recyclerDonor.setHasFixedSize(true);
        recyclerDonor.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        donorList = new ArrayList<SearchData>();
        adapter = new SearchAdapterNotLogged(SelectedEventActivity.this, donorList);
        recyclerDonor.setAdapter(adapter);

        if(blood_stat==true){
            btnIn.setVisibility(View.INVISIBLE);
            btnIn.setEnabled(false);
        }
        //load org details

        if(sharedpref.getString("COR_ID","").equals("")){
            btnViewDonations.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            btnViewDonations.setText("No access to view all records");
            btnViewDonations.setEnabled(false);
            btnViewDonations.setTextColor((getResources().getColor(R.color.colorAccent)));

            btnViewRespondents.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            btnViewRespondents.setText("No access to view all records");
            btnViewRespondents.setEnabled(false);
            btnViewRespondents.setTextColor((getResources().getColor(R.color.colorAccent)));
        }

        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(globalFunctions.isNetworkAvailable()){
                    if(sharedpref.getString("USERID","").equals("")){
                        //alert dialog
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                        final View mView = getLayoutInflater().inflate(R.layout.dialog_message, null);
                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();
                        Button btnSign = mView.findViewById(R.id.btnProceed);
                        Button btnRegister = mView.findViewById(R.id.btnRegister);
                        TextView txtInfo = mView.findViewById(R.id.txtInfo);
                        txtInfo.setText("You need to sign in to be part of the list for going donors for this blood drive");
                        btnSign.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(SelectedEventActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        btnRegister.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(SelectedEventActivity.this, SignupActivity1.class);
                                startActivity(intent);
                            }
                        });
                    }else{
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                        final View mView = getLayoutInflater().inflate(R.layout.dialog_confirm_drive, null);
                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();
                        TextView txtDonorName = mView.findViewById(R.id.txtDonorName);
                        TextView txtInfo = mView.findViewById(R.id.txtInfo);
                        txtInfo.setText("Once confirmed you're data will be part of the list of going donors for this blood drive. Proceed?");
                        TextView txtTitle = mView.findViewById(R.id.txtTitle);
                        txtTitle.setText("Blood Drive Confirmation");
                        Button btnProceed = mView.findViewById(R.id.btnProceed);
                        Button btnClose = mView.findViewById(R.id.btnClose);
                        txtDonorName.setText(sharedpref.getString("USERNAME",""));

                        btnProceed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //update donation count ni toto
                                mDatabase.getReference("events_going").child(drive_id).child(sharedpref.getString("USERID","")).setValue(false, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        Toast.makeText(SelectedEventActivity.this, "You have been successfully added in the list", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });

                            }
                        });

                        btnClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }
                }else{
                    Toast.makeText(SelectedEventActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


        txtNoneOne.setVisibility(View.VISIBLE);
        loadGoing();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(blood_stat==true) {
            getMenuInflater().inflate(R.menu.menu_edit_delete, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.home:
                finish();
                return true;
            case R.id.menu_edit:
                Intent intent = new Intent(SelectedEventActivity.this, EditBloodDriveActivity.class);
                intent.putExtra("DRIVE_ID",drive_id);
                intent.putExtra("DRIVE_TITLE", title);
                intent.putExtra("DRIVE_DATE", date);
                intent.putExtra("DRIVE_ADDRESS", address);
                intent.putExtra("DRIVE_TIME_FROM", time_from);
                intent.putExtra("DRIVE_TIME_TO", time_to);
                intent.putExtra("DRIVE_DESC", description);
                intent.putExtra("DRIVE_PHOTO", photo);
                intent.putExtra("DATE_ORIG", date_orig);
                startActivity(intent);
                return true;
            case R.id.menu_delete:
                finish();
                drivesReference.child(sharedpref.getString("COR_ID","")).child(drive_id).removeValue();
                return true;
        }
        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadGoing(){
        progDialog.show();
        donorList.clear();
        //confirmList.clear();
        countConfirmed=0;
        countRespondent=0;
        eventGoingReference.orderByKey().equalTo(drive_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.exists()){
                    txtNoneOne.setVisibility(View.VISIBLE);
                    btnViewRespondents.setVisibility(View.GONE);
                    btnViewDonations.setVisibility(View.GONE);
                    progDialog.dismiss();
                }else {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            countRespondent++;
                            txtRespondentCount.setText("( "+String.valueOf(countRespondent)+" )");

                            //limit 5
                            if(countConfirmed<6) {
                                loadDonorList(dataSnapshot1.getKey(), false);
                                btnViewRespondents.setVisibility(View.VISIBLE);
                            }

                    }
                    progDialog.dismiss();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        progDialog.dismiss();
    }

    private void loadDonorList(final String key, final Boolean type){
        userReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    searchValue = dataSnapshot.getValue(SearchData.class);
                    SearchData info = new SearchData();

                    String user_id = key;
                    String name = searchValue.getName();
                    String city = searchValue.getCity();
                    String bloodtype = searchValue.getBloodtype();
                    String donation_count = searchValue.getDonation_count();
                    String user_photo = searchValue.getUser_photo();
                    String contact = searchValue.getGender();

                    if(user_id.equals(sharedpref.getString("USERID",""))){
                        btnIn.setText("Already part of this event");
                        btnIn.setEnabled(false);
                    }

                    info.setUser_id(user_id);
                    info.setName(name);
                    info.setCity(city);
                    info.setBloodtype(bloodtype);
                    info.setDonation_count(donation_count);
                    info.setUser_photo(user_photo);
                    info.setContact(contact);

                        donorList.add(info);
                        adapter.notifyDataSetChanged();

                }
                if(donorList.size()==0 || donorList.size()<0){
                    txtNoneOne.setVisibility(View.VISIBLE);
                }else{
                    txtNoneOne.setVisibility(View.INVISIBLE);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}