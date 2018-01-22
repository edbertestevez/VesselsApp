package com.decypher.vesselsapp.Coordinator.EventModule;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.decypher.vesselsapp.Search.SearchData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriveStatisticsActivity extends AppCompatActivity {

    TextView txtTitle, txtUnconfirmed, txtConfirmed, txtA1, txtA2, txtB1, txtB2, txtO1, txtO2, txtAB1, txtAB2, txtMale, txtFemale;

    GlobalFunctions globalFunctions;
    String GOING_REFERENCE = "drives_going";
    String USER_REFERENCE = "users";
    String DRIVES_REFERENCE = "blood_drives";
    String drive_id, title;

    int ctr_unconfirmed, ctr_confirmed;
    int o1, o2, a1, a2, b1, b2, ab1, ab2;
    int ctr_male, ctr_female;

    ProgressDialog progDialog;

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference goingReference, userReference, drivesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_statistics);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Drive Statistics");

        progDialog = new ProgressDialog(DriveStatisticsActivity.this);
        progDialog.setMessage("Loading statistics. .");

        txtMale = (TextView) findViewById(R.id.txtMale);
        txtFemale = (TextView) findViewById(R.id.txtFemale);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtUnconfirmed = (TextView) findViewById(R.id.txtUnconfirmed);
        txtConfirmed = (TextView) findViewById(R.id.txtConfirmed);
        txtA1 = (TextView) findViewById(R.id.txtA1);
        txtA2 = (TextView) findViewById(R.id.txtA2);
        txtB1 = (TextView) findViewById(R.id.txtB1);
        txtB2 = (TextView) findViewById(R.id.txtB2);
        txtO1 = (TextView) findViewById(R.id.txtO1);
        txtO2 = (TextView) findViewById(R.id.txtO2);
        txtAB1 = (TextView) findViewById(R.id.txtAB1);
        txtAB2 = (TextView) findViewById(R.id.txtAB2);

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        goingReference = mDatabase.getReference(GOING_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        drivesReference = mDatabase.getReference(DRIVES_REFERENCE);

        Bundle extras = getIntent().getExtras();
        title = extras.getString("DRIVE_TITLE");
        drive_id = extras.getString("DRIVE_ID");
        txtTitle.setText(title);

        globalFunctions = new GlobalFunctions(getApplicationContext());

        if(globalFunctions.isNetworkAvailable()) {
            progDialog.show();
            loadConfirmationCount();
        }else{
            Toast.makeText(this, "No internet connection found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadConfirmationCount(){
        ctr_unconfirmed = 0;
        ctr_confirmed=0;

        goingReference.orderByKey().equalTo(drive_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if(dataSnapshot1.getValue().equals(true)){
                        ctr_confirmed++;
                        txtConfirmed.setText(String.valueOf(ctr_confirmed));
                        loadBloodTypeCount(dataSnapshot1.getKey());
                    }else{
                        ctr_unconfirmed++;
                        txtUnconfirmed.setText(String.valueOf(ctr_unconfirmed));
                        loadBloodTypeCount(dataSnapshot1.getKey());
                    }
                }
                progDialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }


    private void loadBloodTypeCount(String user_id){

        userReference.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Toast.makeText(DriveStatisticsActivity.this, dataSnapshot.child("bloodtype").getValue().toString(), Toast.LENGTH_SHORT).show();
                switch (String.valueOf(dataSnapshot.child("bloodtype").getValue())) {
                    case "O+":
                        o1++;
                        txtO1.setText(String.valueOf(o1));
                        break;
                    case "O-":
                        o2++;
                        txtO2.setText(String.valueOf(o2));
                        break;
                    case "A+":
                        a1++;
                        txtA1.setText(String.valueOf(a1));
                        break;
                    case "A-":
                        a2++;
                        txtA2.setText(String.valueOf(a2));
                        break;
                    case "B+":
                        b2++;
                        txtB2.setText(String.valueOf(b2));
                        break;
                    case "B-":
                        b2++;
                        txtB2.setText(String.valueOf(b2));
                        break;
                    case "AB+":
                        ab1++;
                        txtAB1.setText(String.valueOf(ab1));
                        break;
                    case "AB-":
                        ab2++;
                        txtAB2.setText(String.valueOf(ab2));
                        break;
                }

                switch (String.valueOf(dataSnapshot.child("gender").getValue())) {
                    case "Male":
                        ctr_male++;
                        txtMale.setText(String.valueOf(ctr_male));
                        break;
                    case "Female":
                        ctr_female++;
                        txtFemale.setText(String.valueOf(ctr_female));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}