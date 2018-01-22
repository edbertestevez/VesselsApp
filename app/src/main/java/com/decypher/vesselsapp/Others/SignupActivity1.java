package com.decypher.vesselsapp.Others;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class SignupActivity1 extends AppCompatActivity {

    Button btnNext;
    EditText etName, etPassword, etContact, etEmail;
    int userCount = 0;
    TextView txtLogin;
    GlobalFunctions globalFunctions;

    ProgressDialog progDialog;

    //FIREBASE
    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        globalFunctions = new GlobalFunctions(getApplicationContext());

        btnNext = (Button) findViewById(R.id.btnNext);
        etName = (EditText) findViewById(R.id.etAddress);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etContact = (EditText) findViewById(R.id.etContact);
        etEmail = (EditText) findViewById(R.id.etEmail);

        txtLogin = (TextView) findViewById(R.id.txtLogin);

        //FIREBASE
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);

        progDialog = new ProgressDialog(SignupActivity1.this);
        progDialog.setMessage("Verifying email . . .");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strName = etName.getText().toString();
                String strPassword = etPassword.getText().toString();
                String strContact = etContact.getText().toString();
                String strEmail = etEmail.getText().toString();
                if(strName.isEmpty() || strPassword.isEmpty() || strContact.isEmpty() || strEmail.isEmpty()) {
                    Toast.makeText(SignupActivity1.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if(globalFunctions.isNetworkAvailable()) {
                        //verify email
                        verifyInput();
                    }else{
                        Toast.makeText(SignupActivity1.this, "No internet connection found", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity1.this, LoginActivity.class);;
                startActivity(intent);
            }
        });
    }

    public void verifyInput(){
        userCount = 0;
        progDialog.show();
        userReference.orderByChild("email").equalTo(etEmail.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String strName = etName.getText().toString();
                String strPassword = etPassword.getText().toString();
                String strContact = etContact.getText().toString();
                String strEmail = etEmail.getText().toString();
                progDialog.dismiss();

                if(dataSnapshot.exists()){
                    Toast.makeText(SignupActivity1.this, "Email is already in use", Toast.LENGTH_SHORT).show();
                }else if(strPassword.length()<6) {
                    Toast.makeText(SignupActivity1.this, "Password should be at least 6 characters.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(SignupActivity1.this, SignupActivity2.class);
                    intent.putExtra("NAME", strName);
                    intent.putExtra("PASSWORD", strPassword);
                    intent.putExtra("CONTACT", strContact);
                    intent.putExtra("EMAIL", strEmail);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
