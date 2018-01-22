package com.decypher.vesselsapp.Profile;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.decypher.vesselsapp.Home.Users;
import com.decypher.vesselsapp.MyPosts.ViewMyPostActivity;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyProfileActivity extends AppCompatActivity {

    TextView txtName, txtProfile, txtEmail, txtContact, txtAddress, txtCity, txtBirthdate,txtGender, txtType,txtCount;
    ImageView imgEdit, imgUser;
    Button btnConfirm;

    Users userValue;
    ProgressDialog progDialog;

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, donorReference, userReference;
    String POST_REFERENCE = "posts";
    String DONOR_REFERENCE = "posts_donors";
    String USER_REFERENCE = "users";

    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;
    GlobalFunctions globalFunctions;

    public String name;
    public String email;
    public String password;
    public String gender;
    public String bloodtype;
    public String birthdate;
    public String city;
    public String address;
    public String contact;
    public String donation_count;
    public String user_photo;
    public String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        progDialog = new ProgressDialog(MyProfileActivity.this);
        progDialog.setMessage("Loading profile. .");
        progDialog.show();
        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        donorReference = mDatabase.getReference(DONOR_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(getApplicationContext());

        txtName = (TextView) findViewById(R.id.txtAssociation);
        txtEmail = (TextView) findViewById(R.id.txtName);
        txtContact = (TextView) findViewById(R.id.txtContactOrg);
        txtAddress = (TextView) findViewById(R.id.etAddress);
        txtBirthdate = (TextView) findViewById(R.id.txtBirthdate);
        txtGender = (TextView) findViewById(R.id.txtGender);
        txtType = (TextView) findViewById(R.id.txtType);
        txtCount = (TextView) findViewById(R.id.txtCount);

        imgUser = (ImageView) findViewById(R.id.imgUser);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);

        btnConfirm.setVisibility(View.INVISIBLE);


        /*imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("email",email);
                intent.putExtra("gender",gender);
                intent.putExtra("bloodtype",bloodtype);
                intent.putExtra("birthdate",birthdate);
                intent.putExtra("city",city);
                intent.putExtra("address",address);
                intent.putExtra("contact",contact);
                intent.putExtra("donation_count",donation_count);
                intent.putExtra("user_photo",user_photo);
                startActivity(intent);
            }
        });*/

        loadProfile();
    }

    private void loadProfile() {
        userReference.child(sharedpref.getString("USERID","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userValue = dataSnapshot.getValue(Users.class);

                    SimpleDateFormat simpleDate =  new SimpleDateFormat("MMMM d, y");

                     user_photo = userValue.getUser_photo();
                     email = userValue.getEmail();
                     gender = userValue.getGender();
                     birthdate = userValue.getBirthdate();
                     address = userValue.getAddress();
                     contact = userValue.getContact();
                     name = userValue.getName();
                     city = userValue.getCity();
                     bloodtype = userValue.getBloodtype();
                     donation_count = userValue.getDonation_count();

                    Picasso.with(getApplicationContext()).load(user_photo).fit().centerCrop().into(imgUser);
                    txtEmail.setText(email);
                    txtGender.setText(gender);
                    txtBirthdate.setText(simpleDate.format(new Date(birthdate)));
                    txtAddress.setText(address);
                    txtContact.setText(contact);
                    txtName.setText(name);
                    txtType.setText(bloodtype);
                    txtCount.setText(donation_count);
                }
                progDialog.dismiss();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
