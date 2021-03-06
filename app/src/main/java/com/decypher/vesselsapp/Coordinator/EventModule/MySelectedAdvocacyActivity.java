package com.decypher.vesselsapp.Coordinator.EventModule;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.decypher.vesselsapp.Search.SearchAdapterCoordinator;
import com.decypher.vesselsapp.Search.SearchData;
import com.decypher.vesselsapp.Search.SearchEventAdapterCoordinator;
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

public class MySelectedAdvocacyActivity extends AppCompatActivity {

    TextView txtTitle, txtDescription, txtDate, txtTime, txtAddress, txtNoneOne, txtNoneTwo, txtOrg, txtContact, txtInstruction;
    TextView txtRespondentCount, txtConfirmedCount;
    Button btnViewRespondents, btnViewDonations;
    int countRespondent, countConfirmed;
    Button btnIn;
    String date_orig, drive_id, title, description, date, time, address, photo, cor_photo, cor_association, cor_contact, time_from, time_to;
    ImageView imgPost, imgOrg;
    GlobalFunctions globalFunctions;

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference goingReference, userReference, eventsReference;
    String EVENTS_GOING_REFERENCE = "events_going";
    String USER_REFERENCE = "users";
    String EVENTS_REFERENCE = "events";

    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    Boolean blood_stat;

    RecyclerView recyclerDonor;
    SearchData searchValue;
    ArrayList<SearchData> donorList, confirmList;
    SearchEventAdapterCoordinator adapter,confirmAdapter;

    ProgressDialog progDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_selected_advocacy);

        setTitle("Event Details");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progDialog = new ProgressDialog(MySelectedAdvocacyActivity.this);
        progDialog.setMessage("Loading info. .");


        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(getApplicationContext());

        txtNoneOne = (TextView) findViewById(R.id.txtNoneOne);
        txtNoneTwo = (TextView) findViewById(R.id.txtNoneTwo);
        //txtInstruction = (TextView) findViewById(R.id.txtInstruction);

        imgOrg = (ImageView) findViewById(R.id.imgOrg);
        txtOrg = (TextView) findViewById(R.id.txtOrg);
        txtContact = (TextView) findViewById(R.id.txtContactOrg);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtAddress = (TextView) findViewById(R.id.etAddress);
        imgPost = (ImageView) findViewById(R.id.imgPost);


        btnViewDonations = (Button) findViewById(R.id.btnViewDonations);
        btnViewRespondents = (Button) findViewById(R.id.btnViewRespondents);
        txtConfirmedCount = (TextView) findViewById(R.id.txtConfirmedCount);
        txtRespondentCount = (TextView) findViewById(R.id.txtRespondentCount);

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
        //convert
        SimpleDateFormat simpleDate = new SimpleDateFormat("MMMM d, y");
        txtDate.setText(simpleDate.format(new Date(date_orig)));
        txtTime.setText(time);
        Picasso.with(getApplicationContext()).load(photo).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imgPost);

        txtOrg.setText(cor_association);
        txtContact.setText(cor_contact);
        Picasso.with(getApplicationContext()).load(cor_photo).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imgOrg);

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        goingReference = mDatabase.getReference(EVENTS_GOING_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        eventsReference = mDatabase.getReference(EVENTS_REFERENCE);

        recyclerDonor = (RecyclerView) findViewById(R.id.recyclerDonor);
        recyclerDonor.setHasFixedSize(true);
        recyclerDonor.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        donorList = new ArrayList<SearchData>();
        adapter = new SearchEventAdapterCoordinator(MySelectedAdvocacyActivity.this, donorList);
        recyclerDonor.setAdapter(adapter);

        txtNoneOne.setVisibility(View.VISIBLE);

        if(!sharedpref.getString("COR_ID","").equals("")){
            btnViewRespondents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MySelectedAdvocacyActivity.this, ViewAdvocacyAttendee.class);
                    intent.putExtra("EVENT_ID", drive_id);
                    intent.putExtra("EVENT_TITLE", title);
                    startActivity(intent);
                }
            });

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(globalFunctions.isNetworkAvailable()) {
            progDialog.show();
            loadGoing();
        }else{
            Toast.makeText(this, "No internet connection found", Toast.LENGTH_SHORT).show();
        }
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
                Intent intent = new Intent(MySelectedAdvocacyActivity.this, EditAdvocacyActivity.class);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MySelectedAdvocacyActivity.this);
                builder.setMessage("Delete Event?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                eventsReference.child(sharedpref.getString("COR_ID","")).child(drive_id).removeValue();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                            }
                        });
                builder.create();
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
        }
        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadGoing(){

        donorList.clear();
        countConfirmed=0;
        countRespondent=0;
        goingReference.orderByKey().equalTo(drive_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.exists()){
                    txtNoneTwo.setVisibility(View.VISIBLE);
                    txtNoneOne.setVisibility(View.VISIBLE);
                    btnViewRespondents.setVisibility(View.GONE);
                    btnViewDonations.setVisibility(View.GONE);
                }else {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            countRespondent++;
                            txtRespondentCount.setText("( "+String.valueOf(countRespondent)+" )");

                            //limit 5
                            if(countRespondent<6) {
                                loadDonorList(dataSnapshot1.getKey(), false);
                                btnViewRespondents.setVisibility(View.VISIBLE);
                            }
                    }
                }
                progDialog.dismiss();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                adapter.notifyDataSetChanged();
                confirmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
                confirmAdapter.notifyDataSetChanged();
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
                    String contact = searchValue.getContact();

                    info.setUser_id(user_id);
                    info.setName(name);
                    info.setCity(city);
                    info.setBloodtype(bloodtype);
                    info.setDonation_count(donation_count);
                    info.setUser_photo(user_photo);
                    info.setContact(contact);
                    info.setPost_id(drive_id);

                        donorList.add(info);
                        adapter.notifyDataSetChanged();

                }
                if(donorList.size()==0 || donorList.size()<0){
                    txtNoneOne.setVisibility(View.VISIBLE);
                    //txtInstruction.setVisibility(View.INVISIBLE);
                }else{
                    txtNoneOne.setVisibility(View.INVISIBLE);
                    //txtInstruction.setVisibility(View.VISIBLE);
                }
                progDialog.dismiss();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
